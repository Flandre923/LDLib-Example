package com.example.examplemod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 修复台方块实体
 * 包含一个物品槽，每tick修复物品耐久度
 */
public class RepairStationBlockEntity extends BlockEntity implements Container, MenuProvider {

    private static final int SLOT_COUNT = 1;
    private final SimpleContainer inventory = new SimpleContainer(SLOT_COUNT);
    private int tickCounter = 0;

    public RepairStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModularBlockEntityTypes.REPAIR_STATION.get(), pos, state);
    }

    /**
     * 服务端每tick调用，修复物品耐久度
     */
    public void serverTick() {
        if (level == null) return;
        tickCounter++;
        if (tickCounter >= 20) { // 每20 ticks (约1秒)修复一次
            tickCounter = 0;
            repairItem();
        }
    }

    /**
     * 修复物品耐久度
     */
    private void repairItem() {
        if (!inventory.getItem(0).isEmpty()) {
            var item = inventory.getItem(0);
            var damage = item.getDamageValue();
            var maxDamage = item.getMaxDamage();

            if (damage > 0 && maxDamage > 0) {
                // 每次修复1点耐久度
                item.setDamageValue(Math.max(0, damage - 1));
                inventory.setChanged();
            }
        }
    }

    // Container接口实现
    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return inventory.removeItem(slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }

    // MenuProvider接口实现
    @Override
    public @NotNull net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("block.examplemod.repair_station_block");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId,
                                                                           @NotNull net.minecraft.world.entity.player.Inventory playerInventory,
                                                                           @NotNull Player player) {
        return new RepairStationMenu(containerId, playerInventory, this);
    }

    public void onSlotChanged() {
        setChanged();
    }
}
