package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Tutorial 7 Screen - Displays a ModularUI with data bindings
 * 教程 7 屏幕 - 显示带有数据绑定的 ModularUI
 */
public class Tutorial7Screen extends AbstractContainerScreen<Tutorial7Menu> {
    private final ModularUI modularUI;

//    public Tutorial7Screen(Tutorial7Menu menu, Inventory playerInventory, Component title, ModularUI modularUI) {
//        super(menu, playerInventory, title);
//        this.modularUI = modularUI;
//        this.imageWidth = 250;
//        this.imageHeight = 220;
//    }
    public Tutorial7Screen(Tutorial7Menu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.modularUI = null;
        this.imageWidth = 250;
        this.imageHeight = 220;
    }
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

//        modularUI.setScreenAndInit(this);
//        this.addRenderableWidget(modularUI.getWidget());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Draw title if needed
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // ModularUI handles its own rendering
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
