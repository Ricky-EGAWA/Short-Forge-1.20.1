package com.ricky.totem.block;

import com.ricky.totem.entity.ReverseFallingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 上方向に落ちるブロック
 */
public class ReverseFallingBlock extends FallingBlock {

    public ReverseFallingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        level.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // 上方向のブロックが変わった時にチェック
        if (direction == Direction.UP) {
            level.scheduleTick(pos, this, this.getDelayAfterPlace());
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 上のブロックが空気かどうかチェック
        if (canFallUp(level, pos)) {
            ReverseFallingBlockEntity fallingBlock = ReverseFallingBlockEntity.fall(level, pos, state);
            this.falling(fallingBlock);
        }
    }

    /**
     * 上に落ちれるかチェック
     */
    private static boolean canFallUp(Level level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        return aboveState.isAir() || aboveState.canBeReplaced();
    }

    @Override
    protected int getDelayAfterPlace() {
        return 2;
    }
}
