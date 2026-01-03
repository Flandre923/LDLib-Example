package com.example.examplemod.block;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 修复台菜单
 * 用于服务端同步数据
 */
public class RepairStationMenu extends AbstractContainerMenu {

    private static final int SLOT_COUNT = 1;
    private static final int PLAYER_INVENTORY_START = 1 + SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 26; // 27 slots
    private static final int PLAYER_HOTBAR_END = PLAYER_INVENTORY_END + 9; // +9 = 37 total

    private final RepairStationBlockEntity blockEntity;
    private final Player player;

    public RepairStationMenu(int containerId, Inventory playerInventory, RepairStationBlockEntity blockEntity) {
        super(ExampleMod.REPAIR_STATION_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.player = playerInventory.player;

        Container inventory = blockEntity != null ? blockEntity : new SimpleInventory(SLOT_COUNT);

        // 修复台物品槽 (在GUI中间)
        this.addSlot(new Slot(inventory, 0, 80, 35));

        // 玩家物品栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 玩家快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public RepairStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null);
    }

    public RepairStationBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (slotIndex < PLAYER_INVENTORY_START) {
                // 从修复槽移动到玩家背包
                if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移动到修复槽
                if (!this.moveItemStackTo(itemstack1, 0, PLAYER_INVENTORY_START, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    /**
     * 简单的虚拟容器，用于客户端同步
     */
    private static class SimpleInventory implements Container {
        private final ItemStack[] items;

        public SimpleInventory(int size) {
            this.items = new ItemStack[size];
        }

        @Override
        public int getContainerSize() {
            return items.length;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) return false;
            }
            return true;
        }

        @Override
        public @NotNull ItemStack getItem(int slot) {
            return items[slot];
        }

        @Override
        public @NotNull ItemStack removeItem(int slot, int amount) {
            if (items[slot].isEmpty()) return ItemStack.EMPTY;
            return items[slot].split(amount);
        }

        @Override
        public @NotNull ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = items[slot];
            items[slot] = ItemStack.EMPTY;
            return stack;
        }

        @Override
        public void setItem(int slot, @NotNull ItemStack stack) {
            items[slot] = stack;
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(@NotNull Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < items.length; i++) {
                items[i] = ItemStack.EMPTY;
            }
        }
    }
}
