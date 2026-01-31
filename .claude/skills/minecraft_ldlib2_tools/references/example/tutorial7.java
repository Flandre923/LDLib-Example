Tutorial 7: communication between Screen and Menu
While the InventorySlots works out of the box, they are pre-packaged built-in components. In real projects, you often need more control over how data and events flow between the client-side screen and the server-side menu.

ModularUI provides full support for data bindings and event dispatch across client and server. This allows UI interactions on the client to safely trigger logic on the server, and server-side state changes to automatically update the UI. Check data bindings page for more details.

Here, we focus on practical patterns to help you get started quickly.


// represents data on the server
private final ItemStackHandler itemHandler = new ItemStackHandler(2);
private final FluidTank fluidTank = new FluidTank(2000);
private boolean bool = true;
private String string = "hello";
private float number = 0.5f;

private static ModularUI createModularUI(Player player) {
    // create a root element
    var root = new UIElement();
    root.addChildren(
            // add a label to display text
            new Label().setText("Data Between Screen and Menu"),
            // bind storage to slots
            new UIElement().addChildren(
                    new ItemSlot().bind(itemHandler, 0),
                    new ItemSlot().bind(new ItemHandlerSlot(itemHandler, 1).setCanTake(p -> false)),
                    new FluidSlot().bind(fluidTank, 0)
            ).layout(l -> l.gapAll(2).flexDirection(YogaFlexDirection.ROW)),
            // bind value to the components
            new UIElement().addChildren(
                    new Switch().bind(DataBindingBuilder.bool(() -> bool, value -> bool = value).build()),
                    new TextField().bind(DataBindingBuilder.string(() -> string, value -> string = value).build()),
                    new Scroller.Horizontal().bind(DataBindingBuilder.floatVal(() -> number, value -> number = value).build()),
                    // read-only (s->c), always get data from the server and display on the client
                    new Label().bind(DataBindingBuilder.componentS2C(() -> Component.literal("s->c only: ")
                            .append(Component.literal(String.valueOf(bool)).withStyle(ChatFormatting.AQUA)).append(" ")
                            .append(Component.literal(string).withStyle(ChatFormatting.RED)).append(" ")
                            .append(Component.literal("%.2f".formatted(number)).withStyle(ChatFormatting.YELLOW)))
                            .build())
            ).layout(l -> l.gapAll(2)),
            // trigger ui events on the server side
            new Button().addServerEventListener(UIEvents.MOUSE_DOWN, e -> {
                if (fluidTank.getFluid().getFluid() == Fluids.WATER) {
                    fluidTank.setFluid(new FluidStack(Fluids.LAVA, 1000));
                } else {
                    fluidTank.setFluid(new FluidStack(Fluids.WATER, 1000));
                }
            }),
            // you could also use button.setOnServerClick(e -> { ... })
            new InventorySlots()
    );
    root.addClass("panel_bg");

    // pass the player to the Modular UI
    return ModularUI.of(UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MODERN)), player);
}
Tutorial 7 Result