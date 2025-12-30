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
     * 上方向に広がるようにオーバーライド
     */
    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (!blockState.isAir() && !blockState.canBeReplaced()) {
            return;
        }

        // 上方向への広がりを許可
        if (direction == Direction.UP) {
            level.setBlock(pos, fluidState.createLegacyBlock(), 3);
            return;
        }

        // 水平方向の広がりも許可
        if (direction != Direction.DOWN) {
            super.spreadTo(level, pos, blockState, direction, fluidState);
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        if (!state.isSource()) {
            FluidState fluidState = this.getNewLiquid(level, pos, level.getBlockState(pos));
            int tickDelay = this.getTickDelay(level);

            if (fluidState.isEmpty()) {
                state = fluidState;
                level.setBlock(pos, fluidState.createLegacyBlock(), 3);
            } else if (!fluidState.equals(state)) {
                state = fluidState;
                level.setBlock(pos, fluidState.createLegacyBlock(), 3);
                level.scheduleTick(pos, fluidState.getType(), tickDelay);
            }
        }

        // 上方向に広がる
        this.spreadUpward(level, pos, state);
        // 水平方向にも広がる
        this.spread(level, pos, state);
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
