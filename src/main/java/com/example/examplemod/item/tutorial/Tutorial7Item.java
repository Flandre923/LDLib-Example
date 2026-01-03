package com.example.examplemod.item.tutorial;

import com.example.examplemod.gui.tutorial.Tutorial7Menu;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
public class Tutorial7Item extends Item {

    public static final Supplier<Tutorial7Item> INSTANCE = () -> new Tutorial7Item(
            new Properties()
                    .stacksTo(1)
    );

    public Tutorial7Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var level = context.getLevel();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        // Open the menu on the server side
        if (!level.isClientSide()) {
            player.openMenu(new Tutorial7Menu(0, player.getInventory()));
        }

        return InteractionResult.CONSUME;
    }
}
