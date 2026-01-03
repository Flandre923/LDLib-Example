package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import net.minecraft.world.entity.player.Player;
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
public class Tutorial3UIContainer {

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
        // add an element to display an image based on a resource location
        var image = new UIElement().layout(layout -> layout.width(80).height(80))
                .style(style -> style.background(
                        SpriteTexture.of("ldlib2:textures/gui/icon.png"))
                );
        root.addChildren(
                // add a label to display text
                new Label().setText("Interaction")
                        // center align text
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),
                image,
                // add a container with the row flex direction
                new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)).addChildren(
                        // a button to rotate the image -45°
                        new Button().setText("-45°")
                                .setOnClick(e -> image.transform(transform ->
                                        transform.rotation(transform.rotation()-45))),
                        new UIElement().layout(layout -> layout.flex(1)), // occupies the remaining space
                        // a button to rotate the image 45°
                        new Button().setText("+45°")
                                .setOnClick(e -> image.transform(transform ->
                                        transform.rotation(transform.rotation() + 45)))
                )
        ).style(style -> style.background(Sprites.BORDER)); // set a background for the root element
        // set padding and gap for children elements
        root.layout(layout -> layout.paddingAll(7).gapAll(5));
        // create a UI
        var ui = UI.of(root);
        // return a modular UI for runtime instance
        return ModularUI.of(ui);
    }


    public static ModularUI createModularUI2() {
        // 1. 创建根容器
        var root = new UIElement();

        // 2. 创建一个用于显示的图片元素，并存储在变量中以便后续控制
        var image = new UIElement().layout(layout -> layout.width(80).height(80))
                .style(style -> style.background(
                        SpriteTexture.of("ldlib2:textures/gui/icon.png"))
                );

        // 3. 向根容器添加子组件
        root.addChildren(
                // 添加标题标签
                new Label().setText("UI Event")
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),

                // 放入上面定义的图片元素
                image,

                // 4. 创建一个水平排列的容器 (ROW)，类似于网页中的 Flexbox 行布局
                new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)).addChildren(

                        // --- 手动实现的“-45°”按钮 (展示 UI 事件系统) ---
                        new UIElement()
                                // 按钮内部嵌套一个标签，adaptiveWidth(true) 让宽度随文字自动撑开
                                .addChild(new Label().setText("-45°").textStyle(textStyle -> textStyle.adaptiveWidth(true)))
                                // 布局设置：内容居中，水平内边距 3
                                .layout(layout -> layout.justifyItems(YogaJustify.CENTER).paddingHorizontal(3))
                                // 设置初始背景样式
                                .style(style -> style.background(Sprites.BORDER1))
                                // 监听鼠标按下事件：通过 transform 属性让图片逆时针旋转 45°
                                .addEventListener(UIEvents.MOUSE_DOWN, e -> image.transform(transform ->
                                        transform.rotation(transform.rotation() - 45)))
                                // 监听鼠标进入事件：实现悬停变色效果（变深色）
                                .addEventListener(UIEvents.MOUSE_ENTER, e ->
                                        e.currentElement.style(style -> style.background(Sprites.BORDER1_DARK)), true)
                                // 监听鼠标离开事件：恢复初始背景颜色
                                .addEventListener(UIEvents.MOUSE_LEAVE, e ->
                                        e.currentElement.style(style -> style.background(Sprites.BORDER1)), true),

                        // 5. 伸缩空间 (Spacer)：flex(1) 会占据行内所有剩余空间，将左右两个按钮推开
                        new UIElement().layout(layout -> layout.flex(1)),

                        // --- 标准按钮实现的“+45°”按钮 ---
                        new Button().setText("+45°")
                                // 直接使用封装好的点击回调，让图片顺时针旋转 45°
                                .setOnClick(e -> image.transform(transform ->
                                        transform.rotation(transform.rotation() + 45)))
                )
        ).style(style -> style.background(Sprites.BORDER)); // 根容器背景

        // 6. 设置根容器的布局：内边距 7，子元素间距 5
        root.layout(layout -> layout.paddingAll(7).gapAll(5));

        // 7. 封装并返回实例
        var ui = UI.of(root);
        return ModularUI.of(ui);
    }

}
