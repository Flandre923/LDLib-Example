package com.example.examplemod.item.tutorial;

import com.example.examplemod.gui.tutorial.Tutorial1Screen;
import com.example.examplemod.gui.tutorial.Tutorial1UIContainer;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Supplier;

/**
 * Tutorial 1 Item - Right-click to open the Tutorial 1 UI
 * 教程 1 物品 - 右键点击打开教程 1 界面
 *
 * This item demonstrates how to trigger a ModularUI from an item.
 * This is the entry point for testing Tutorial 1.
 *
 * 此物品演示了如何从物品触发 ModularUI。
 * 这是测试教程 1 的入口点。
 */
public class Tutorial1Item extends Item {

    public static final Supplier<Tutorial1Item> INSTANCE = () -> new Tutorial1Item(
            new Properties()
                    .stacksTo(1)
    );

    public Tutorial1Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var level = context.getLevel();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        // Only open the UI on the client side
        // 仅在客户端打开 UI
        if (level.isClientSide()) {
            // Create the ModularUI following the tutorial pattern
            // 按照教程模式创建 ModularUI
            var modularUI = Tutorial1UIContainer.createModularUI(player);

            // Open the screen with the ModularUI
            // 打开包含 ModularUI 的屏幕
            Minecraft.getInstance().setScreen(new Tutorial1Screen(modularUI));
        }

        return InteractionResult.CONSUME;
    }
}
