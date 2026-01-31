package com.example.examplemod.block;

import com.example.examplemod.ExampleMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.Block;

/**
 * 方块注册
 */
public class ModularBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExampleMod.MODID);

    public static final DeferredHolder<Block, RepairStationBlock> REPAIR_STATION_BLOCK =
            BLOCKS.register("repair_station_block", RepairStationBlock::new);

    public static final DeferredHolder<Block, MyFurnBlock> MY_FURN_BLOCK =
            BLOCKS.register("my_furn_block", MyFurnBlock::new);

    public static final DeferredHolder<Block, SimpleBlock> SIMPLE_BLOCK =
            BLOCKS.register("simple_block", SimpleBlock::new);

    public static final DeferredHolder<Block, FluidFurnaceBlock> FLUID_FURNACE_BLOCK =
            BLOCKS.register("fluid_furnace_block", FluidFurnaceBlock::new);

    public static final DeferredHolder<Block, CoalGeneator> COAL_GENERATOR_BLOCK =
            BLOCKS.register("coal_generator_block", CoalGeneator::new);

    public static final DeferredHolder<Block, GrinderBlock> GRINDER_BLOCK =
            BLOCKS.register("grinder_block", GrinderBlock::new);

}
