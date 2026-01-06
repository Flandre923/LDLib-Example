package com.example.examplemod.block;

import com.example.examplemod.ExampleMod;
import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaGutter;
import org.appliedenergistics.yoga.YogaJustify;
import org.openjdk.nashorn.internal.objects.annotations.Getter;

/**
 * 修复台方块实体
 * 包含一个物品槽，每tick修复物品耐久度
 */
public class RepairStationBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorge = new FieldManagedStorage(this);

    @Persisted
    public final ItemStackHandler inventory = new ItemStackHandler(1);

    @DescSynced
    public int repairProgress = 0;

    public RepairStationBlockEntity( BlockPos pos, BlockState blockState) {
        super(ModularBlockEntityTypes.REPAIR_STATION.get(), pos, blockState);
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorge;
    }

    public void tick(){
        if(level == null || level.isClientSide) return;

        ItemStack stack = inventory.getStackInSlot(0);

        if(!stack.isEmpty() && stack.isDamageableItem() && stack.getDamageValue() > 0){
            repairProgress ++;

            if(repairProgress >= 100){
                stack.setDamageValue(stack.getDamageValue() -1);
                repairProgress = 0;
            }
        }else{
            if(repairProgress != 0){
                repairProgress = 0;
            }
        }
    }


    public String computeShowDamageValue(){
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty() || !stack.isDamaged()) {
            return "耐久度: --/--";
        }
        // 计算当前耐久和最大耐久
        int current = stack.getMaxDamage() - stack.getDamageValue();
        int max = stack.getMaxDamage();
        return "耐久度: " + current + " / " + max;
    }


    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder){

        var root = new UIElement().layout(layout -> layout
                .setPadding(YogaEdge.ALL,4)
                .setGap(YogaGutter.ALL,2)
                .setJustifyContent(YogaJustify.CENTER)
        ).addClass("panel_bg");

        root.addChild(new ItemSlot().bind(inventory,0));

        root.addChild(new ProgressBar()
                .bindDataSource(SupplierDataSource.of(()->repairProgress/100f))
                .setProgress(0.5f)
                .label(label->label.setText(""))
        );

        root.addChild(new Label()
                .bindDataSource(SupplierDataSource.of(()-> Component.literal(computeShowDamageValue())))

        );

        var invs = new UIElement().addChildren(
                new com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots()
        );
        root.addChild(invs);

        return new ModularUI(UI.of(root),holder.player);
    }


}
