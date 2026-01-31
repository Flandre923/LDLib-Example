package com.example.examplemod.item.tutorial;

import com.example.examplemod.gui.tutorial.Tutorial8Screen;
import com.example.examplemod.gui.tutorial.Tutorial8UIContainer;
import com.example.examplemod.thirst.ThirstData;
import com.example.examplemod.thirst.ThirstDataAttachment;
import com.lowdragmc.lowdraglib2.gui.factory.HeldItemUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacketDistributor;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaGutter;
import org.appliedenergistics.yoga.YogaJustify;

import java.util.function.Supplier;

public class Tutorial9Item extends Item implements HeldItemUIMenuType.HeldItemUI {

    public static final Supplier<Tutorial9Item> INSTANCE = () -> new Tutorial9Item(
            new Properties()
                    .stacksTo(1)
    );

    public Tutorial9Item(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player instanceof ServerPlayer serverPlayer){
            HeldItemUIMenuType.openUI(serverPlayer,usedHand);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand),level.isClientSide);
    }
//
//    @Override
//    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
//        var root = new UIElement().layout(layout -> layout
//                .setWidth(100)
//                .setHeight(100)
//                .setPadding(YogaEdge.ALL,4)
//                .setGap(YogaGutter.ALL,4)
//                .setJustifyContent(YogaJustify.CENTER)
//        ).style(style->style.background(Sprites.BORDER));
//
//        root.addChild(new Label().bindDataSource(SupplierDataSource.of(() -> Component.literal(String.valueOf(getProgress())))));
//        root.addChild(new Button().setText("+").setOnClick(e -> addProgress(1)));
//        root.addChild(new Button().setText("-").setOnClick(e -> addProgress(-1)));
//        return new ModularUI(UI.of(root),holder.player);
//    }

    @Override
    public ModularUI createUI(HeldItemUIMenuType.HeldItemUIHolder holder) {
        var root = new UIElement().layout(layout -> layout
                .setWidth(100)
                .setHeight(100)
                .setPadding(YogaEdge.ALL, 4)
                .setGap(YogaGutter.ALL, 2)
                .setJustifyContent(YogaJustify.CENTER)
        ).style(style -> style.backgroundTexture(Sprites.BORDER));
        root.addChild(new Label().setText("Test Item UI"));
        root.addChild(new TextField());
        root.addChild(new Label()
                .bindDataSource(SupplierDataSource.of(()-> Component.literal(String.valueOf(ThirstDataAttachment.getThirstData(holder.player).getThirst())))));
        root.addChild(new Button().setText("+").setOnClick(e -> RPCPacketDistributor.rpcToServer("addThirst",1)));
        root.addChild(new Button().setText("-").setOnServerClick(event -> { }).setOnClick(e -> RPCPacketDistributor.rpcToServer("addThirst",-1)));
        return new ModularUI(UI.of(root), holder.player);
        }
}
