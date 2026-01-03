package com.example.examplemod;

import com.example.examplemod.item.tutorial.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.example.examplemod.block.ModularBlockEntityTypes;
import com.example.examplemod.block.ModularBlocks;
import com.example.examplemod.block.RepairStationBlock;
import com.example.examplemod.block.RepairStationMenu;
import com.example.examplemod.gui.tutorial.Tutorial6Menu;
import com.example.examplemod.gui.tutorial.Tutorial7Menu;
import com.example.examplemod.item.RepairStationItem;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "examplemod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    // Create a Deferred Register to hold MenuTypes which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);

    // Menu types
    public static final DeferredHolder<MenuType<?>, MenuType<RepairStationMenu>> REPAIR_STATION_MENU =
            MENU_TYPES.register("repair_station", () -> new MenuType<>(RepairStationMenu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
    public static final DeferredHolder<MenuType<?>, MenuType<Tutorial6Menu>> TUTORIAL_6_MENU =
            MENU_TYPES.register("tutorial_6", () -> new MenuType<>(Tutorial6Menu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
    public static final DeferredHolder<MenuType<?>, MenuType<Tutorial7Menu>> TUTORIAL_7_MENU =
            MENU_TYPES.register("tutorial_7", () -> new MenuType<>(Tutorial7Menu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET));

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 创建修复台物品
    public static final DeferredItem<Item> REPAIR_STATION = ITEMS.register("repair_station",
            () -> RepairStationItem.INSTANCE.get());

    // 创建修复台方块物品
    public static final DeferredItem<BlockItem> REPAIR_STATION_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("repair_station_block", ModularBlocks.REPAIR_STATION_BLOCK);

    // 创建教程1物品
    public static final DeferredItem<Item> TUTORIAL_1 = ITEMS.register("tutorial_1",
            () -> Tutorial1Item.INSTANCE.get());

    // 创建教程2物品
    public static final DeferredItem<Item> TUTORIAL_2 = ITEMS.register("tutorial_2",
            () -> Tutorial2Item.INSTANCE.get());

    // 创建教程2物品
    public static final DeferredItem<Item> TUTORIAL_3 = ITEMS.register("tutorial_3",
            () -> Tutorial3Item.INSTANCE.get());

    public static final DeferredItem<Item> TUTORIAL_4 = ITEMS.register("tutorial_4",
            () -> Tutorial4Item.INSTANCE.get());

    public static final DeferredItem<Item> TUTORIAL_5 = ITEMS.register("tutorial_5",
            () -> Tutorial5Item.INSTANCE.get());

    public static final DeferredItem<Item> TUTORIAL_6 = ITEMS.register("tutorial_6",
            () -> Tutorial6Item.INSTANCE.get());

    public static final DeferredItem<Item> TUTORIAL_7 = ITEMS.register("tutorial_7",
            () -> Tutorial7Item.INSTANCE.get());
    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(REPAIR_STATION.get());
                output.accept(REPAIR_STATION_BLOCK_ITEM.get());
                output.accept(TUTORIAL_1.get());
                output.accept(TUTORIAL_2.get());
                output.accept(TUTORIAL_3.get());
                output.accept(TUTORIAL_4.get());
                output.accept(TUTORIAL_5.get());
                output.accept(TUTORIAL_6.get());
                output.accept(TUTORIAL_7.get());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register modular blocks
        ModularBlocks.BLOCKS.register(modEventBus);
        // Register block entity types
        ModularBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        // Register menu types (centralized registration)
        MENU_TYPES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
