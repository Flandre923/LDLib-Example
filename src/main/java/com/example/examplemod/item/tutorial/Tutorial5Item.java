package com.example.examplemod.item.tutorial;

import com.example.examplemod.gui.tutorial.Tutorial4Screen;
import com.example.examplemod.gui.tutorial.Tutorial4UIContainer;
import com.example.examplemod.gui.tutorial.Tutorial5UIContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Supplier;

/**
 * Tutorial 2 Item - Right-click to open the Tutorial 2 UI
 * 教程 2 物品 - 右键点击打开教程 2 界面
 */
public class Tutorial5Item extends Item {

    public static final Supplier<Tutorial5Item> INSTANCE = () -> new Tutorial5Item(
            new Properties()
                    .stacksTo(1)
    );

    public Tutorial5Item(Properties properties) {
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
            var modularUI = Tutorial5UIContainer.createModularUI(player);

            // Open the screen with the ModularUI
            Minecraft.getInstance().setScreen(new Tutorial4Screen(modularUI));
        }

        return InteractionResult.CONSUME;
    }
}
