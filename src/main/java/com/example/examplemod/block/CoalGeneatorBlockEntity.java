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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaGutter;
import org.appliedenergistics.yoga.YogaJustify;

import java.util.List;

public class CoalGeneatorBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    public static final int MAX_CAPACITY = 100000;
    public static final int GENERATION_RATE = 32;

    @Persisted
    @DescSynced
    public final ItemStackHandler inventory = new ItemStackHandler(1);

    @Persisted
    @DescSynced
    private int energy = 0;

    @Persisted
    @DescSynced
    private int burnTime = 0;

    @Persisted
    @DescSynced
    private int maxBurnTime = 0;

    @Persisted
    @DescSynced
    private boolean isWorking = false;

    public final IEnergyStorage energyStorage = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = Math.min(energy, Math.min(maxExtract, Integer.MAX_VALUE));
            if (!simulate) {
                energy -= extracted;
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return MAX_CAPACITY;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    public CoalGeneatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModularBlockEntityTypes.COAL_GENERATOR.get(), pos, blockState);
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean wasWorking = isWorking;

        consumeFuel();

        generateEnergy();

        outputEnergy();

        if (wasWorking != isWorking) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }

        setChanged();
    }

    private void consumeFuel() {
        if (burnTime <= 0 && energy < MAX_CAPACITY) {
            ItemStack fuelStack = inventory.getStackInSlot(0);
            if (!fuelStack.isEmpty()) {
                int burnDuration = getBurnTime(fuelStack);

                if (burnDuration > 0) {
                    inventory.extractItem(0, 1, false);
                    burnTime = burnDuration;
                    maxBurnTime = burnDuration;
                }
            }
        }
    }

    private int getBurnTime(ItemStack fuel) {
        if (fuel.is(Items.COAL)) return 1600;
        if (fuel.is(Items.CHARCOAL)) return 1600;
        if (fuel.is(Items.COAL_BLOCK)) return 16000;
        if (fuel.is(Items.BLAZE_ROD)) return 2400;
        if (fuel.is(Items.DRIED_KELP_BLOCK)) return 200;
        if (fuel.is(Items.LAVA_BUCKET)) return 20000;
        return 0;
    }

    private void generateEnergy() {
        if (burnTime > 0 && energy < MAX_CAPACITY) {
            int toGenerate = Math.min(GENERATION_RATE, MAX_CAPACITY - energy);
            energy += toGenerate;
            burnTime--;
            isWorking = true;
        } else {
            isWorking = false;
        }
    }

    private void outputEnergy() {
        if (energy <= 0) return;

        for (Direction direction : Direction.values()) {
            var adjacentPos = worldPosition.relative(direction);
            var adjacentBE = level.getBlockEntity(adjacentPos);

            if (adjacentBE != null) {
                var energyCap = level.getCapability(Capabilities.EnergyStorage.BLOCK, adjacentPos, direction.getOpposite());
                if (energyCap != null) {
                    int toTransfer = Math.min(energy, energyCap.receiveEnergy(energy, true));
                    if (toTransfer > 0) {
                        int transferred = energyCap.receiveEnergy(toTransfer, false);
                        energy -= transferred;
                        if (energy <= 0) break;
                    }
                }
            }
        }
    }

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        var root = new UIElement().layout(layout -> layout
                .setPadding(YogaEdge.ALL, 4)
                .setGap(YogaGutter.ALL, 4)
        );

        root.addChild(new Label().setText("Coal Generator").layout(layout -> layout
                .setMargin(YogaEdge.BOTTOM, 2)
        ));

        var middleRow = new UIElement();

        var fuelSlot = new ItemSlot().bind(inventory, 0);
        middleRow.addChild(fuelSlot);

        var progressBar = new ProgressBar()
                .setRange(0, maxBurnTime > 0 ? maxBurnTime : 100)
                .progressBarStyle(style -> style.fillDirection(FillDirection.LEFT_TO_RIGHT));
        progressBar.bindDataSource(SupplierDataSource.of(() -> (float) burnTime));

        middleRow.addChild(progressBar);

        root.addChild(middleRow);

        var energyRow = new UIElement();
        energyRow.addChild(new Label().setText("Energy:"));

        var energyBar = new ProgressBar()
                .bar(b -> b.style( s->s.background(new ColorRectTexture(0xFF00FF00))))
                .setRange(0,MAX_CAPACITY)
                .bindDataSource(SupplierDataSource.of(()-> (float) energy))
                .label(label -> label.bindDataSource(SupplierDataSource.of( ()-> Component.literal("Energy: %d / %d".formatted(energy, MAX_CAPACITY)))).setText(""));

        energyRow.addChild(energyBar);
        root.addChild(energyRow);

        root.addChild(new Label().bindDataSource(SupplierDataSource.of(() -> {
            if (isWorking) {
                return Component.literal("Generating: " + GENERATION_RATE + " FE/t");
            } else if (burnTime > 0) {
                return Component.literal("Idle - Energy Full");
            } else if (inventory.getStackInSlot(0).isEmpty()) {
                return Component.literal("No Fuel");
            } else {
                return Component.literal("Ready");
            }
        })));

        root.addChildren(new InventorySlots());
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))), holder.player);
    }
}
