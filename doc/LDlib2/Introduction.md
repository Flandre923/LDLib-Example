# LDlib2 是什么？

这个视频已经介绍的很清晰了，这里就不在过多说了。
https://www.bilibili.com/video/BV1fzvDBhEBV/?spm_id_from=333.1365.list.card_archive.click


这是wiki地址：
https://low-drag-mc.github.io/LowDragMC-Doc/ldlib2/java_integration/

这里的内容也大多来自wiki

# 安装

安装也很简单。
```groovy
repositories {
    // LDLib2
    maven { url = "https://maven.firstdark.dev/snapshots" } 
}

dependencies {
    // LDLib2
    implementation("com.lowdragmc.ldlib2:ldlib2-neoforge-${minecraft_version}:${ldlib2_version}:all") { transitive = false }
    compileOnly("org.appliedenergistics.yoga:yoga:1.0.0")   
}

```

# 快速开始

## tutorial 1：创建一个展示ModularUI
ModularUI 是UI的运行时管理器 -- 处理生命周期和交互。
接受一个UI 实例和一个可选的玩家作为输入。

[Tutorial1UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial1UIContainer.java)
```java
// 定义一个用于演示基础 UI 容器的类
public class Tutorial1UIContainer {
    // 创建 ModularUI 的静态工厂方法，传入 Player 对象以支持 Menu 同步
    public static ModularUI createModularUI(Player player) {
        // 1. 创建根元素 (Root Element)，它是所有其他 UI 组件的父级容器
        var root = new UIElement();
        // 2. 创建一个标签 (Label) 组件用于显示文本
        var title = new Label();
        title.setText("My First UI"); // 设置显示的文字内容
        root.addChild(title);         // 将标签添加到根容器中
        // 3. 创建一个按钮 (Button) 组件
        var button = new Button();
        button.setText("Click Me!");  // 设置按钮上显示的文字
        root.addChild(button);        // 将按钮添加到根容器中
        // 4. 创建一个通用的 UIElement 来显示图标
        var icon = new UIElement();
        icon.layout(layout -> layout
                .width(80)            // 设置图标宽度为 80 像素
                .height(80)           // 设置图标高度为 80 像素
        ).style(style -> style.background(
                // 使用指定的资源路径加载图片作为背景
                SpriteTexture.of("ldlib2:textures/gui/icon.png"))
        );
        // 5. 将图标添加到根容器，并为根容器设置背景
        root.addChild(icon);
        // 使用内置的 Sprites.BORDER 样式作为根容器的背景边框
        root.style(basicStyle -> basicStyle.background(Sprites.BORDER));
        // 6. 将根元素封装进 UI 实例中
        var ui = UI.of(root);
        // 7. 返回 ModularUI 实例
        // 如果 player 不为空，则创建支持服务端同步的 Menu UI
        if (player != null) {
            return ModularUI.of(ui, player);
        }
        // 否则返回一个仅在客户端渲染的普通 UI
        return ModularUI.of(ui);
    }
}
```

LDLib2不强制使用专门Screen类，LDLib2可以选择任何的Screen，ModularUI提供了通用的交互和解决方案。

[Tutorial1Screen.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial1Screen.java)
```java
public class Tutorial1Screen extends Screen {

    private final ModularUI modularUI;

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

```

一个item类，用于打开对应的screen
```java

public class Tutorial1Item extends Item {

    public static final Supplier<Tutorial1Item> INSTANCE = () -> new Tutorial1Item(
            new Properties()
                    .stacksTo(1)
    );

    public Tutorial1Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var level = context.getLevel();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            var modularUI = Tutorial1UIContainer.createModularUI(player);
            Minecraft.getInstance().setScreen(new Tutorial1Screen(modularUI));
        }

        return InteractionResult.CONSUME;
    }
}

```
![img.png](img.png)

##  Tutorial 2 更好的布局和样式
可以运行了，但是样式和布局的效果并不理想，通过padding 内边距，更好的布局。
首先将Label 居中对齐。

```java

private static ModularUI createModularUI() {
    // 1. 创建一个根元素（Root Element），作为所有 UI 组件的容器
    var root = new UIElement();

    // 2. 向根元素中添加子组件
    root.addChildren(
            // 添加一个文本标签（Label）用于显示文字
            new Label().setText("My First UI")
                    // 设置文字样式：水平居中对齐
                    .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),

            // 添加一个带有文本内容的按钮（Button）
            new Button().setText("Click Me!"),

            // 添加一个普通的 UIElement 来显示图片
            new UIElement()
                    // 设置该元素的布局：宽度 80，高度 80
                    .layout(layout -> layout.width(80).height(80))
                    // 设置该元素的背景：指定资源路径下的图片纹理
                    .style(style -> style.background(
                            SpriteTexture.of("ldlib2:textures/gui/icon.png"))
                    )
    // 为整个根元素（外壳）设置背景，这里使用的是内置的边框样式（Sprites.BORDER）
    ).style(style -> style.background(Sprites.BORDER));

    // 3. 设置根元素的布局参数
    // paddingAll(7): 设置全方位的内边距为 7，防止内容紧贴边框
    // gapAll(5): 设置子元素（标签、按钮、图片）之间的间距为 5
    root.layout(layout -> layout.paddingAll(7).gapAll(5));

    // 4. 将根元素封装成一个 UI 对象
    var ui = UI.of(root);

    // 5. 返回一个 ModularUI 实例，它是 Minecraft 运行时处理交互的最终对象
    return ModularUI.of(ui);
}
```
[Tutorial2Item.java](../../src/main/java/com/example/examplemod/item/tutorial/Tutorial2Item.java)

[Tutorial2Screen.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial2Screen.java)

[Tutorial2UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial2UIContainer.java)

![img_2.png](img_2.png)


## Tutorial 3 组件交互和UI事件
钮组件提供了 setOnClick() 方法。
```java
private static ModularUI createModularUI() {
    // create a root element
    var root = new UIElement();
    // add an element to display an image based on a resource location
    var image = new UIElement().layout(layout -> layout.width(80).height(80))
            .style(style -> style.background(
                    SpriteTexture.of("ldlib2:textures/gui/icon.png"))
            );
    root.addChildren(
            // add a label to display text
            new Label().setText("Interaction")
                    // center align text
                    .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),
            image,
            // add a container with the row flex direction
            new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)).addChildren(
                    // a button to rotate the image -45°
                    new Button().setText("-45°")
                            .setOnClick(e -> image.transform(transform -> 
                                    transform.rotation(transform.rotation()-45))),
                    new UIElement().layout(layout -> layout.flex(1)), // occupies the remaining space
                    // a button to rotate the image 45°
                    new Button().setText("+45°")
                            .setOnClick(e -> image.transform(transform -> 
                                    transform.rotation(transform.rotation() + 45)))
            )
    ).style(style -> style.background(Sprites.BORDER)); // set a background for the root element
    // set padding and gap for children elements
    root.layout(layout -> layout.paddingAll(7).gapAll(5));
    // create a UI
    var ui = UI.of(root);
    // return a modular UI for runtime instance
    return ModularUI.of(ui);
}
```
[Tutorial3UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial3UIContainer.java)
[Tutorial3Screen.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial3Screen.java)
[Tutorial3Item.java](../../src/main/java/com/example/examplemod/item/tutorial/Tutorial3Item.java)

![img_1.png](img_1.png)

除了Button#setOnClick() 来处理交互，外LDLib2 还提供了一套完整灵活的UI事件系统
任何 UIElement（UI 元素）都可以监听输入事件，例如鼠标点击、悬停、指令、生命周期、拖拽、焦点、键盘输入等。

```java
private static ModularUI createModularUI() {
    // create a root element
    var root = new UIElement();
    // add an element to display an image based on a resource location
    var image = new UIElement().layout(layout -> layout.width(80).height(80))
            .style(style -> style.background(
                    SpriteTexture.of("ldlib2:textures/gui/icon.png"))
            );
    root.addChildren(
            // add a label to display text
            new Label().setText("UI Event")
                    // center align text
                    .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),
            image,
            // add a container with the row flex direction
            new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)).addChildren(
                    // implement the button by using ui events
                    new UIElement().addChild(new Label().setText("-45°").textStyle(textStyle -> textStyle.adaptiveWidth(true)))
                            .layout(layout -> layout.justifyItems(YogaJustify.CENTER).paddingHorizontal(3))
                            .style(style -> style.background(Sprites.BORDER1))
                            .addEventListener(UIEvents.MOUSE_DOWN, e -> image.transform(transform ->
                                    transform.rotation(transform.rotation()-45)))
                            .addEventListener(UIEvents.MOUSE_ENTER, e ->
                                    e.currentElement.style(style -> style.background(Sprites.BORDER1_DARK)), true)
                            .addEventListener(UIEvents.MOUSE_LEAVE, e ->
                                    e.currentElement.style(style -> style.background(Sprites.BORDER1)), true),
                    new UIElement().layout(layout -> layout.flex(1)), // occupies the remaining space
                    // a button to rotate the image 45°
                    new Button().setText("+45°")
                            .setOnClick(e -> image.transform(transform ->
                                    transform.rotation(transform.rotation() + 45)))
            )
    ).style(style -> style.background(Sprites.BORDER)); // set a background for the root element
    // set padding and gap for children elements
    root.layout(layout -> layout.paddingAll(7).gapAll(5));
    // create a UI
    var ui = UI.of(root);
    // return a modular UI for runtime instance
    return ModularUI.of(ui);
}

```
[Tutorial3UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial3UIContainer.java)

![img_3.png](img_3.png)

## Tutorial 4 UI 样式

类似CSS，可以将UI的样式和代码进行分离，从而更好的维护。

示例 1 展示了如何直接在 UI 元素上绑定 LSS。

示例 2 展示了如何定义一个独立的样式表文件并将其应用到 UI 中。

```java
private static ModularUI createModularUI() {
    // 1. 创建根元素容器
    var root = new UIElement();

    // 2. 向根容器中添加子组件
    root.addChildren(
            // 创建一个标签，并使用 LSS 设置水平对齐方式为居中
            new Label().setText("LSS example")
                    .lss("horizontal-align", "center"),

            // 创建一个标准按钮
            new Button().setText("Click Me!"),

            // 创建一个通用的 UI 元素（用于显示图标）
            new UIElement()
                    // 使用 LSS 声明宽度和高度
                    .lss("width", 80)
                    .lss("height", 80)
                    // 使用 LSS 加载指定路径的图片作为背景
                    // sprite(...) 是 LSS 内部的函数语法
                    .lss("background", "sprite(ldlib2:textures/gui/icon.png)")
    );

    // 3. 为根容器配置全局样式和布局属性
    // 设置根容器背景为内置的 GDP 风格边框
    root.lss("background", "built-in(ui-gdp:BORDER)");

    // 设置根容器的内边距 (Padding) 为 7 像素
    root.lss("padding-all", 7);

    // 设置子元素之间的间距 (Gap) 为 5 像素
    root.lss("gap-all", 5);

    // 4. 将根元素封装为 UI 对象
    var ui = UI.of(root);

    // 5. 返回 ModularUI 实例（此处未传入 player，默认为纯客户端 UI）
    return ModularUI.of(ui);
}
```

```java
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
```
[Tutorial4Screen.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial4Screen.java)

[Tutorial4UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial4UIContainer.java)

[Tutorial4Item.java](../../src/main/java/com/example/examplemod/item/tutorial/Tutorial4Item.java)

除了自定义 LSS 定义外，LDLib2 还提供了几种 **内置样式表（Built-in Stylesheets）** 主题，涵盖了大多数常见的 UI 组件：

- StylesheetManager.GDP
- StylesheetManager.MC （原生 Minecraft 风格）
- StylesheetManager.MODERN （现代感设计风格）

这些内置样式表允许你以最少的配置，为整个 UI 应用统一的视觉风格。你可以通过 StylesheetManager 来访问和管理它们，该管理器作为所有可用样式表包的中央注册表（Central Registry）。

通过使用内置样式表，你可以快速让你的模组 UI 看起来像原版 Minecraft（使用 MC 主题），或者拥有更现代、更精致的外观（使用 MODERN 或 GDP 主题），而无需从头编写任何 CSS。

```java
public static ModularUI createModularUI3() {

    // 创建根容器
    var root = new UIElement();
    // 设置根容器宽度为 100 像素
    root.layout(layout -> layout.width(100));

    root.addChildren(
            // 添加文本标签
            new Label().setText("样式表 (Stylesheets)"),
            // 添加标准按钮
            new Button().setText("点我！"),
            // 添加进度条：设置进度为 0.5，并在其中显示文字 "Progress"
            new ProgressBar().setProgress(0.5f).label(label -> label.setText("进度")),
            // 添加开关/切换按钮
            new Toggle().setText("切换开关"),
            // 添加文本输入框
            new TextField().setText("文本框"),
            // 创建一个水平排列 (ROW) 的容器，包含物品槽和流体槽
            new UIElement().layout(layout -> layout.setFlexDirection(YogaFlexDirection.ROW)).addChildren(
                    // 物品槽：默认放置一个苹果
                    new ItemSlot().setItem(Items.APPLE.getDefaultInstance()),
                    // 流体槽：设置装有 1000mB 水
                    new FluidSlot().setFluid(new FluidStack(Fluids.WATER, 1000))
            ),

            // --- 核心逻辑：列出所有样式表并创建选择器 ---
            new Selector<ResourceLocation>()
                    // 默认选中 GDP 主题（不触发变更事件）
                    .setSelected(StylesheetManager.GDP, false)
                    // 获取样式表管理器中所有已注册的主题作为候选项
                    .setCandidates(StylesheetManager.INSTANCE.getAllPackStylesheets().stream().toList())
                    // 当选中的值发生改变时
                    .setOnValueChanged(selected -> {
                        // 切换到所选的样式表
                        var mui = root.getModularUI();
                        if (mui != null) {
                            // 1. 清除当前 UI 引擎中的所有样式表
                            mui.getStyleEngine().clearAllStylesheets();
                            // 2. 添加玩家新选择的样式表
                            mui.getStyleEngine().addStylesheet(StylesheetManager.INSTANCE.getStylesheetSafe(selected));
                        }
                    })
    );

    // 给根元素添加 "panel_bg" 类名，样式表会自动为其渲染面板背景
    root.addClass("panel_bg");

    // 默认使用 GDP 样式表创建 UI
    var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP));

    // 返回 ModularUI 实例
    return ModularUI.of(ui);
}

```

## Tutorial 5 数据绑定

LDlib2 提供了内置的数据绑定，使得可以和底层数据保持一致，无需繁琐的手动更新逻辑。
该绑定逻辑基于IObserver<T>(观察者) 和 IDataProvider<T>(数据提供者)

在本示例中：
- 使用一个共享的 AtomicInteger 作为唯一事实来源（Single Source of Truth）。
- **按钮（Buttons）**直接修改该数值。
- **文本输入框（TextField）**通过观察者同步更新该数值。
- **标签（Labels）和进度条（Progress Bar）**在数据发生变化时会自动刷新显示。

[Tutorial5UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial5UIContainer.java)
[Tutorial5Screen.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial5Screen.java)
[Tutorial5Item.java](../../src/main/java/com/example/examplemod/item/tutorial/Tutorial5Item.java)

```java

public static ModularUI createModularUI(Player player) {
    // 1. 定义一个“事实来源”：使用 AtomicInteger 来存储数值（0-100）
    // 这样在 Lambda 表达式中可以方便地进行读写操作
    var valueHolder = new AtomicInteger(0);

    var root = new UIElement();
    root.addChildren(
            // 标题标签，水平居中
            new Label().setText("Data Bindings")
                    .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),

            // 2. 创建水平行：包含 [ - 按钮 ] [ 输入框 ] [ + 按钮 ]
            new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW)).addChildren(
                    // 减少数值的按钮
                    new Button().setText("-")
                            .setOnClick(e -> {
                                if (valueHolder.get() > 0) {
                                    valueHolder.decrementAndGet();
                                }
                            }),

                    // 文本输入框：演示双向绑定
                    new TextField()
                            .setNumbersOnlyInt(0, 100) // 限制只能输入 0-100 的整数
                            .setValue(String.valueOf(valueHolder.get())) // 设置初始值
                            // 【双向绑定 - 从 UI 到 数据】：当玩家在框里输入内容时，更新 valueHolder
                            .bindObserver(value -> valueHolder.set(Integer.parseInt(value)))
                            // 【双向绑定 - 从 数据 到 UI】：当 valueHolder 改变时，自动刷新框内的文字
                            .bindDataSource(SupplierDataSource.of(() -> String.valueOf(valueHolder.get())))
                            .layout(layout -> layout.flex(1)), // 占据中间剩余所有空间

                    // 增加数值的按钮
                    new Button().setText("+")
                            .setOnClick(e -> {
                                if (valueHolder.get() < 100) {
                                    valueHolder.incrementAndGet();
                                }
                            })
            ),

            // 3. 演示单向绑定：标签自动显示当前数值
            // 只要 valueHolder 发生变化，Label 的文字会自动刷新
            new Label().bindDataSource(SupplierDataSource.of(() -> 
                Component.literal("Binding: ").append(String.valueOf(valueHolder.get())))),

            // 4. 演示进度条绑定
            new ProgressBar()
                    .setProgress(valueHolder.get() / 100f)
                    // 绑定进度值：数据改变时，进度条长度自动增减
                    .bindDataSource(SupplierDataSource.of(() -> valueHolder.get() / 100f))
                    // 绑定进度条上的文字：同步显示百分比或数值
                    .label(label -> label.bindDataSource(SupplierDataSource.of(() -> 
                        Component.literal("Progress: ").append(String.valueOf(valueHolder.get())))))
    ).style(style -> style.background(Sprites.BORDER)); // 设置背景边框

    // 5. 根容器布局设置
    root.layout(layout -> layout.width(100).paddingAll(7).gapAll(5));

    return ModularUI.of(UI.of(root));
}
```
![img_4.png](img_4.png)


## Tutorial 6 Modular UI for menu
在之前的几个例子中，主要关注在客户端 Screen（屏幕）中渲染 ModularUI。这对于纯视觉或仅限客户端的界面非常有效。

我的世界的GUI是需要服务端和客户端数据同步的。当GUI涉及到游戏数据持久化或者逻辑处理时候，数据需要以服务端为准，这通常是Menu来负责的。

LDlib2 为服务端Menu提供了一系类的支持。你看与在menu中使用ModularUI。而无需编写额外的数据同步的代码。

让我们创建一个简单的基于 Menu 的 UI，用于显示玩家的物品栏。

```java
private static ModularUI createModularUI(Player player) {
    // 1. 创建根元素容器
    var root = new UIElement();

    // 2. 向根容器中添加子组件
    root.addChildren(
            // 添加一个简单的文本标签，显示 "Menu UI"
            new Label().setText("Menu UI"),

            // 3. 【核心组件】添加玩家物品栏槽位
            // 这个内置组件会自动生成玩家快捷栏（9格）和背包（27格）的 UI 布局及同步逻辑
            new InventorySlots()

            // 4. 为根容器应用名为 "panel_bg" 的样式类
            // 这将根据加载的样式表自动渲染面板的背景和边框
    ).addClass("panel_bg");

    // 5. 将 root 包装进 UI 实例中，并指定默认使用 GDP 样式表（主题）
    var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP));

    // 6. 【关键点】将 player 对象传入 ModularUI.of
    // 只有传入了 player 参数，LDLib2 才会创建一个“服务端支持”的 ModularUI 实例，
    // 从而使 InventorySlots 能够与服务器端的实际玩家背包进行数据同步。
    return ModularUI.of(ui, player);
}
```
你必须通过 Player 对象来创建 ModularUI，这对于基于 Menu 的 UI（服务端同步界面）是必不可少的。
此外，不仅是客户端的 Screen，你也应当为服务端的 Menu 初始化 ModularUI。
初始化操作应当在实例创建之后、向额外数据缓冲区（Extra Data Buffer）写入数据之前完成。

```java
// 定义你自己的菜单类，继承自原版的 AbstractContainerMenu
public class MyContainerMenu extends AbstractContainerMenu {

    // 你可以在构造函数中执行初始化操作
    public MyContainerMenu(...) {
        // 调用父类构造函数（处理同步 ID 和玩家背包等）
        super(...)

        // 1. 调用之前定义的静态方法创建一个 ModularUI 实例
        var modularUI = createModularUI(player);

        // 2. 核心逻辑：LDLib2 通过 Mixin 技术让原版的 AbstractContainerMenu 
        // 自动实现了 IModularUIHolderMenu 接口
        if (this instanceof IModularUIHolderMenu holder) {
            // 3. 将创建好的 ModularUI 实例注入到当前 Menu 中
            // 这样 LDLib2 就能接管该菜单的槽位同步和数据绑定逻辑
            holder.setModularUI(modularUI);
        }
    }

    // ..... 其他 Menu 逻辑（如 quickMoveStack 等）
}
```
[Tutorial6Menu.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial6Menu.java)
[Tutorial6Screen.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial6Screen.java)
[Tutorial6UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial6UIContainer.java)
[Tutorial6Item.java](../../src/main/java/com/example/examplemod/item/tutorial/Tutorial6Item.java)

## Tutorial 7 communication
Screen 与 Menu 之间的通信
虽然 InventorySlots（物品栏槽位）可以开箱即用，但它们属于预封装的内置组件。在实际项目中，你往往需要更精准地控制数据和事件在客户端 Screen 与服务端 Menu 之间的流动方式。

ModularUI 为跨客户端和服务器的**数据绑定（Data Bindings）及事件分发（Event Dispatch）**提供全面支持。这使得客户端上的 UI 交互能够安全地触发服务端逻辑，同时服务端的内部状态变化也能自动更新 UI。欲了解更多细节，请参阅“数据绑定”页面。


```java
// 以下代表存在于服务端（Server）的数据
// 创建一个包含 2 个槽位的物品处理器
private final ItemStackHandler itemHandler = new ItemStackHandler(2);
// 创建一个容量为 2000mB 的流体罐
private final FluidTank fluidTank = new FluidTank(2000);
// 定义基础类型的变量（用于同步演示）
private boolean bool = true;
private String string = "hello";
private float number = 0.5f;

private static ModularUI createModularUI(Player player) {
    // 1. 创建一个根元素容器
    var root = new UIElement();
    root.addChildren(
            // 添加一个标签显示标题文本
            new Label().setText("Data Between Screen and Menu"),

            // 2. 将存储容器（物品/流体）绑定到 UI 槽位
            new UIElement().addChildren(
                    // 将第一个物品槽绑定到 itemHandler 的第 0 号索引
                    new ItemSlot().bind(itemHandler, 0),
                    // 绑定第 1 号索引，并设置为“玩家不可取出”
                    new ItemSlot().bind(new ItemHandlerSlot(itemHandler, 1).setCanTake(p -> false)),
                    // 将流体槽绑定到流体罐
                    new FluidSlot().bind(fluidTank, 0)
                    // 设置布局：间距为 2，水平排列
            ).layout(l -> l.gapAll(2).flexDirection(YogaFlexDirection.ROW)),

            // 3. 将变量数值绑定到对应的 UI 组件
            new UIElement().addChildren(
                    // 绑定开关：同步布尔值（读写双向）
                    new Switch().bind(DataBindingBuilder.bool(() -> bool, value -> bool = value).build()),
                    // 绑定文本框：同步字符串（读写双向）
                    new TextField().bind(DataBindingBuilder.string(() -> string, value -> string = value).build()),
                    // 绑定滚动条：同步浮点数（读写双向）
                    new Scroller.Horizontal().bind(DataBindingBuilder.floatVal(() -> number, value -> number = value).build()),

                    // 4. 只读绑定 (服务端 -> 客户端)
                    // 始终从服务端获取最新的数据拼接成组件，并在客户端显示（玩家不可修改）
                    new Label().bind(DataBindingBuilder.componentS2C(() -> Component.literal("s->c only: ")
                                    .append(Component.literal(String.valueOf(bool)).withStyle(ChatFormatting.AQUA)).append(" ")
                                    .append(Component.literal(string).withStyle(ChatFormatting.RED)).append(" ")
                                    .append(Component.literal("%.2f".formatted(number)).withStyle(ChatFormatting.YELLOW)))
                            .build())
                    // 设置布局：间距为 2
            ).layout(l -> l.gapAll(2)),

            // 5. 在服务端触发 UI 事件
            // 当玩家在客户端点击此按钮时，逻辑会在服务端执行（切换流体罐里的流体）
            new Button().addServerEventListener(UIEvents.MOUSE_DOWN, e -> {
                if (fluidTank.getFluid().getFluid() == Fluids.WATER) {
                    fluidTank.setFluid(new FluidStack(Fluids.LAVA, 1000));
                } else {
                    fluidTank.setFluid(new FluidStack(Fluids.WATER, 1000));
                }
            }),

            // 6. 添加快捷栏和背包物品槽（原版功能复刻）
            new InventorySlots()
    );

    // 给根元素添加样式类名“panel_bg”
    root.addClass("panel_bg");

    // 7. 将 player 对象传递给 Modular UI 实例
    // 这一步至关重要，它标志着该 UI 是一个具有服务端同步能力的“Menu”界面
    return ModularUI.of(UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MODERN)), player);
}
```

![img_5.png](img_5.png)
[Tutorial7Screen.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial7Screen.java)
[Tutorial7Item.java](../../src/main/java/com/example/examplemod/item/tutorial/Tutorial7Item.java)
[Tutorial7UIContainer.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial7UIContainer.java)
[Tutorial7Menu.java](../../src/main/java/com/example/examplemod/gui/tutorial/Tutorial7Menu.java)


--- 
part2 

# 预备知识
##  基础知识
1. UIEleemnt：万物皆是“砖块” 
在 Java 里，一切皆对象；在 LDLib2 里，一切皆 UIElement。 不管是按钮、文字、还是进度条，它们本质上都是UIElement。
   - 你可以套娃：UIEleemnt里可以塞UIEleemnt（addChild）。
2. Layout:给UIEleemnt 做装饰
   -  LDLib2 引入了网页开发里著名的 Flexbox  逻辑。
   - 不用算坐标：x=10, y=20
   - 下指令：你只需要跟Layout说：“把这三个UIEleemnt横着排（ROW），中间留点缝（gap），撑满整个房间（flex(1)）。”
   - 自动适应：不管你的游戏窗口是 4:3 还是带鱼屏，Layout会自动帮你把UIEleemnt挪到合适的位置。
3. LSS（样式表）
   - 代码归代码，外观归外观：你可以写一个专门的 .lss 文件（就像 CSS 文件）。
   - 在代码里给砖头贴个标签 root.addClass("大理石风格")。以后你想把全模组的界面从“石头色”改成“猛男粉”，只需要改一下 LSS 文件，一行 Java 代码都不用动。
4. Data Binding（数据绑定）
   - 以前你要在 UI 里显示机器的电量，你得不停地写： label.setText(machine.getEnergy())
   - 现在你只需要“牵根线”：把 UI 组件和你的变量连起来（bind）。
   - 变量变，UI 变：只要你的机器电量（变量）一动，UI 上的进度条和数字会自动跟着跳，就像装了同步传感器一样。
5. Screen 与 Menu
   - 在 Minecraft 里，UI 有个最头疼的问题：客户端看到的只是假象，服务端才是真理。
   - Screen：仅做数据的展示，修改其数值不影响服务器端的内容。
   - Menu：是服务器里管逻辑的大脑（服务端）。
   - LDLib2 ：它在两者之间开了相关的同步MAgic，你在 createModularUI(player) 里传个 player 过去，你在屏幕上点一下按钮，服务器那边的数据就跟着变了。你不需要自己处理那些网络逻辑了。

## Modular UI
1. 什么是 ModularUI？（大管家的身份）

在 Java 里，你可能习惯了写一个 Main 类来启动程序。在 LDLib2 的 UI 世界里，ModularUI 就是那个 总负责人。
虽然你创建了很多（UIElement）、画了很多样式（LSS），但要把这些东西变成一个真正能点、能看、能跟服务器聊天的窗口，必须由 ModularUI 来统筹。

这个“ModularUI”主要负责五件事：
   1. 管生命周期：决定 UI 什么时候出生（打开），什么时候死掉（关闭）。
   2. 管翻译：把玩家的键盘按键、鼠标点击，翻译成组件能听懂的“事件”。
   3. 管装修：拿着你的 LSS 样式表，给每个组件涂颜色、定位置。
   4. 管画画：每秒钟几十次地把 UI 渲染到你的屏幕上。
   5. 管两界沟通：最牛的功能！它像一个“时空隧道”，把服务器里的数据同步到你的屏幕上。


2. 如何创建ModularUI
   你只需要记住两个工厂方法，区别就在于“需不需要同步服务器数据”：
- ModularUI.of(ui)
  - 适用场景：纯客户端 UI（比如游戏的设置界面、单机用的说明书）。
  - 特点：简单快捷，不需要考虑网络数据同步。
- ModularUI.of(ui, player)
  - 适用场景：容器、机器、背包（比如箱子界面）。
  - 特点：必须传入一个 Player。它会在后台悄悄开启“同步模式”，保证你屏幕上看到的电量/物品和服务器里的一模一样。

3. ModularUI常用的API
   - 行为控制（基础设置）
     - shouldCloseOnEsc()：按 ESC 键时，这个 UI 关不关？（默认都是关的）。
     - getTickCounter()：这个 UI 已经打开多久了？（用来做动画非常方便）。
   - 找人专用（元素查询）
     - 按 ID 找：getElementById("my_id")
     - 模糊找：getElementsByIdStartsWith("btn_")
     - 按类型找：getElementsByType(Button.class)

> 这些方法返回的是列表的副本。你随便改列表，不会弄乱 UI 树本身的结构。

4. UI 调试模式
F3,开启 UI Debug 模式。你会看到屏幕上出现各种框框和线条，告诉你每个组件的边界在哪、叫什么名字、占了多少像素。就像开了“透视外挂”一样，排查错误极快。
![img_6.png](img_6.png)

## Layout
### 什么是Layout？
####  Layout的本质
我的世界原版的情况是：按钮位置不好调，文本框大小不合适，组件排列总是歪歪扭扭。这些问题的根源在于——
每一个元素都需要你规定好他的位置，计算开始的坐标，结束的坐标。

而LDLib2的Layout系统，可以自动计算每个组件（按钮、文本、图片）应该有多大。每个组件应该放在什么位置。多个组件之间应该如何相处。
#### Yoga引擎
LDLib2的布局系统使用了Yoga引擎。
Yoga负责计算算每个组件该多大、该在哪。
#### FlexBox
Yoga使用的是FlexBox布局模型。FlexBox是前端领域非常流行的布局思想，核心思想很简单：
把界面看作一个个可以伸缩的"盒子"，这些盒子可以横向排列也可以纵向排列，还可以自动适应空间变化。

### UI元素与层级结构
#### 什么是UIElement
在LDLib2中，所有的UI组件都是UIElement。不管是一个按钮、一个标签、一张图片，还是一个面板，本质上都是一个UIElement。
```java
UIElement element = new UIElement();
```
UIElement就像一个空的"容器"，你可以往里面放东西，也可以设置它的外观和行为。

#### 层级结构
UI界面不是平面的，而是一层套一层的。想象一个俄罗斯套娃，最外层是最大的娃娃，里面套着稍小的，再里面还有更小的。每个"娃娃"就是一个UIElement，里面可以包含其他UIElement。
```
┌─────────────────────────────────────────┐
│  Window (窗口)                          │
│  ┌───────────────────────────────────┐  │
│  │  Panel (面板)                      │  │
│  │  ┌───────────┐  ┌───────────┐    │  │
│  │  │  Button   │  │  Button   │    │  │
│  │  └───────────┘  └───────────┘    │  │
│  │              ┌───────────┐       │  │
│  │              │  Label    │       │  │
│  │              └───────────┘       │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```
这种嵌套结构就是UI层级（Layout Tree）。父组件负责"管教"子组件——子组件的位置和大小，往往由父组件的布局规则来决定。
#### 为什么需要层级?
分层管理让复杂界面变得可控。

#### 定位模式
每个UIElement都有两种"性格"可以选择：相对定位和绝对定位。这两种定位方式决定了组件如何与父容器相处。
##### 相对定位（Relative）
相对定位是默认模式，也是最常用的模式。选择相对定位的组件，会完全听从父容器的安排。
```java
// 设置为相对定位（实际上这是默认值，可以不写）
var element = new UIElement().layout(layout -> layout.positionType(YogaPositionType.RELATIVE);
```
相对定位的特点：
- 它会参与FlexBox布局:父容器怎么排布子组件，它就怎么跟着走。父容器说"横着排"，它就横着站；父容器说"竖着排"，它就竖着站。
- 它会响应父容器的"规矩".父容器设置了对齐方式、内边距、外边距等规则，相对定位的子组件都会遵守。
- 它的最终位置和大小由"协商"决定.不是子组件说了算，也不是父容器说了算，而是双方"商量"出来的。如果子组件想要的空间比父容器能给的更多，布局引擎会想办法协调（可能让其他组件让出空间，或者允许溢出）。

##### 绝对定位（Absolute）
绝对定位的组件完全不参与FlexBox布局，就像一个不听话的孩子，父母的话它当耳旁风。
```java
// 设置为绝对定位
var element = new UIElement().layout(layout -> layout.positionType(YogaPositionType.Absolute);
```
- 第一，它无视FlexBox规则。父容器设置什么flexDirection、alignItems，对它完全无效。它想怎么排就怎么排。
- 第二，它不响应Grow、Shrink等Flex属性。这些属性是为相对定位设计的，绝对定位组件当作没看见。
- 第三，它可能覆盖其他组件。因为它不参与布局计算，所以位置完全由自己决定，可能会和其他组件"撞车"，相互覆盖。
- 第四，位置由"偏移量"控制。要控制绝对定位组件的位置，需要设置上、右、下、左四个方向的偏移量。
#### 尺寸设置
每个组件都有自己的"身材"——宽度和高度。LDLib2提供了丰富的尺寸控制方式，让你能够精确或灵活地定义组件大小。
##### 4.1 基础尺寸：Width和Height

```java
// 设置固定像素值
var element = new UIElement().layout(layout -> layout.width(80).height(80));
```

##### 尺寸限制：Min和Max
有时候我们希望组件"有弹性"，但弹性要有边界。比如一个按钮可以变大，但不能超过某个尺寸：
```java
var element = new UIElement().layout(layout -> layout.width(80).height(80));
```
这就像人的身高：成年后基本定型（基础尺寸），但也不能太矮（最小限制），也不能无限长高（最大限制）。
##### 宽高比：AspectRatio
如果你想让组件保持一定的"身材比例"，可以用AspectRatio：
```java
var element = new UIElement().layout(layout -> layout.aspectRatio(2.0f));
```
这在处理图片、视频等需要保持比例的场景非常有用。比如一个16:9的视频播放器，设置好宽度后，高度自动计算，不用担心画面变形。
##### 溢出控制：Overflow
组件内容太多、放不下怎么办？Overflow属性决定如何处理：
```java
// 默认：内容超出也不隐藏，显示在外面
var element = new UIElement().layout(layout -> layout.overflow(YogaOverflow.VISIBLE));
// 超出部分裁剪掉
var element = new UIElement().layout(layout -> layout.overflow(YogaOverflow.HIDDEN));

```
一个快递盒（组件），里面装了太多东西（内容）。Visible模式下，东西会从盒子里"冒出来"；Hidden模式下，冒出来的部分被"切掉"，看不见了。

#####  容器特性
默认情况下，UIElement是"容器"。这意味着什么？
- 如果你不设置具体尺寸，它会"看情况"决定大小
- 有空间时，它会尽可能撑满（"能屈"）
- 没内容时，它会"塌陷"成最小尺寸（"能伸"）

#### Flex设置
Flex相关属性是FlexBox布局的核心，也是相对定位组件的"杀手锏"。这部分需要重点理解，因为它直接决定了组件如何分配空间。


#####  Flex Basis
Flex Basis定义组件的初始尺寸，在FlexGrow和FlexShrink之前"打底"。
```java
var element = new UIElement().layout(layout -> layout.flexBasisPercent(80));
```
想象一群人排队领救济粮。FlexBasis就是你"理论"上应该领到的份额——80%的粮食。但这只是理论，实际领多少还要看后面两个因素。

##### Flex Grow
当父容器有多余空间时，FlexGrow决定子组件如何"分赃"：

```java
// 值为0：不扩张（不参与分配）
var element = new UIElement().layout(layout -> layout.flexGrow(0));
// 值大于0：参与空间分配，值越大分的越多
var element = new UIElement().layout(layout -> layout.flexGrow(1)); // 普通份额
var element = new UIElement().layout(layout -> layout.flexGrow(2)); // 双份份额

```
举个例子：父容器宽度500px，有三个子组件A、B、C
A的flexGrow=1，B的flexGrow=1，C的flexGrow=2
假设A和B初始宽度100，C初始宽度100
剩余空间（500-300=200）按照1:1:2的比例分配
A最终宽度：100 + 200×(1/4) = 150
B最终宽度：100 + 200×(1/4) = 150
C最终宽度：100 + 200×(2/4) = 200
##### Flex Shrink
当父容器空间不够用时，FlexShrink决定子组件如何"共患难"：
```java
// 值为0：不收缩（硬汉类型，宁死不屈）
var element = new UIElement().layout(layout -> layout.flexShrink(0));
// 值大于0：参与收缩，值越大收缩越多
var element = new UIElement().layout(layout -> layout.flexShrink(1));
```
注意：如果组件设置了固定像素尺寸（比如width(100)），FlexShrink对它无效——因为固定尺寸的组件"寸土不让"。
##### Flex属性
Flex是Grow和Shrink的"二合一"快捷键：

```java
// 这两行是等价的
var element = new UIElement().layout(layout -> layout.flex(1));
var element = new UIElement().layout(layout -> layout.flexGrow(1).flexShrink(1));

```
Flex=1的意思是：默认情况下匀称分配，有福同享有难同当。

##### 尺寸计算顺序
当使用相对定位时，布局引擎是按以下"流程"算账的：
第一步：算基础尺寸。根据你设置的width、height算出"理论尺寸"。
第二步：看父容器脸色。检查父容器是有余粮还是有缺口。
第三步：分配余粮（Grow）。有余粮时，按flexGrow值分配。
第四步：分担缺口（Shrink）。有缺口时，按flexShrink值分担。
第五步：套上枷锁（约束）。应用min、max、flexBasis等限制。
第六步：尘埃落定。确定最终尺寸。

#### Flex Direction与Wrapping

##### Flex Direction
FlexDirection决定子组件的排列方向：
```java
// 横向排列（从左到右，默认）
var element = new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW));
// 纵向排列（从上到下）
var element = new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.COLUMN));
// 横向排列（从右到左，类似阿拉伯文排版）
var element = new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.ROW_REVERSE));
// 纵向排列（从下到上）
var element = new UIElement().layout(layout -> layout.flexDirection(YogaFlexDirection.COLUMN_REVERSE));
```
#####  Flex Wrap
```java

// 不换行，强行挤在一行（可能溢出）
var element = new UIElement().layout(layout -> layout.flexWrap(YogaFlexWrap.NOWRAP));

// 换行，多出的放到下一行/列
var element = new UIElement().layout(layout -> layout.flexWrap(YogaFlexWrap.WRAP));

// 反向换行（从反方向开始排）
var element = new UIElement().layout(layout -> layout.flexWrap(YogaFlexWrap.WRAP_REVERSE));
```
一行能排3个组件，我有5个
NOWRAP：5个挤在1行，可能超出容器边界
WRAP：3个在第一行，2个在第二行

#### 对齐方式
对齐属性控制子组件在容器中的"站位"，这在CSS中也是重点难点。
##### Justify Content
JustifyContent控制子组件在主轴方向上的对齐方式。什么是主轴？如果FlexDirection是ROW，主轴就是水平方向；如果是COLUMN，主轴就是垂直方向。
```java

// 从起点开始依次排列（默认）
var element = new UIElement().layout(layout -> layout.justifyContent(YogaFlexWrap.FLEX_START));

// 从终点开始依次排列
var element = new UIElement().layout(layout -> layout.justifyContent(YogaFlexWrap.FLEX_END));

// 居中排列
var element = new UIElement().layout(layout -> layout.justifyContent(YogaFlexWrap.CENTER));

// 两端对齐，中间均匀分布
var element = new UIElement().layout(layout -> layout.justifyContent(YogaFlexWrap.SPACE_BETWEEN));

// 每个项目两侧间距相等（首尾也有间距）
var element = new UIElement().layout(layout -> layout.justifyContent(YogaFlexWrap.SPACE_AROUND));

// 均匀分布，首尾间距是的一半
var element = new UIElement().layout(layout -> layout.justifyContent(YogaFlexWrap.SPACE_EVENLY));
```
用排队等公交来理解：
- FLEX_START：车来了，大家往车头方向挤
- FLEX_END：车门在车尾，大家往车尾挤
- CENTER：大家站中间
- SPACE_BETWEEN：首尾两人贴车门，中间的人均匀站
- SPACE_AROUND：每人两边都有空，但首尾空小
- SPACE_EVENLY：所有空都一样大
#####  Align Items
AlignItems控制子组件在交叉轴方向上的对齐方式。交叉轴与主轴垂直：如果主轴是水平方向，交叉轴就是垂直方向。
```java
// 自动（继承父容器设置，默认）
var element = new UIElement().layout(layout -> layout.alignItems(YogaAlign.AUTO));

// 靠起点对齐
var element = new UIElement().layout(layout -> layout.alignItems(YogaAlign.FLEX_START));

// 靠终点对齐
var element = new UIElement().layout(layout -> layout.alignItems(YogaAlign.FLEX_END));

// 居中对齐
var element = new UIElement().layout(layout -> layout.alignItems(YogaAlign.CENTER));

// 基线对齐（按文字底线对齐）
var element = new UIElement().layout(layout -> layout.alignItems(YogaAlign.BASELINE));

// 拉伸填满交叉轴方向
var element = new UIElement().layout(layout -> layout.alignItems(YogaAlign.STRETCH));
```
想象一排身高不一的人站横队：
- FLEX_START：都把头顶对齐（最矮的那个会空出一截）
- FLEX_END：都把脚底对齐
- CENTER：都把身体中间对齐
- STRETCH：每个都"拉长"到同样高度
##### Align Self
AlignSelf允许单个子组件"搞特殊"，覆盖父容器的AlignItems设置：
```java
var element = new UIElement().layout(layout -> layout.alignSelf(YogaAlign.FLEX_END));
```

#####  Align Content
AlignContent只有在使用flexWrap且产生多行/多列时才生效，它控制各行/各列本身的对齐方式。

```java 
// 多行内容作为一个整体的对齐方式
// 靠上/左
var element = new UIElement().layout(layout -> layout.alignSelf(YogaAlign.FLEX_END));
// 居中
var element = new UIElement().layout(layout -> layout.alignSelf(YogaAlign.FLEX_END));
// 两端分布
var element = new UIElement().layout(layout -> layout.alignSelf(YogaAlign.FLEX_END));
```
#### Marging与Padding
Margin和Padding是UI布局中的"距离制造机"，类似于CSS的盒模型。
##### LDLib2的盒模型

LDLib2遵循类似CSS的盒模型，从内到外分为四层：
```
┌─────────────────────────────────────┐
│             Margin（外边距）          │  ← 组件与其他组件的间距
│   ┌─────────────────────────────┐   │
│   │         Border（边框）        │   │  ← 可选的边界线（文档建议避免使用）
│   │   ┌─────────────────────┐   │   │
│   │   │      Padding        │   │   │  ← 组件边界与内容的间距
│   │   │  ┌───────────────┐  │   │   │
│   │   │  │    Content    │  │   │   │  ← 实际内容（文字、图片等）
│   │   │  └───────────────┘  │   │   │
│   │   └─────────────────────┘   │   │
│   └─────────────────────────────┘   │
└─────────────────────────────────────┘
```
##### Padding
Padding是组件内部的留白，让内容不贴边：


```java 
// 四周统一设置
var element = new UIElement().layout(layout -> layout.paddingAll(10));

// 分别设置四个方向
var element = new UIElement().layout(layout -> layout.paddingTop(10)
        .paddingBottom(10)
        .paddingStart(10)
        .paddingEnd(10));

// 用百分比
var element = new UIElement().layout(layout -> layout.paddingPercent(5));
```
##### Margin
```java
// 四周统一设置
var element = new UIElement().layout(layout -> layout.marginAll(10));

// 分别设置
var element = new UIElement().layout(layout -> layout.marginTop(10)
        .marginBottom(10)
        .marginStart(10)
        .marginEnd(10));
```

##### 注意事项
Border建议避免使用：文档特别提到Border是可选项，而且建议避免使用。在LDLib2中，你完全可以用Padding和背景色来实现类似效果。
溢出问题：如果组件设置了固定width/height，再加上margin、padding，可能会导致总尺寸超过父容器。布局引擎会根据情况处理——可能溢出、可能压缩内容、可能调整其他组件。
#### 设置布局的三种方式
####   方式一：直接调用API
```java
UIElement element = new UIElement();

element.getLayout()
    .flexDirection(YogaFlexDirection.ROW)
    .width(150)
    .heightPercent(100)
    .marginAll(10)
    .paddingAll(10);

```

##### 方式二：链式Lambda（推荐）
```java

UIElement element = new UIElement();

element.layout(layout -> layout
    .flexDirection(YogaFlexDirection.ROW)
    .width(150)
    .heightPercent(100)
    .marginAll(10)
    .paddingAll(10)
);
```
##### 方式三：LSS样式表（推荐用于复杂界面）
```java
element.lss("flex-direction", "row");
element.lss("width", 150);
element.lss("height-percent", 100);
element.lss("margin-all", 10);
element.lss("padding-all", 10);
```