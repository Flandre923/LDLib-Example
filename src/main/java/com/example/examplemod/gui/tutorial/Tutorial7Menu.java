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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tutorial 7: Menu with server-side data for communication with Screen
 * 教程 7：包含服务端数据的菜单，用于与屏幕通信
 *
 * This menu holds data on the server side and provides data bindings
 * for seamless communication between the client screen and server menu.
 */
public class Tutorial7Menu extends AbstractContainerMenu implements MenuProvider {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ExampleMod.MODID);

    @SuppressWarnings("unchecked")
    public static final DeferredHolder<MenuType<?>, MenuType<Tutorial7Menu>> TYPE =
            MENU_TYPES.register("tutorial_7", () -> {
                return new MenuType<>((p_38951_, p_38952_) -> new Tutorial7Menu(p_38951_, p_38952_), net.minecraft.world.flag.FeatureFlags.VANILLA_SET);
            });

    // Server-side data
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);
    private final FluidTank fluidTank = new FluidTank(2000);
    private boolean boolValue = true;
    private String stringValue = "hello";
    private float numberValue = 0.5f;

    private final Player player;
    private ModularUI modularUI;

    /**
     * Create a Tutorial7Menu
     *
     * @param containerId the container ID
     * @param playerInventory the player's inventory
     */
    public Tutorial7Menu(int containerId, Inventory playerInventory) {
        super(TYPE.get(), containerId);
        this.player = playerInventory.player;

        // Initialize fluid tank with empty fluid
        this.fluidTank.setFluid(FluidStack.EMPTY);

        // Create the ModularUI with data bindings
        this.modularUI = Tutorial7UIContainer.createModularUI(this);

        if (this instanceof IModularUIHolderMenu holder) {
            holder.setModularUI(modularUI);
        }
    }

    // Getters for data bindings
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public boolean isBool() {
        return boolValue;
    }

    public void setBool(boolean value) {
        this.boolValue = value;
    }

    public String getString() {
        return stringValue;
    }

    public void setString(String value) {
        this.stringValue = value;
    }

    public float getNumber() {
        return numberValue;
    }

    public void setNumber(float value) {
        this.numberValue = value;
    }

    public Player getPlayer() {
        return player;
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

    // MenuProvider implementation
    @Override
    public @NotNull net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("tutorial.ldlib.data_binding");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId,
                                                                           @NotNull net.minecraft.world.entity.player.Inventory playerInventory,
                                                                           @NotNull Player player) {
        return new Tutorial7Menu(containerId, playerInventory);
    }
}
