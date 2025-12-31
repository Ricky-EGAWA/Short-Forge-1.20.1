package com.ricky.totem.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 上方向に落下するFallingBlockEntity
 */
public class ReverseFallingBlockEntity extends FallingBlockEntity {

    private BlockState blockState = Blocks.SAND.defaultBlockState();

    public ReverseFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static ReverseFallingBlockEntity fall(Level level, BlockPos pos, BlockState state) {
        ReverseFallingBlockEntity entity = new ReverseFallingBlockEntity(
                ModEntities.REVERSE_FALLING_BLOCK.get(), level);
        entity.blockState = state;
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        entity.xo = pos.getX() + 0.5;
        entity.yo = pos.getY();
        entity.zo = pos.getZ() + 0.5;
        entity.setStartPos(pos);

        // ブロックを削除
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.addFreshEntity(entity);

        return entity;
    }

    @Override
    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
            return;
        }

        Block block = this.blockState.getBlock();
        this.time++;

        // 上向きの重力を適用（通常は下向きに-0.04）
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (!this.level().isClientSide) {
            BlockPos currentPos = this.blockPosition();
            BlockPos abovePos = currentPos.above();

            // 上にブロックがあるか、一定時間経過したら着地
            boolean shouldLand = false;
            BlockPos landingPos = currentPos;

            if (!this.level().getBlockState(abovePos).isAir() &&
                !this.level().getBlockState(abovePos).canBeReplaced()) {
                // 上にブロックがある場合、現在位置に着地
                shouldLand = true;
                landingPos = currentPos;
            } else if (this.onGround() && this.getDeltaMovement().y >= 0) {
                // 天井に当たった場合
                shouldLand = true;
                landingPos = currentPos;
            }

            if (shouldLand) {
                BlockState stateAtPos = this.level().getBlockState(landingPos);

                if (stateAtPos.isAir() || stateAtPos.canBeReplaced()) {
                    this.level().setBlock(landingPos, this.blockState, 3);
                } else {
                    // 置けない場合はアイテムとしてドロップ
                    this.spawnAtLocation(block);
                }
                this.discard();
                return;
            }

            // 600ティック（30秒）以上経過したら消滅
            if (this.time > 600) {
                this.spawnAtLocation(block);
                this.discard();
                return;
            }
        }

        // 空気抵抗
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }

    @Override
    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        tag.putInt("Time", this.time);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.blockState = NbtUtils.readBlockState(
                this.level().holderLookup(Registries.BLOCK), tag.getCompound("BlockState"));
        this.time = tag.getInt("Time");
    }
}
