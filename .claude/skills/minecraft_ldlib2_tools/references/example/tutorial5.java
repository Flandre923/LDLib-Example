Tutorial 5: data bindings
LDLib2 provides built-in data binding support for most data-driven UI components. This allows UI elements to stay in sync with underlying data without manual update logic. The binding system is based on IObserver<T> and IDataProvider<T>. Check data bindings page for more details.

In this example:

A shared AtomicInteger acts as the single source of truth
Buttons modify the value directly
The TextField updates the value through an observer
Labels and the progress bar automatically refresh when the data changes

private static ModularUI createModularUI() {
    // a value holder
    var valueHolder = new AtomicInteger(0);

    var root = new UIElement();
    root.addChildren(
            new Label().setText("Data Bindings")
                    .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),
            new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)).addChildren(
                    // button to decrease the value
                    new Button().setText("-")
                            .setOnClick(e -> {
                                if (valueHolder.get() > 0) {
                                    valueHolder.decrementAndGet();
                                }
                            }),
                    new TextField()
                            .setNumbersOnlyInt(0, 100)
                            .setValue(String.valueOf(valueHolder.get()))
                            // bind an Observer to update the value holder
                            .bindObserver(value -> valueHolder.set(Integer.parseInt(value)))
                            // bind a DataSource to notify the value changes
                            .bindDataSource(SupplierDataSource.of(() -> String.valueOf(valueHolder.get())))
                            .layout(layout -> layout.flex(1)),
                    // button to increase the value
                    new Button().setText("+")
                            .setOnClick(e -> {
                                if (valueHolder.get() < 100) {
                                    valueHolder.incrementAndGet();
                                }
                            })
            ),
            // bind a DataSource to notify the value changes for label and progress bar
            new Label().bindDataSource(SupplierDataSource.of(() -> Component.literal("Binding: ").append(String.valueOf(valueHolder.get())))),
            new ProgressBar()
                    .setProgress(valueHolder.get() / 100f)
                    .bindDataSource(SupplierDataSource.of(() -> valueHolder.get() / 100f))
                    .label(label -> label.bindDataSource(SupplierDataSource.of(() -> Component.literal("Progress: ").append(String.valueOf(valueHolder.get())))))
    ).style(style -> style.background(Sprites.BORDER));
    root.layout(layout -> layout.width(100).paddingAll(7).gapAll(5));
    return ModularUI.of(UI.of(root));
}
Tutorial 5 Result