package com.example.examplemod.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.appliedenergistics.yoga.*;

public class MyFurnBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorge = new FieldManagedStorage(this);

    @Persisted
    public final ItemStackHandler inputSlot = new ItemStackHandler(1);
    @Persisted
    public final ItemStackHandler fuelSlot = new ItemStackHandler(1);
    @Persisted
    public final ItemStackHandler outputSlot = new ItemStackHandler(1);



    public MyFurnBlockEntity( BlockPos pos, BlockState blockState) {
        super(ModularBlockEntityTypes.MY_FURN.get(), pos, blockState);
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorge;
    }


    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder){

        var root = new UIElement().addClass("panel_bg");

        root.style(basicStyle -> basicStyle.background(Sprites.BORDER))
                .layout(layoutStyle -> layoutStyle
                        .setGap(YogaGutter.ALL,2)
                        .setPadding(YogaEdge.ALL,4));

        var container = new UIElement();
        var left = new UIElement();
        var middle = new UIElement();
        var right = new UIElement();

        container.addChildren(left,middle,right).layout(layoutStyle -> layoutStyle
                .flexDirection(YogaFlexDirection.ROW)
                .setPadding(YogaEdge.ALL,4)
                .setGap(YogaGutter.ALL,2)
                .setJustifyContent(YogaJustify.CENTER)
                .setAlignItems(YogaAlign.CENTER)      // 垂直居中 (让左中右三块内容在中间对齐)
        );

        left.addChildren(
                new ItemSlot().bind(inputSlot,0),
                new UIElement().layout(layoutStyle -> layoutStyle.setWidth(14).setHeight(14)),
                new ItemSlot().bind(fuelSlot,0)
        ).layout(layout -> layout
                .setPadding(YogaEdge.ALL,4)
                .setGap(YogaGutter.ALL,2)
                .setAlignItems(YogaAlign.CENTER)         // 内部居中
        );

        middle.addChildren(
                new ProgressBar().setProgress(0.5f).label(label -> label.setText("")).layout(layout-> layout.setWidth(50).setHeight(20))
        );

        right.addChildren(
                new ItemSlot().bind(outputSlot,0)
        );

        root.addChildren(container);

        var invs = new UIElement().addChildren(
                new com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots()
        );
        root.addChild(invs);

        return new ModularUI(UI.of(root),holder.player);
    }




}
