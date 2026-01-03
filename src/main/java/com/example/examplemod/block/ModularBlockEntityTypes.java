package com.example.examplemod.block;

import com.example.examplemod.ExampleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块实体类型注册
 */
public class ModularBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExampleMod.MODID);

    @SuppressWarnings("rawtypes")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RepairStationBlockEntity>> REPAIR_STATION =
            BLOCK_ENTITY_TYPES.register("repair_station", () ->
                    BlockEntityType.Builder.of(RepairStationBlockEntity::new, ModularBlocks.REPAIR_STATION_BLOCK.get()).build(null));

}
