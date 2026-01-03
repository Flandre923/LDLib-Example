package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import net.minecraft.world.entity.player.Player;

public class Tutorial1UIContainer {

    /**
     * Create a ModularUI with basic UI elements
     * 创建一个包含基本 UI 元素的 ModularUI
     *
     * @param player the player (optional, can be null)
     * @return a configured ModularUI instance
     */
    public static ModularUI createModularUI(Player player) {
        var root = new UIElement();

        var title = new Label();
        title.setText("My First UI");
        root.addChild(title);

        var button = new Button();
        button.setText("Click Me!");
        root.addChild(button);

        var icon = new UIElement();
        icon.layout(layout -> layout
                .width(80)
                .height(80)
        ).style(style-> style.background(SpriteTexture.of("ldlib2:textures/gui/icon.png")));

        root.addChild(icon);
        root.style(basicStyle -> basicStyle.background(Sprites.BORDER));
        var ui = UI.of(root);

        if (player != null) {
            return ModularUI.of(ui, player);
        }
        return ModularUI.of(ui);
    }
}
