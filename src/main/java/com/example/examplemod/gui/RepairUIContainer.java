package com.example.examplemod.gui;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.texture.ResourceTexture;
import net.minecraft.world.entity.player.Player;

/**
 * 使用LDLib2 UI系统构建的物品修复界面
 */
public class RepairUIContainer {

    /**
     * 创建物品修复界面的ModularUI
     * @param player 玩家
     * @param container 物品容器
     * @return ModularUI实例
     */
    public static ModularUI createUI(Player player, RepairContainer container) {
        // 创建根元素
        var root = new UIElement();
        root.setId("root");

        // 设置布局
        root.layout(layout -> layout
                .width(200)
                .height(150)
        );

        // 设置背景
        root.lss("background-image", "minecraft:textures/gui/container/generic_54.png");

        // 标题
        var title = new Label()
                .setText("item_repair.title")
                .setId("title");
        root.addChild(title);

        // 修复按钮
        var repairButton = new Button()
                .setId("repairButton");
        repairButton.addEventListener(UIEvents.MOUSE_DOWN, event -> {
            container.tryRepairItem();
        });
        repairButton.lss("background-color", "#4CAF50");
        repairButton.addChild(new Label()
                .setText("item_repair.repair"));
        root.addChild(repairButton);

        // 创建UI
        var ui = UI.of(root);

        // 创建ModularUI（客户端）
        return ModularUI.of(ui);
    }
}
