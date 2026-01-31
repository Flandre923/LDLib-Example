package com.example.examplemod.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.data.FillDirection;
import com.lowdragmc.lowdraglib2.gui.ui.elements.FluidSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import com.lowdragmc.lowdraglib2.utils.XmlUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * 流体熔炉方块实体
 * 支持接收流体并使用流体能量烧制物品
 * 使用原版烧制配方
 */
public class FluidFurnaceBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity, IFluidHandler {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    // ==================== 物品栏 ====================

    /** 物品栏 - 0:输入槽, 1:输出槽 */
    @Persisted
    @DescSynced
    public final ItemStackHandler inventory = new ItemStackHandler(2);

    // ==================== 流体系统 ====================

    /** 流体存储 - 燃料/能源 (直接存储 FluidStack，LDlib2 支持) */
    @Persisted
    @DescSynced
    private FluidStack fluidStack = FluidStack.EMPTY;

    /** 当前进度 (0-SMELT_TIME) */
    @Persisted
    @DescSynced
    private float progress = 0f;

    /** 是否正在工作 */
    @Persisted
    @DescSynced
    private boolean isWorking = false;

    /** 每烧制一个物品需要的基础流体量 (原版岩浆1000mb烧100个物品，每个10mb) */
    private static final int FLUID_PER_ITEM = 10;

    /** 烧制时间 (原版熔炉200tick) */
    public static final int SMELT_TIME = 200;

    /** 当前剩余可烧制次数 (根据剩余流体计算) */
    @Persisted
    @DescSynced
    private int remainingSmelts = 0;

    /** 当前配方 */
    private RecipeHolder<?> currentRecipe;

    /** 配方缓存的输入物品 */
    private ItemStack cachedInput = ItemStack.EMPTY;

    public FluidFurnaceBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModularBlockEntityTypes.FLUID_FURNACE.get(), pWorldPosition, pBlockState);
        this.fluidStack = FluidStack.EMPTY;
    }


    /** 容量常量 */
    private static final int FLUID_CAPACITY = 10000; // 10000mb = 10桶

    /**
     * 服务端每tick调用 - 处理烧制逻辑
     */
    public void serverTick() {
        var inputStack = inventory.getStackInSlot(0);
        var outputStack = inventory.getStackInSlot(1);

        // 检查输入物品是否改变
        if (!ItemStack.matches(inputStack, cachedInput)) {
            currentRecipe = null;
            cachedInput = inputStack.copy();
        }

        // 检查是否有有效的配方
        if (currentRecipe == null && !inputStack.isEmpty() && level != null) {
            currentRecipe = findSmeltingRecipe(inputStack);
        }

        // 如果有配方且有剩余烧制次数，开始烧制
        if (currentRecipe != null && remainingSmelts > 0) {
            var resultItem = ((AbstractCookingRecipe) currentRecipe.value()).getResultItem(null);
            if (outputStack.isEmpty() || canMergeOutput(outputStack, resultItem)) {
                // 增加进度
                progress++;

                if (progress >= SMELT_TIME) {
                    // 烧制完成
                    completeSmelting(resultItem);
                }
                isWorking = true;
            } else {
                isWorking = false;
            }
        } else {
            isWorking = false;
            if (progress > 0) {
                progress = 0;
            }
        }
    }

    /**
     * 完成烧制
     */
    private void completeSmelting(ItemStack resultItem) {
        var outputSlot = inventory.getStackInSlot(1);
        var inputSlot = inventory.getStackInSlot(0);

        if (outputSlot.isEmpty()) {
            inventory.setStackInSlot(1, resultItem.copy());
        } else {
            inventory.setStackInSlot(1, outputSlot.copyWithCount(outputSlot.getCount() + resultItem.getCount()));
        }

        inventory.extractItem(0, 1, false);

        // 减少剩余烧制次数
        remainingSmelts--;

        // 消耗对应的流体量
        if (!fluidStack.isEmpty()) {
            fluidStack = fluidStack.copy();
            fluidStack.shrink(FLUID_PER_ITEM);
            if (fluidStack.getAmount() <= 0) {
                fluidStack = FluidStack.EMPTY;
            }
        }

        progress = 0;
        currentRecipe = null;
        cachedInput = ItemStack.EMPTY;
    }

    /**
     * 查找烧制配方
     * 同时支持普通熔炉和高炉配方 (RAW IRON 等需要 BLASTING)
     */
    private RecipeHolder<?> findSmeltingRecipe(ItemStack input) {
        if (level == null) return null;

        var recipeManager = level.getRecipeManager();

        // 优先检查普通熔炉配方
        var smeltingRecipes = recipeManager.getAllRecipesFor(RecipeType.SMELTING);
        for (var holder : smeltingRecipes) {
            var recipe = holder.value();
            var ingredient = recipe.getIngredients().get(0);
            if (ingredient.test(input)) {
                return holder;
            }
        }

        // 兼容高炉配方 (RAW IRON, RAW GOLD, RAW COPPER 等)
        var blastingRecipes = recipeManager.getAllRecipesFor(RecipeType.BLASTING);
        for (var holder : blastingRecipes) {
            var recipe = holder.value();
            var ingredient = recipe.getIngredients().get(0);
            if (ingredient.test(input)) {
                return holder;
            }
        }

        return null;
    }

    /**
     * 检查是否可以合并输出
     */
    private boolean canMergeOutput(ItemStack existing, ItemStack adding) {
        if (existing.isEmpty()) return true;
        if (!existing.is(adding.getItem())) return false;
        return existing.getCount() + adding.getCount() <= existing.getMaxStackSize();
    }

    /**
     * 创建模块化UI - 从XML加载并绑定数据
     */
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        // 从XML加载UI
        var xml = XmlUtils.loadXml(ResourceLocation.parse("examplemod:fluid_furnace.xml"));
        var ui = UI.of(xml);

        // 绑定流体槽
        ui.select("#fluid_slot").findFirst().ifPresent(element -> {
            if (element instanceof FluidSlot fluidSlot) {
                fluidSlot.setCapacity(FLUID_CAPACITY)
                        .slotStyle(style -> style.fillDirection(FillDirection.UP_TO_DOWN));
                fluidSlot.bind(this, 0);
            }
        });

        // 绑定输入槽
        ui.select("#input_slot").findFirst().ifPresent(element -> {
            if (element instanceof ItemSlot itemSlot) {
                itemSlot.bind(inventory, 0);
            }
        });

        // 绑定输出槽
        ui.select("#output_slot").findFirst().ifPresent(element -> {
            if (element instanceof ItemSlot itemSlot) {
                itemSlot.bind(inventory, 1);
            }
        });

        // 绑定进度条
        ui.select("#progress_bar").findFirst().ifPresent(element -> {
            if (element instanceof ProgressBar progressBar) {
                progressBar.setRange(0, SMELT_TIME)
                        .progressBarStyle(style -> style.fillDirection(FillDirection.LEFT_TO_RIGHT));
                progressBar.bindDataSource(SupplierDataSource.of(() -> progress));
            }
        });

        // 绑定状态标签
        ui.select("#status_label").findFirst().ifPresent(element -> {
            if (element instanceof Label label) {
                label.bindDataSource(SupplierDataSource.of(() -> {
                    if (isWorking) {
                        return Component.literal("Smelting... " + (int)(progress / SMELT_TIME * 100) + "%");
                    } else if (fluidStack.isEmpty()) {
                        return Component.literal("Need Fluid!");
                    } else if (inventory.getStackInSlot(0).isEmpty()) {
                        return Component.literal("Place items to smelt");
                    } else if (currentRecipe == null) {
                        return Component.literal("No recipe for this item");
                    } else {
                        return Component.literal("Ready");
                    }
                }));
            }
        });

        return ModularUI.of(ui, holder.player);
    }

    // ==================== IFluidHandler 实现 ====================

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluidStack;
    }

    @Override
    public int getTankCapacity(int tank) {
        return FLUID_CAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) return 0;

        // 计算加入的流体可以烧制多少个物品
        int additionalSmelts = resource.getAmount() / FLUID_PER_ITEM;

        if (additionalSmelts <= 0) {
            return 0;
        }

        // 如果当前没有流体，接收任何流体；否则只接收同类型流体
        if (!fluidStack.isEmpty() && !resource.getFluid().isSame(fluidStack.getFluid())) {
            return 0;
        }

        int space = FLUID_CAPACITY - fluidStack.getAmount();
        int toFill = Math.min(resource.getAmount(), space);

        if (toFill <= 0) return 0;

        if (action.execute()) {
            if (fluidStack.isEmpty()) {
                fluidStack = new FluidStack(resource.getFluid(), toFill);
            } else {
                fluidStack = fluidStack.copy();
                fluidStack.grow(toFill);
            }
            // 增加剩余烧制次数
            remainingSmelts += additionalSmelts;
        }
        return toFill;
    }

    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (fluidStack.isEmpty() || !resource.is(fluidStack.getFluid())) {
            return FluidStack.EMPTY;
        }

        int toDrain = Math.min(resource.getAmount(), fluidStack.getAmount());
        if (toDrain <= 0) return FluidStack.EMPTY;

        FluidStack drained = new FluidStack(fluidStack.getFluid(), toDrain);
        if (action.execute()) {
            fluidStack = fluidStack.copy();
            fluidStack.shrink(toDrain);
            if (fluidStack.getAmount() <= 0) {
                fluidStack = FluidStack.EMPTY;
            }
            // 减少对应的烧制次数
            int removedSmelts = toDrain / FLUID_PER_ITEM;
            remainingSmelts = Math.max(0, remainingSmelts - removedSmelts);
        }
        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (fluidStack.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int toDrain = Math.min(maxDrain, fluidStack.getAmount());
        FluidStack drained = new FluidStack(fluidStack.getFluid(), toDrain);
        if (action.execute()) {
            fluidStack = fluidStack.copy();
            fluidStack.shrink(toDrain);
            if (fluidStack.getAmount() <= 0) {
                fluidStack = FluidStack.EMPTY;
            }
            // 减少对应的烧制次数
            int removedSmelts = toDrain / FLUID_PER_ITEM;
            remainingSmelts = Math.max(0, remainingSmelts - removedSmelts);
        }
        return drained;
    }


    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }
}
