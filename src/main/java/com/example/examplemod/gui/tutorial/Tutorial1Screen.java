package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class Tutorial1Screen extends Screen {

    private final ModularUI modularUI;

    /**
     * Create a new Tutorial1Screen with the provided ModularUI
     * 使用提供的 ModularUI 创建新的 Tutorial1Screen
     *
     * @param modularUI the ModularUI to display
     */
    public Tutorial1Screen(ModularUI modularUI) {
        super(Component.empty());
        this.modularUI = modularUI;
    }

    @Override
    protected void init() {
        super.init();

        // Initialize the ModularUI and add it to this screen
        // 初始化 ModularUI 并将其添加到此屏幕
        modularUI.setScreenAndInit(this);
        this.addRenderableWidget(modularUI.getWidget());
    }

    @Override
    public void removed() {
        super.removed();
        // The ModularUI handles cleanup automatically when removed
        // ModularUI 在被移除时自动处理清理
    }
}
