package com.ricky.totem.item.totem;

import com.ricky.totem.item.ModItems;
import com.ricky.totem.network.ModNetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "totem")
public class TotemEffectHandler {

    /**
     * エンティティが死亡する直前に発動
     * バニラのLivingEntity#dieメソッド内のcheckTotemDeathProtectionと同じタイミング
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getSource();

        // クリエイティブモードやbypassesInvulnerabilityダメージの場合は発動しない
        if (damageSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        // カスタムトーテムアイテムを探す
        ItemStack totemStack = findTotemItem(entity);

        if (!totemStack.isEmpty()) {
            // トーテム効果を発動
            if (activateTotem(entity, totemStack)) {
                // 死亡イベントをキャンセル
                event.setCanceled(true);

                // アイテムを1つ消費
                totemStack.shrink(1);
            }
        }
    }

    /**
     * バニラのトーテム効果を再現
     * LivingEntity#checkTotemDeathProtectionの実装を参考
     */
    public static boolean activateTotem(LivingEntity entity, ItemStack totemStack) {
        if (!totemStack.isEmpty()) {
            // プレイヤーの場合は統計とアドバンスメントを更新
            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(totemStack.getItem()));
                CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, totemStack);
            }

            // 体力を1.0に設定
            entity.setHealth(1.0F);

            // デバフをクリア
            entity.removeAllEffects();

            // トーテム効果を付与（バニラと同じ効果）
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

            // カスタムトーテムのテクスチャ情報をクライアントに送信
            if (entity.level() instanceof ServerLevel serverLevel) {
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(totemStack.getItem());
                if (itemId != null) {
                    // エンティティを追跡している全プレイヤーに送信
                    for (ServerPlayer player : serverLevel.players()) {
                        ModNetworkHandler.sendToPlayer(player, itemId);
                    }
                }
            }

            // トーテムのアニメーション（パーティクル）を表示
            entity.level().broadcastEntityEvent(entity, (byte)35);

            return true;
        }
        return false;
    }

    /**
     * エンティティが指定のアイテムを持っているか確認し、持っていれば取得
     */
    public static ItemStack findTotemItem(LivingEntity entity) {
        // メインハンド、オフハンド、インベントリの順で確認
        for (ItemStack stack : entity.getAllSlots()) {
            if (isTotemItem(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * カスタムトーテムアイテムかどうかを判定
     */
    public static boolean isTotemItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return stack.is(ModItems.TOTEM_WATER_BUCKET.get()) ||
                stack.is(ModItems.TOTEM_COOKED_BEEF.get()) ||
                stack.is(ModItems.TOTEM_SWORD.get()) ||
                stack.is(ModItems.TOTEM_PICKAXE.get());
    }
}