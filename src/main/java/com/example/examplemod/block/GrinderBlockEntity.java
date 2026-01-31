package com.example.examplemod.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.FillDirection;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.appliedenergistics.yoga.YogaGutter;

import java.util.Map;

/**
 * 粉碎机方块实体
 * 将矿物粉碎成粉末，消耗RF能量
 */
public class GrinderBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    // 配置常量
    public static final int MAX_ENERGY = 10000;
    public static final int ENERGY_PER_TICK = 20;
    public static final int PROCESSING_TIME = 100;

    // 物品槽位：0=输入, 1=输出
    @Persisted
    @DescSynced
    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                // 输入槽只接受可粉碎的物品
                return isValidInput(stack);
            }
            return slot == 0;
        }
    };

    // 能量存储
    @Persisted
    @DescSynced
    private int energy = 0;

    // 处理进度
    @Persisted
    @DescSynced
    private int progress = 0;

    // 是否正在工作
    @Persisted
    @DescSynced
    private boolean isWorking = false;

    // 配方缓存
    private ItemStack currentOutput = ItemStack.EMPTY;

    // 能量存储接口
    public final IEnergyStorage energyStorage = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = Math.min(maxReceive, MAX_ENERGY - energy);
            if (!simulate && received > 0) {
                energy += received;
                setChanged();
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return MAX_ENERGY;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    };

    // 配方映射表：输入 -> 输出
    private static final Map<ItemStack, ItemStack> RECIPES = Map.of(
            new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_NUGGET, 9)
    );

    public GrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModularBlockEntityTypes.GRINDER.get(), pos, blockState);
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }

    /**
     * 检查物品是否为有效的输入
     */
    private boolean isValidInput(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (ItemStack validInput : RECIPES.keySet()) {
            if (ItemStack.isSameItem(validInput, stack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取输入对应的输出
     */
    private ItemStack getOutputForInput(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> entry : RECIPES.entrySet()) {
            if (ItemStack.isSameItem(entry.getKey(), input)) {
                return entry.getValue().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 每刻更新逻辑
     */
    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean wasWorking = isWorking;

        // 尝试处理物品
        tryProcess();

        // 如果工作状态改变，更新方块状态
        if (wasWorking != isWorking) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }

        setChanged();
    }

    /**
     * 处理逻辑
     */
    private void tryProcess() {
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);

        // 检查是否有输入
        if (input.isEmpty()) {
            progress = 0;
            isWorking = false;
            currentOutput = ItemStack.EMPTY;
            return;
        }

        // 获取配方输出
        ItemStack recipeOutput = getOutputForInput(input);
        if (recipeOutput.isEmpty()) {
            progress = 0;
            isWorking = false;
            return;
        }

        // 检查输出槽是否可以接收
        if (!output.isEmpty() && !ItemStack.isSameItem(output, recipeOutput)) {
            progress = 0;
            isWorking = false;
            return;
        }

        if (!output.isEmpty() && output.getCount() + recipeOutput.getCount() > output.getMaxStackSize()) {
            progress = 0;
            isWorking = false;
            return;
        }

        // 保存当前输出
        currentOutput = recipeOutput;

        // 检查能量是否足够
        if (energy < ENERGY_PER_TICK) {
            isWorking = false;
            return;
        }

        // 消耗能量并增加进度
        energy -= ENERGY_PER_TICK;
        progress++;
        isWorking = true;

        // 检查是否完成
        if (progress >= PROCESSING_TIME) {
            // 完成处理
            inventory.extractItem(0, 1, false);
            if (output.isEmpty()) {
                inventory.setStackInSlot(1, recipeOutput.copy());
            } else {
                output.grow(recipeOutput.getCount());
                inventory.setStackInSlot(1, output);
            }
            progress = 0;
            isWorking = false;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        energy = tag.getInt("Energy");
        progress = tag.getInt("Progress");
        isWorking = tag.getBoolean("IsWorking");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Energy", energy);
        tag.putInt("Progress", progress);
        tag.putBoolean("IsWorking", isWorking);
    }

    /**
     * 创建UI界面
     */
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        var root = new UIElement().layout(layout -> layout
                .setPadding(YogaEdge.ALL, 7)
                .setGap(YogaGutter.ALL, 5)
        );

        // 标题
        root.addChild(new Label()
                .setText("Grinder")
                .layout(layout -> layout.setMargin(YogaEdge.BOTTOM, 5))
        );

        // 中间行：输入槽 + 进度条 + 输出槽
        var middleRow = new UIElement().layout(layout -> layout
                .flexDirection(YogaFlexDirection.ROW)
                .setGap(YogaGutter.ALL, 10)
                .setJustifyContent(org.appliedenergistics.yoga.YogaJustify.CENTER)
        );

        // 输入槽
        var inputSlot = new ItemSlot().bind(inventory, 0);
        middleRow.addChild(inputSlot);

        // 进度条
        var progressBar = new ProgressBar()
                .setRange(0, PROCESSING_TIME)
                .progressBarStyle(style -> style.fillDirection(FillDirection.LEFT_TO_RIGHT))
                .bar(bar -> bar.style(s -> s.background(new ColorRectTexture(0xFF00AAFF))))
                .bindDataSource(SupplierDataSource.of(() -> (float) progress))
                .layout(layout -> layout.setWidth(60).setHeight(16));
        middleRow.addChild(progressBar);

        // 输出槽
        var outputSlot = new ItemSlot().bind(inventory, 1);
        middleRow.addChild(outputSlot);

        root.addChild(middleRow);

        // 能量条
        var energyRow = new UIElement().layout(layout -> layout
                .flexDirection(YogaFlexDirection.ROW)
                .setGap(YogaGutter.ALL, 5)
                .setJustifyContent(org.appliedenergistics.yoga.YogaJustify.CENTER)
        );

        energyRow.addChild(new Label().setText("Energy: "));

        var energyBar = new ProgressBar()
                .bar(bar -> bar.style(s -> s.background(new ColorRectTexture(0xFFFFAA00))))
                .setRange(0, MAX_ENERGY)
                .bindDataSource(SupplierDataSource.of(() -> (float) energy))
                .label(label -> label
                        .bindDataSource(SupplierDataSource.of(() ->
                                Component.literal("%d / %d FE".formatted(energy, MAX_ENERGY))))
                        .setText("")
                )
                .layout(layout -> layout.setWidth(120).setHeight(14));
        energyRow.addChild(energyBar);

        root.addChild(energyRow);

        // 状态标签
        root.addChild(new Label()
                .bindDataSource(SupplierDataSource.of(() -> {
                    if (isWorking) {
                        return Component.literal("Processing... (%d%%)".formatted(progress * 100 / PROCESSING_TIME));
                    } else if (inventory.getStackInSlot(0).isEmpty()) {
                        return Component.literal("No Input");
                    } else if (energy < ENERGY_PER_TICK) {
                        return Component.literal("No Energy");
                    } else {
                        return Component.literal("Ready");
                    }
                }))
                .layout(layout -> layout.setMargin(YogaEdge.TOP, 5))
        );

        // 玩家背包
        root.addChildren(new InventorySlots());

        return new ModularUI(
                UI.of(root, java.util.List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player
        );
    }
}
