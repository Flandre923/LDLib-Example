package com.example.examplemod.gui;

import com.example.examplemod.ExampleMod;
import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacket;
import com.lowdragmc.lowdraglib2.syncdata.rpc.RPCSender;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * 药水注射器RPC处理类
 * 处理客户端发来的药水注射请求
 */
public class PotionInjectorRPC {

    // 效果持续时间（tick），10秒 = 200 tick
    private static final int EFFECT_DURATION = 200;
    // 效果等级（0 = I级，1 = II级，以此类推）
    private static final int EFFECT_AMPLIFIER = 0;

    /**
     * 处理药水注射RPC请求
     * @param sender RPC发送者信息
     * @param potionId 药水效果ID（如 "minecraft:speed"）
     */
    @RPCPacket("potion_injector:apply")
    public static void onApplyPotion(RPCSender sender, String potionId) {
        if (!sender.isServer()) {
            // 在服务器端执行
            ServerPlayer player = sender.asPlayer();
            if (player == null) {
                ExampleMod.LOGGER.error("PotionInjector: Player is null");
                return;
            }

            // 解析药水效果
            ResourceLocation effectLocation = ResourceLocation.tryParse(potionId);
            if (effectLocation == null) {
                ExampleMod.LOGGER.error("PotionInjector: Invalid potion ID: {}", potionId);
                return;
            }

            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectLocation);
            if (effect == null) {
                ExampleMod.LOGGER.error("PotionInjector: Unknown potion effect: {}", potionId);
                return;
            }

            // 应用药水效果 - 10秒持续时间
            MobEffectInstance effectInstance = new MobEffectInstance(
                    BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect),
                    EFFECT_DURATION,
                    EFFECT_AMPLIFIER,
                    false,  // 是否显示粒子效果
                    true,   // 是否显示图标
                    true    // 是否显示环境效果
            );

            player.addEffect(effectInstance);
            ExampleMod.LOGGER.info("PotionInjector: Applied {} to player {} for 10 seconds", 
                    potionId, player.getName().getString());
        }
    }
}
