package com.example.examplemod.gui;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 使用LDLlib2 UI的自定义Screen
 */
public class RepairScreen extends Screen {

    private final ModularUI modularUI;

    public RepairScreen(ModularUI modularUI) {
        super(Component.empty());
        this.modularUI = modularUI;
    }

    @Override
    protected void init() {
        super.init();
        // 初始化ModularUI并将其添加到Screen
        modularUI.setScreenAndInit(this);
        this.addRenderableWidget(modularUI.getWidget());
    }

    @Override
    public void removed() {
        super.removed();
    }
}
