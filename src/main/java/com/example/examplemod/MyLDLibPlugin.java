package com.example.examplemod;


import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacketDistributor;
import com.lowdragmc.lowdraglib2.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib2.plugin.LDLibPlugin;

/**
 * LDLib2 plugin for Example Mod.
 * This class is used to initialize and configure LDLib2.
 */
@LDLibPlugin
public class MyLDLibPlugin  implements ILDLibPlugin {
    @Override
    public void onLoad() {
        // Do your register or setup for LDLib2 here.
        ExampleMod.LOGGER.info("LDLib2 initialized");
    }
}
