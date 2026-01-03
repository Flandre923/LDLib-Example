package com.example.examplemod.gui;

import com.example.examplemod.block.RepairStationBlockEntity;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.style.Stylesheet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.Collections;

/**
 * 修复台ModularUI界面 - 使用LDLib2构建
 */
public class RepairStationUIContainer {

    /**
     * 创建修复台ModularUI
     * @param player 玩家
     * @param blockEntity 方块实体
     * @return ModularUI实例
     */
    public static ModularUI createUI(Player player, RepairStationBlockEntity blockEntity) {
        // 创建修复槽 - 使用 blockEntity 作为 Container
        Slot repairSlot = new Slot(blockEntity, 0, 80, 35);
        var itemSlot = new ItemSlot(repairSlot);

        // 标题
        var title = new Label();
        title.setText("block.examplemod.repair_station_block");

        // 将标题添加为子元素
        itemSlot.addChild(title);

        // 创建UI - 使用 UI.of() 工厂方法
        var ui = UI.of(itemSlot, Stylesheet.EMPTY);

        // 创建ModularUI
        return new ModularUI(ui, player);
    }
}
