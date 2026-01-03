package com.example.examplemod.gui;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 物品修复容器的数据类，用于存储槽位中的物品
 */
public class RepairContainer implements Container {

    private static final int SLOT_COUNT = 1;
    private final ItemStack[] items = new ItemStack[SLOT_COUNT];

    public RepairContainer() {
        for (int i = 0; i < items.length; i++) {
            items[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return items[0].isEmpty();
    }

    @NotNull
    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? items[slot] : ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !items[slot].isEmpty()) {
            ItemStack itemstack = items[slot].split(amount);
            if (items[slot].isEmpty()) {
                items[slot] = ItemStack.EMPTY;
            }
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0 && !items[slot].isEmpty()) {
            ItemStack itemstack = items[slot];
            items[slot] = ItemStack.EMPTY;
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (slot == 0) {
            items[slot] = stack;
        }
    }

    @Override
    public void setChanged() {
    }

    @Override
    public void clearContent() {
        items[0] = ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * 获取物品并尝试修复耐久度
     * @return 是否成功修复
     */
    public boolean tryRepairItem() {
        if (isEmpty()) {
            return false;
        }

        var item = items[0];
        var itemDamage = item.getDamageValue();
        var maxDamage = item.getMaxDamage();

        // 检查物品是否有耐久度
        if (maxDamage <= 0 || itemDamage <= 0) {
            return false;
        }

        // 修复一半的耐久度消耗
        int repairAmount = itemDamage / 2;
        if (repairAmount <= 0) {
            repairAmount = 1;
        }

        item.setDamageValue(itemDamage - repairAmount);
        return true;
    }
}
