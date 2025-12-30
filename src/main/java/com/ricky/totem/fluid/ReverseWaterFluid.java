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
import net.minecraft.world.level.block.Blocks;
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
        // 下方向からの置き換えのみ許可しない
        return direction != Direction.DOWN && !isSame(fluid);
    }

    /**
     * 下方向への広がりを完全に無効化し、上方向のみに広がる
     */
    @Override
    protected void spread(Level level, BlockPos pos, FluidState state) {
        // 何もしない - tick()で処理する
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        if (!level.isClientSide) {
            if (!state.isSource()) {
                // 水源からの供給をチェック
                int newAmount = getNewAmountFromSource(level, pos);

                if (newAmount <= 0) {
                    // 水源がなくなったので消える
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    return;
                } else if (newAmount != state.getAmount()) {
                    // 水位を更新
                    FluidState newState = getFlowing(newAmount, false);
                    level.setBlock(pos, newState.createLegacyBlock(), 3);
                    level.scheduleTick(pos, this, getTickDelay(level));
                }
            }

            // 上方向にのみ広がる
            spreadUpwardOnly(level, pos, state);
        }
    }

    /**
     * 下のブロックから水源を探して新しい水位を計算
     * 水は下から上に流れるので、下にある水から供給を受ける
     */
    protected int getNewAmountFromSource(Level level, BlockPos pos) {
        // 下のブロックをチェック（主な供給源）
        BlockPos belowPos = pos.below();
        FluidState belowFluid = level.getFluidState(belowPos);

        if (isSame(belowFluid.getType())) {
            // 下にある水から1減らした値
            return belowFluid.getAmount() - getDropOff(level);
        }

        // 下に水がない場合は0（消える）
        return 0;
    }

    /**
     * 上方向にのみ水を広げる
     */
    protected void spreadUpwardOnly(Level level, BlockPos pos, FluidState fluidState) {
        if (fluidState.isEmpty()) return;

        int currentAmount = fluidState.getAmount();
        int newLevel = currentAmount - getDropOff(level);

        if (newLevel <= 0) return;

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        FluidState aboveFluid = level.getFluidState(abovePos);

        // 上のブロックが空気か置き換え可能で、まだ水がないか水位が低い場合
        if ((aboveState.isAir() || aboveState.canBeReplaced()) &&
            (aboveFluid.isEmpty() || (isSame(aboveFluid.getType()) && aboveFluid.getAmount() < newLevel))) {

            FluidState newFluidState = getFlowing(newLevel, false);
            level.setBlock(abovePos, newFluidState.createLegacyBlock(), 3);
            level.scheduleTick(abovePos, this, getTickDelay(level));
        }
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
