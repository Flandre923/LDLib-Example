package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.*;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.style.Stylesheet;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaJustify;

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
public class Tutorial4UIContainer {

    /**
     * Create a ModularUI with improved layout and styling
     * 创建一个具有更好布局和样式的 ModularUI
     *
     * @param player the player (optional, can be null)
     * @return a configured ModularUI instance
     */
    public static ModularUI createModularUI(Player player) {
        var root = new UIElement();
        root.addChildren(
                new Label().setText("LSS example")
                        .lss("horizontal-align", "center"),
                new Button().setText("Click Me!"),
                new UIElement()
                        .lss("width", 80)
                        .lss("height", 80)
                        .lss("background", "sprite(ldlib2:textures/gui/icon.png)")
        );
        root.lss("background", "built-in(ui-gdp:BORDER)");
        root.lss("padding-all", 7);
        root.lss("gap-all", 5);
        var ui = UI.of(root);
        return ModularUI.of(ui);    }


    public static ModularUI createModularUI2() {
        // set root with an ID
        var root = new UIElement().setId("root");
        root.addChildren(
                new Label().setText("LSS example"),
                new Button().setText("Click Me!"),
                // set the element with a class
                new UIElement().addClass("image")
        );
        var lss = """
        // id selector
        #root {
            background: built-in(ui-gdp:BORDER);
            padding-all: 7;
            gap-all: 5;
        }

        // class selector
        .image {
            width: 80;
            height: 80;
            background: sprite(ldlib2:textures/gui/icon.png);
        }

        // element selector
        #root label {
            horizontal-align: center;
        }
        """;
        var stylesheet = Stylesheet.parse(lss);
        // add stylesheets to ui
        var ui = UI.of(root, stylesheet);
        return ModularUI.of(ui);
    }

    public static ModularUI createModularUI3() {
        // 1. 创建根容器并设置固定宽度
        var root = new UIElement();
        root.layout(layout -> layout.width(100));

        // 2. 向根容器中添加多种内置组件（这些组件在不同主题下会有不同的外观）
        root.addChildren(
                new Label().setText("Stylesheets"), // 文本标签
                new Button().setText("Click Me!"), // 按钮
                // 进度条：进度 50%，中间显示文本 "Progress"
                new ProgressBar().setProgress(0.5f).label(label -> label.setText("Progress")),
                new Toggle().setText("Toggle"),     // 开关（复选框）
                new TextField().setText("Text Field"), // 文本输入框
                // 创建一个水平容器 (ROW) 来放置物品槽和流体槽
                new UIElement().layout(layout -> layout.setFlexDirection(YogaFlexDirection.ROW)).addChildren(
                        new ItemSlot().setItem(Items.APPLE.getDefaultInstance()), // 物品槽（显示苹果）
                        new FluidSlot().setFluid(new FluidStack(Fluids.WATER, 1000)) // 流体槽（显示 1000mB 水）
                ),

                // 3. 创建一个选择器 (Selector)，用于在运行时动态切换样式表
                new Selector<ResourceLocation>()
                        // 默认选中 GDP 主题
                        .setSelected(StylesheetManager.GDP, false)
                        // 获取系统中所有已注册的样式表资源路径作为选项
                        .setCandidates(StylesheetManager.INSTANCE.getAllPackStylesheets().stream().toList())
                        // 当玩家在界面上选择不同的选项时触发回调
                        .setOnValueChanged(selected -> {
                            // 获取当前正在运行的 ModularUI 实例
                            var mui = root.getModularUI();
                            if (mui != null) {
                                // 清除当前界面的所有样式表
                                mui.getStyleEngine().clearAllStylesheets();
                                // 加载玩家新选择的样式表包
                                mui.getStyleEngine().addStylesheet(StylesheetManager.INSTANCE.getStylesheetSafe(selected));
                            }
                        })
        );

        // 4. 为根容器添加一个特定的 LSS 类名 "panel_bg"
        // 这个类在内置样式表中通常定义了背景面板的渲染规则
        root.addClass("panel_bg");

        // 5. 创建 UI 实例时，指定默认使用的样式表（此处为 GDP）
        var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP));

        // 6. 返回 ModularUI 实例供运行时使用
        return ModularUI.of(ui);
    }

}
