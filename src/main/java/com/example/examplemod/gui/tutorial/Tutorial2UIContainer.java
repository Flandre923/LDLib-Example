package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import net.minecraft.world.entity.player.Player;
import org.appliedenergistics.yoga.YogaFlexDirection;

/**
 * Tutorial 2: Better layout and style
 * 教程 2：更好的布局和样式
 *
 * Fine, it works — but the layout and styling are still not ideal.
 * For example, we want to add padding to the root element,
 * introduce some spacing between components, and center-align the label.
 * Thanks to yoga, we can handle layout code easily.
 *
 * 好的，它能运行——但布局和样式仍然不理想。
 * 例如，我们想给根元素添加内边距，在组件之间添加一些间距，并将标签居中对齐。
 * 借助 yoga，我们可以轻松处理布局代码。
 */
public class Tutorial2UIContainer {

    /**
     * Create a ModularUI with improved layout and styling
     * 创建一个具有更好布局和样式的 ModularUI
     *
     * @param player the player (optional, can be null)
     * @return a configured ModularUI instance
     */
    public static ModularUI createModularUI(Player player) {
        // create a root element
        var root = new UIElement();
        root.addChildren(
                // add a label to display text
                new Label().setText("My First UI")
                        // center align text
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),
                // add a button with text
                new Button().setText("Click Me!"),
                // add an element to display an image based on a resource location
                new UIElement().layout(layout -> layout.width(80).height(80))
                        .style(style -> style.background(
                                SpriteTexture.of("ldlib2:textures/gui/icon.png"))
                        )
        ).style(style -> style.background(Sprites.BORDER)); // set a background for the root element
        // set padding and gap for children elements
        root.layout(layout -> layout.paddingAll(7).gapAll(5));
        // create a UI
        var ui = UI.of(root);
        // return a modular UI for runtime instance
        return ModularUI.of(ui);
    }
}
