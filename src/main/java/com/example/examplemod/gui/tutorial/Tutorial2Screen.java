package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Tutorial 2 Screen - Displays a ModularUI with better layout
 * 教程 2 屏幕 - 显示具有更好布局的 ModularUI
 */
public class Tutorial2Screen extends Screen {

    private final ModularUI modularUI;

    /**
     * Create a new Tutorial2Screen with the provided ModularUI
     *
     * @param modularUI the ModularUI to display
     */
    public Tutorial2Screen(ModularUI modularUI) {
        super(Component.empty());
        this.modularUI = modularUI;
    }

    @Override
    protected void init() {
        super.init();

        // Initialize the ModularUI and add it to this screen
        modularUI.setScreenAndInit(this);
        this.addRenderableWidget(modularUI.getWidget());
    }

    @Override
    public void removed() {
        super.removed();
    }
}
