package com.example.examplemod.gui.tutorial;

import com.example.examplemod.ExampleMod;
import com.lowdragmc.lowdraglib2.gui.holder.IModularUIHolderMenu;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tutorial 6: ModularUI for Menu
 * 教程 6：用于菜单的 ModularUI
 */
public class Tutorial6Menu extends AbstractContainerMenu implements MenuProvider {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ExampleMod.MODID);

    @SuppressWarnings("unchecked")
    public static final DeferredHolder<MenuType<?>, MenuType<Tutorial6Menu>> TYPE =
            MENU_TYPES.register("tutorial_6", () -> {
                return new MenuType<>((p_38951_, p_38952_) -> new Tutorial6Menu(p_38951_, p_38952_), net.minecraft.world.flag.FeatureFlags.VANILLA_SET);
            });

    private final Player player;
    private final ModularUI modularUI;

    /**
     * Create a Tutorial6Menu
     *
     * @param containerId the container ID
     * @param playerInventory the player's inventory
     */
    public Tutorial6Menu(int containerId, Inventory playerInventory) {
        super(TYPE.get(), containerId);
        this.player = playerInventory.player;

        this.modularUI = Tutorial6UIContainer.createModularUI(player);
        if (this instanceof IModularUIHolderMenu holder) {
            holder.setModularUI(modularUI);
        }
    }

    @Nullable
    public ModularUI getModularUI() {
        return modularUI;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("tutorial.ldlib.title");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId,
                                                                           @NotNull net.minecraft.world.entity.player.Inventory playerInventory,
                                                                           @NotNull Player player) {
        return new Tutorial6Menu(containerId, playerInventory);
    }
}
