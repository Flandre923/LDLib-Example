package com.example.examplemod.gui;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 药水注射器屏幕类
 */
public class PotionInjectorScreen extends Screen {

    private final ModularUI modularUI;

    public PotionInjectorScreen(ModularUI modularUI) {
        super(Component.empty());
        this.modularUI = modularUI;
    }

    @Override
    protected void init() {
        super.init();
        modularUI.setScreenAndInit(this);
        this.addRenderableWidget(modularUI.getWidget());
    }

    @Override
    public void removed() {
        super.removed();
    }
}
