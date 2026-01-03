package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import net.minecraft.world.entity.player.Player;

public class Tutorial6UIContainer {
    public static ModularUI createModularUI(Player player) {
        var root = new UIElement();
        root.addChildren(
                new Label().setText("Menu UI"),
                new InventorySlots()
        ).addClass("panel_bg");

        var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP));
        return ModularUI.of(ui, player);
    }

}
