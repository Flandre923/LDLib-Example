package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.*;
import com.lowdragmc.lowdraglib2.gui.ui.style.Stylesheet;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.appliedenergistics.yoga.YogaFlexDirection;

import java.util.concurrent.atomic.AtomicInteger;

public class Tutorial5UIContainer {
    public static ModularUI createModularUI(Player player) {
        // 1. 定义一个“事实来源”：使用 AtomicInteger 来存储数值（0-100）
        // 这样在 Lambda 表达式中可以方便地进行读写操作
        var valueHolder = new AtomicInteger(0);

        var root = new UIElement();
        root.addChildren(
                // 标题标签，水平居中
                new Label().setText("Data Bindings")
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),

                // 2. 创建水平行：包含 [ - 按钮 ] [ 输入框 ] [ + 按钮 ]
                new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)).addChildren(
                        // 减少数值的按钮
                        new Button().setText("-")
                                .setOnClick(e -> {
                                    if (valueHolder.get() > 0) {
                                        valueHolder.decrementAndGet();
                                    }
                                }),

                        // 文本输入框：演示双向绑定
                        new TextField()
                                .setNumbersOnlyInt(0, 100) // 限制只能输入 0-100 的整数
                                .setValue(String.valueOf(valueHolder.get())) // 设置初始值
                                // 【双向绑定 - 从 UI 到 数据】：当玩家在框里输入内容时，更新 valueHolder
                                .bindObserver(value -> valueHolder.set(Integer.parseInt(value)))
                                // 【双向绑定 - 从 数据 到 UI】：当 valueHolder 改变时，自动刷新框内的文字
                                .bindDataSource(SupplierDataSource.of(() -> String.valueOf(valueHolder.get())))
                                .layout(layout -> layout.flex(1)), // 占据中间剩余所有空间

                        // 增加数值的按钮
                        new Button().setText("+")
                                .setOnClick(e -> {
                                    if (valueHolder.get() < 100) {
                                        valueHolder.incrementAndGet();
                                    }
                                })
                ),

                // 3. 演示单向绑定：标签自动显示当前数值
                // 只要 valueHolder 发生变化，Label 的文字会自动刷新
                new Label().bindDataSource(SupplierDataSource.of(() ->
                        Component.literal("Binding: ").append(String.valueOf(valueHolder.get())))),

                // 4. 演示进度条绑定
                new ProgressBar()
                        .setProgress(valueHolder.get() / 100f)
                        // 绑定进度值：数据改变时，进度条长度自动增减
                        .bindDataSource(SupplierDataSource.of(() -> valueHolder.get() / 100f))
                        // 绑定进度条上的文字：同步显示百分比或数值
                        .label(label -> label.bindDataSource(SupplierDataSource.of(() ->
                                Component.literal("Progress: ").append(String.valueOf(valueHolder.get())))))
        ).style(style -> style.background(Sprites.BORDER)); // 设置背景边框

        // 5. 根容器布局设置
        root.layout(layout -> layout.width(100).paddingAll(7).gapAll(5));

        return ModularUI.of(UI.of(root));
    }

}
