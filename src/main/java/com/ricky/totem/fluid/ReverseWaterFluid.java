package com.ricky.totem.fluid;

import com.ricky.totem.block.ModBlocks;
import com.ricky.totem.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidType;

/**
 * 通常の水の挙動を上下反転させた流体
 * - 通常: 下に落ちる → 逆: 上に上がる
 * - 通常: 上から供給を受ける → 逆: 下から供給を受ける
 */
public abstract class ReverseWaterFluid extends FlowingFluid {

    @Override
    public FluidType getFluidType() {
        return ModFluidTypes.REVERSE_WATER_FLUID_TYPE.get();
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_REVERSE_WATER.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_REVERSE_WATER.get();
    }

    @Override
    public Item getBucket() {
        return ModItems.REVERSE_WATER_BUCKET.get();
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.REVERSE_WATER_BLOCK.get().defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.UP && !isSame(fluid);
    }

    /**
     * 流体の新しい状態を計算（通常の水のgetNewLiquidを上下反転）
     * 通常: 上にソースがあれば落下水（レベル8）
     * 逆さ: 下にソースがあれば上昇水（レベル8）
     */
    @Override
    protected FluidState getNewLiquid(Level level, BlockPos pos, BlockState blockState) {
        int maxLevel = 0;
        int sourceCount = 0;

        // 水平方向の隣接ブロックをチェック
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            FluidState neighborFluid = neighborState.getFluidState();

            if (isSame(neighborFluid.getType())) {
                if (neighborFluid.isSource()) {
                    sourceCount++;
                }
                int neighborAmount = neighborFluid.getAmount();
                if (neighborAmount > maxLevel) {
                    maxLevel = neighborAmount;
                }
            }
        }

        // 下のブロックをチェック（通常の水では上をチェック）
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        FluidState belowFluid = belowState.getFluidState();

        if (isSame(belowFluid.getType())) {
            // 下に同じ流体があれば、上昇水としてレベル8になる（通常の落下水と同じ）
            return getFlowing(8, true);
        }

        // 水平方向からの供給
        int newLevel = maxLevel - getDropOff(level);
        if (newLevel <= 0) {
            return Fluids.EMPTY.defaultFluidState();
        }

        return getFlowing(newLevel, false);
    }

    /**
     * 流体を広げる（通常の水のspreadを上下反転）
     */
    @Override
    protected void spread(Level level, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isEmpty()) {
            BlockState blockState = level.getBlockState(pos);

            // 上方向への広がりをチェック（通常の水では下方向）
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (canSpreadTo(level, pos, blockState, Direction.UP, abovePos, aboveState, level.getFluidState(abovePos), fluidState.getType())) {
                // 上に広がれる場合、レベル8で広がる（上昇水）
                spreadTo(level, abovePos, aboveState, Direction.UP, getFlowing(8, true));
            } else if (fluidState.isSource() || !isWaterHole(level, fluidState.getType(), pos, blockState, abovePos, aboveState)) {
                // 上に広がれない場合、水平方向に広がる
                spreadToSides(level, pos, fluidState, blockState);
            }
        }
    }

    /**
     * 水平方向に広げる
     */
    private void spreadToSides(Level level, BlockPos pos, FluidState fluidState, BlockState blockState) {
        int currentLevel = fluidState.getAmount() - getDropOff(level);
        if (currentLevel <= 0) {
            return;
        }

        // 最も良い広がり先を見つける
        java.util.Map<Direction, FluidState> spreadMap = getSpread(level, pos, blockState);

        for (java.util.Map.Entry<Direction, FluidState> entry : spreadMap.entrySet()) {
            Direction direction = entry.getKey();
            FluidState newFluidState = entry.getValue();
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (canSpreadTo(level, pos, blockState, direction, neighborPos, neighborState, level.getFluidState(neighborPos), newFluidState.getType())) {
                spreadTo(level, neighborPos, neighborState, direction, newFluidState);
            }
        }
    }

    /**
     * 上方向への穴（上昇できる場所）があるかチェック
     * 通常の水のisWaterHoleを上下反転
     */
    private boolean isWaterHole(BlockGetter level, Fluid fluid, BlockPos pos, BlockState blockState, BlockPos abovePos, BlockState aboveState) {
        if (!canPassThroughWall(Direction.UP, level, pos, blockState, abovePos, aboveState)) {
            return false;
        }
        return aboveState.getFluidState().getType().isSame(fluid) || canHoldFluid(level, abovePos, aboveState, fluid);
    }

    /**
     * 広がり先を計算
     */
    @Override
    protected java.util.Map<Direction, FluidState> getSpread(Level level, BlockPos pos, BlockState blockState) {
        int currentLevel = level.getFluidState(pos).getAmount();
        java.util.Map<Direction, FluidState> map = new java.util.HashMap<>();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (canPassThroughWall(direction, level, pos, blockState, neighborPos, neighborState) && canHoldFluid(level, neighborPos, neighborState, this)) {
                // 上方向への最短経路を探す（通常の水では下方向）
                int distance = getSlopeDistance(level, neighborPos, 1, direction.getOpposite(), neighborState, pos);
                int newLevel;

                if (distance < getSlopeFindDistance(level)) {
                    newLevel = currentLevel - getDropOff(level);
                } else {
                    newLevel = currentLevel - getDropOff(level);
                }

                if (newLevel > 0) {
                    map.put(direction, getFlowing(newLevel, false));
                }
            }
        }

        return map;
    }

    /**
     * 上方向への傾斜距離を計算（通常の水では下方向）
     */
    protected int getSlopeDistance(LevelReader level, BlockPos pos, int distance, Direction excludeDirection, BlockState state, BlockPos sourcePos) {
        int minDistance = 1000;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction == excludeDirection) continue;

            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (!canPassThroughWall(direction, level, pos, state, neighborPos, neighborState) || !canHoldFluid(level, neighborPos, neighborState, this)) {
                continue;
            }

            // 上に穴があるかチェック（通常の水では下をチェック）
            BlockPos abovePos = neighborPos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (isWaterHole(level, this, neighborPos, neighborState, abovePos, aboveState)) {
                return distance;
            }

            if (distance < getSlopeFindDistance(level)) {
                int newDistance = getSlopeDistance(level, neighborPos, distance + 1, direction.getOpposite(), neighborState, sourcePos);
                if (newDistance < minDistance) {
                    minDistance = newDistance;
                }
            }
        }

        return minDistance;
    }

    private boolean canPassThroughWall(Direction direction, BlockGetter level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState) {
        return !state.isFaceSturdy(level, pos, direction) && !neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite());
    }

    private boolean canHoldFluid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return state.canBeReplaced(fluid) || state.isAir();
    }

    protected boolean canSpreadTo(Level level, BlockPos fromPos, BlockState fromState, Direction direction, BlockPos toPos, BlockState toState, FluidState toFluidState, Fluid fluid) {
        return toFluidState.canBeReplacedWith(level, toPos, fluid, direction) &&
               canPassThroughWall(direction, level, fromPos, fromState, toPos, toState) &&
               canHoldFluid(level, toPos, toState, fluid);
    }

    public static class Flowing extends ReverseWaterFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends ReverseWaterFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
