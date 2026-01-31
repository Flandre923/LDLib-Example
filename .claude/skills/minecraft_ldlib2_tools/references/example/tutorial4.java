Tutorial 4: UI stylesheet
In Tutorial 2, we improved the layout and visual appearance by configuring layout and style directly in code. While this works well, inline layout and style definitions can quickly become verbose and harder to maintain as the UI grows.

To address this, LDLib2 introduces a stylesheet system called LSS (LDLib2 StyleSheet). LSS allows you to describe layout and style properties in a declarative, CSS-like way, separating visual design from UI structure. Check stylesheet page for more details.

In the examples below, we reimplement the layout and style logic from Step 3 using LSS:

Example 1 demonstrates LSS bindings directly on UI elements
Example 2 shows how to define a standalone stylesheet and apply it to the UI

example 1
example 2

private static ModularUI createModularUI() {
    // set root with an ID
    var root = new UIElement().setId("root");
    root.addChildren(
            new Label().setText("LSS example"),
            new Button().setText("Click Me!"),
            // set the element with a class
            new UIElement().addClass("image")
    );
    var lss = """
        // id selector
        #root {
            background: built-in(ui-gdp:BORDER);
            padding-all: 7;
            gap-all: 5;
        }

        // class selector
        .image {
            width: 80;
            height: 80;
            background: sprite(ldlib2:textures/gui/icon.png);
        }

        // element selector
        #root label {
            horizontal-align: center;
        }
        """;
    var stylesheet = Stylesheet.parse(lss);
    // add stylesheets to ui
    var ui = UI.of(root, stylesheet);
    return ModularUI.of(ui);
}

Built-in Stylesheets

In addition to custom LSS definitions, LDLib2 also provides several built-in stylesheet themes that cover most common UI components:

StylesheetManager.GDP
StylesheetManager.MC
StylesheetManager.MODERN
These built-in stylesheets allow you to apply a consistent visual style to an entire UI with minimal setup. You can access and manage them through the StylesheetManager, which acts as a central registry for all available stylesheet packs.


private static ModularUI createModularUI() {
    var root = new UIElement();
    root.layout(layout -> layout.width(100));
    root.addChildren(
            new Label().setText("Stylesheets"),
            new Button().setText("Click Me!"),
            new ProgressBar().setProgress(0.5f).label(label -> label.setText("Progress")),
            new Toggle().setText("Toggle"),
            new TextField().setText("Text Field"),
            new UIElement().layout(layout -> layout.setFlexDirection(YogaFlexDirection.ROW)).addChildren(
                    new ItemSlot().setItem(Items.APPLE.getDefaultInstance()),
                    new FluidSlot().setFluid(new FluidStack(Fluids.WATER, 1000))
            ),
            // list all stylesheets
            new Selector<ResourceLocation>()
                    .setSelected(StylesheetManager.GDP, false)
                    .setCandidates(StylesheetManager.INSTANCE.getAllPackStylesheets().stream().toList())
                    .setOnValueChanged(selected -> {
                        // switch to the selected stylesheet
                        var mui = root.getModularUI();
                        if (mui != null) {
                            mui.getStyleEngine().clearAllStylesheets();
                            mui.getStyleEngine().addStylesheet(StylesheetManager.INSTANCE.getStylesheetSafe(selected));
                        }
                    })
    );
    root.addClass("panel_bg");
    // use GDP stylesheets by default
    var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP)));
    return ModularUI.of(ui);
}