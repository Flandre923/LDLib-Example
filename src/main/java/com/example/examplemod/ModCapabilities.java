package com.example.examplemod;

import com.example.examplemod.block.CoalGeneatorBlockEntity;
import com.example.examplemod.block.ModularBlocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities {

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.EnergyStorage.BLOCK, (level, pos, state, be, context) -> {
            if (be instanceof CoalGeneatorBlockEntity coalBE) {
                return coalBE.energyStorage;
            }
            return null;
        }, ModularBlocks.COAL_GENERATOR_BLOCK.get());
    }
}
