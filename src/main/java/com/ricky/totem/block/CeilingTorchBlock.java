package com.ricky.totem.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 天井に設置できる松明
 * 通常の松明を上下逆さまにした見た目
 */
public class CeilingTorchBlock extends Block {
    // 天井松明の当たり判定（上下逆転）
    protected static final VoxelShape AABB = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 16.0D, 10.0D);

    protected final ParticleOptions flameParticle;

    public CeilingTorchBlock(Properties properties) {
        super(properties);
        this.flameParticle = ParticleTypes.FLAME;
    }

    public CeilingTorchBlock(Properties properties, ParticleOptions particle) {
        super(properties);
        this.flameParticle = particle;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // 天井（上向きの面）にのみ設置可能
        if (context.getClickedFace() == Direction.DOWN) {
            BlockPos abovePos = context.getClickedPos().above();
            if (context.getLevel().getBlockState(abovePos).isFaceSturdy(context.getLevel(), abovePos, Direction.DOWN)) {
                return this.defaultBlockState();
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // 上にブロックがあるかチェック
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        return aboveState.isFaceSturdy(level, abovePos, Direction.DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // 上のブロックがなくなったら壊れる
        if (direction == Direction.UP && !this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // パーティクルを下向きに表示（逆さまなので炎は下側に）
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.4D;  // 下の方に配置
        double z = (double) pos.getZ() + 0.5D;

        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(this.flameParticle, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
