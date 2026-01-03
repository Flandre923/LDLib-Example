package com.example.examplemod.item.tutorial;

import com.example.examplemod.gui.tutorial.Tutorial6Menu;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Supplier;

/**
 * Tutorial 6 Item - Right-click to open the Tutorial 6 Menu UI
 * 教程 6 物品 - 右键点击打开教程 6 菜单界面
 */
public class Tutorial6Item extends Item {

    public static final Supplier<Tutorial6Item> INSTANCE = () -> new Tutorial6Item(
            new Properties()
                    .stacksTo(1)
    );

    public Tutorial6Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var level = context.getLevel();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            player.openMenu(new Tutorial6Menu(0, player.getInventory()));
        }

        return InteractionResult.CONSUME;
    }
}
