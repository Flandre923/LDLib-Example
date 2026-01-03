package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.*;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.appliedenergistics.yoga.YogaFlexDirection;

/**
 * Tutorial 7: Communication between Screen and Menu
 * 教程 7：屏幕与菜单之间的通信
 *
 * While InventorySlots works out of the box, they are pre-packaged built-in components.
 * In real projects, you often need more control over how data and events flow between
 * the client-side screen and the server-side menu.
 *
 * ModularUI provides full support for data bindings and event dispatch across client and server.
 * This allows UI interactions on the client to safely trigger logic on the server,
 * and server-side state changes to automatically update the UI.
 */
public class Tutorial7UIContainer {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);
    private final FluidTank fluidTank = new FluidTank(2000);
    private boolean bool = true;
    private String string = "hello";
    private float number = 0.5f;


    /**
     * Create a ModularUI with data bindings for communication between Screen and Menu
     *
     * @param menu the server-side menu containing the data
     * @return a configured ModularUI instance
     */
    public static ModularUI createModularUI(Tutorial7Menu menu) {
        var player = menu.getPlayer();

        // create a root element
        var root = new UIElement();
        root.addChildren(
                // add a label to display title
                new Label().setText("Data Between Screen and Menu")
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),

                // Row 1: Bind storage to slots (ItemSlot and FluidSlot)
                new UIElement().addChildren(
                        new ItemSlot().bind(menu.getItemHandler(), 0),
                        new ItemSlot().bind(menu.getItemHandler(), 1),
                        new FluidSlot().bind(menu.getFluidTank(), 0)
                ).layout(l -> l.gapAll(2).flexDirection(YogaFlexDirection.ROW)),

                // Row 2: Data bindings with different components
                new UIElement().addChildren(
                        // Boolean binding with Switch
                        new Switch()
                                .bindDataSource(SupplierDataSource.of(menu::isBool))
                                .bindObserver(value -> menu.setBool(value)),

                        // String binding with TextField
                        new TextField()
                                .bindDataSource(SupplierDataSource.of(menu::getString))
                                .bindObserver(value -> menu.setString(value)),

                        // Float binding with horizontal Scroller
                        new Scroller.Horizontal()
                                .bindDataSource(SupplierDataSource.of(menu::getNumber))
                                .bindObserver(value -> menu.setNumber(value)),

                        // Read-only S2C binding - always gets data from server
                        new Label().bindDataSource(SupplierDataSource.of(() ->
                                Component.literal("s->c only: ")
                                        .append(Component.literal(String.valueOf(menu.isBool())).withStyle(ChatFormatting.AQUA))
                                        .append(" ")
                                        .append(Component.literal(menu.getString()).withStyle(ChatFormatting.RED))
                                        .append(" ")
                                        .append(Component.literal("%.2f".formatted(menu.getNumber())).withStyle(ChatFormatting.YELLOW))
                        ))
                ).layout(l -> l.gapAll(2)),

                // Row 3: Server event listener button
                new Button().setText("Toggle Fluid")
                        .addServerEventListener(UIEvents.MOUSE_DOWN, event -> {
                            var fluid = menu.getFluidTank().getFluid();
                            if (fluid.getFluid() == Fluids.WATER) {
                                menu.getFluidTank().setFluid(new FluidStack(Fluids.LAVA, 1000));
                            } else {
                                menu.getFluidTank().setFluid(new FluidStack(Fluids.WATER, 1000));
                            }
                        })
                        .layout(l -> l.width(100)),

                // Row 4: Player inventory
                new UIElement().addChildren(
                        new com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots()
                ).addClass("panel_bg")
        );

        root.layout(l -> l.paddingAll(7).gapAll(5));

        var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MODERN));
        return ModularUI.of(ui, player);
    }
}
