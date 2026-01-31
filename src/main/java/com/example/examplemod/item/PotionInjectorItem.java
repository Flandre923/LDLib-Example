package com.example.examplemod.item;

import com.example.examplemod.gui.PotionInjectorScreen;
import com.example.examplemod.gui.PotionInjectorUIContainer;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * 药水注射器物品 - 右键打开UI界面选择药水效果并注射
 */
public class PotionInjectorItem extends Item {

    public static final Supplier<PotionInjectorItem> INSTANCE = () -> new PotionInjectorItem(
            new Properties()
                    .stacksTo(1)
    );

    public PotionInjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            var modularUI = PotionInjectorUIContainer.createModularUI(player);
            Minecraft.getInstance().setScreen(new PotionInjectorScreen(modularUI));
        }
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
}
