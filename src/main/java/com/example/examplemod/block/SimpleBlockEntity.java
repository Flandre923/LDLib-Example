package com.example.examplemod.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.appliedenergistics.yoga.YogaAlign;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaGutter;
import org.appliedenergistics.yoga.YogaJustify;

/**
 * 简单方块实体示例
 * 包含一个计数器，右键点击按钮可以增加计数
 */
public class SimpleBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @DescSynced
    public int counter = 0;

    public SimpleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModularBlockEntityTypes.SIMPLE.get(), pos, blockState);
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }

    /**
     * 服务器端每tick执行
     */
    public void tick() {
        // 可以在这里添加游戏逻辑
    }

    /**
     * RPC方法：增加计数器
     * 客户端调用，服务器执行
     */
    public void incrementCounter() {
        counter++;
    }

    /**
     * 创建方块的UI
     */
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        var root = new UIElement()
                .layout(layout -> layout
                        .setPadding(YogaEdge.ALL, 10)
                        .setGap(YogaGutter.ALL, 8)
                        .setJustifyContent(YogaJustify.CENTER)
                        .alignItems(YogaAlign.CENTER)
                );

        // 标题
        root.addChild(new Label()
                .setText("Simple Block Entity"));

        // 计数器显示
        var counterLabel = new Label();
        counterLabel.setText("Count: " + counter);
        root.addChild(counterLabel);

        // 增加按钮
        var button = new Button()
                .setText("Add +1")
                .setOnClick(e -> {
                    // 调用RPC方法增加计数器
                    incrementCounter();
                });
        root.addChild(button);

        return new ModularUI(UI.of(root), holder.player);
    }
}
