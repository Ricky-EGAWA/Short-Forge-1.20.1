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
import net.minecraftforge.fluids.FluidType;

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
     * 下方向への広がりを完全に無効化
     */
    @Override
    protected void spread(Level level, BlockPos pos, FluidState state) {
        // 親のspread()を呼び出さない - 下方向への流れを完全に無効化
        // 代わりに上方向と水平方向のみに広がる
        if (!state.isEmpty()) {
            spreadUpward(level, pos, state);
            spreadHorizontally(level, pos, state);
        }
    }

    /**
     * 上方向に水を広げる
     */
    protected void spreadUpward(Level level, BlockPos pos, FluidState fluidState) {
        if (fluidState.isEmpty()) return;

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // 上のブロックが空気か置き換え可能な場合
        if (aboveState.isAir() || aboveState.canBeReplaced()) {
            int newLevel = fluidState.getAmount() - getDropOff(level);
            if (newLevel > 0) {
                FluidState newFluidState = getFlowing(newLevel, false);
                level.setBlock(abovePos, newFluidState.createLegacyBlock(), 3);
                level.scheduleTick(abovePos, this, getTickDelay(level));
            }
        }
    }

    /**
     * 水平方向に水を広げる
     */
    protected void spreadHorizontally(Level level, BlockPos pos, FluidState fluidState) {
        if (fluidState.isEmpty()) return;

        int currentAmount = fluidState.getAmount();
        if (currentAmount <= 1) return;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.isAir() || neighborState.canBeReplaced()) {
                FluidState neighborFluid = level.getFluidState(neighborPos);
                int newLevel = currentAmount - getDropOff(level);

                if (newLevel > 0 && (neighborFluid.isEmpty() || neighborFluid.getAmount() < newLevel)) {
                    FluidState newFluidState = getFlowing(newLevel, false);
                    level.setBlock(neighborPos, newFluidState.createLegacyBlock(), 3);
                    level.scheduleTick(neighborPos, this, getTickDelay(level));
                }
            }
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        if (!state.isSource()) {
            // ソースブロックからの距離に基づいて水位を更新
            int newAmount = calculateNewAmount(level, pos);

            if (newAmount <= 0) {
                // 水が消える
                level.setBlock(pos, state.createLegacyBlock().getFluidState().createLegacyBlock(), 3);
                level.removeBlock(pos, false);
                return;
            } else if (newAmount != state.getAmount()) {
                FluidState newState = getFlowing(newAmount, false);
                level.setBlock(pos, newState.createLegacyBlock(), 3);
                level.scheduleTick(pos, this, getTickDelay(level));
            }
        }

        // 広がる
        this.spread(level, pos, state);
    }

    /**
     * 周囲のブロックから新しい水位を計算（下方向からの供給を見る）
     */
    protected int calculateNewAmount(Level level, BlockPos pos) {
        int maxAmount = 0;

        // 下のブロックをチェック（ソースからの供給）
        BlockPos belowPos = pos.below();
        FluidState belowFluid = level.getFluidState(belowPos);
        if (isSame(belowFluid.getType())) {
            maxAmount = Math.max(maxAmount, belowFluid.getAmount() - getDropOff(level));
        }

        // 水平方向をチェック
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            FluidState neighborFluid = level.getFluidState(neighborPos);
            if (isSame(neighborFluid.getType())) {
                maxAmount = Math.max(maxAmount, neighborFluid.getAmount() - getDropOff(level));
            }
        }

        return maxAmount;
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
