package com.example.examplemod.item;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.gui.RepairContainer;
import com.example.examplemod.gui.RepairScreen;
import com.example.examplemod.gui.RepairUIContainer;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Supplier;

/**
 * 修复台物品 - 右键点击打开LDLlib2 UI修复界面
 */
public class RepairStationItem extends Item {

    public static final Supplier<RepairStationItem> INSTANCE = () -> new RepairStationItem(
            new Properties()
                    .stacksTo(1)
    );

    public RepairStationItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var level = context.getLevel();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        // 在客户端打开UI
        if (level.isClientSide()) {
            // 创建容器
            var container = new RepairContainer();

            // 创建ModularUI
            var modularUI = RepairUIContainer.createUI(player, container);

            // 打开自定义Screen
            Minecraft.getInstance().setScreen(new RepairScreen(modularUI));
        }

        return InteractionResult.CONSUME;
    }
}
