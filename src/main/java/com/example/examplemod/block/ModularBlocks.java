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

}
