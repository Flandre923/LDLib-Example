package com.example.examplemod.item.tutorial;

import com.example.examplemod.gui.tutorial.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Supplier;

/**
 * Tutorial 7 Item - Right-click to open the Tutorial 7 UI
 * 教程 7 物品 - 右键点击打开教程 7 界面
 *
 * This item demonstrates the data binding communication pattern
 * between client screen and server menu.
 */
public class Tutorial8Item extends Item {

    public static final Supplier<Tutorial8Item> INSTANCE = () -> new Tutorial8Item(
            new Properties()
                    .stacksTo(1)
    );

    public Tutorial8Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var level = context.getLevel();

        if (player == null) {
            return InteractionResult.FAIL;
        }


        // Only open the UI on the client side
        if (level.isClientSide()) {
            // Create the ModularUI with improved layout
//            var modularUI = Tutorial3UIContainer.createModularUI(player);
            var modularUI = Tutorial8UIContainer.createModularUI(player);

            // Open the screen with the ModularUI
            Minecraft.getInstance().setScreen(new Tutorial8Screen(modularUI));
        }


        return InteractionResult.CONSUME;
    }
}
