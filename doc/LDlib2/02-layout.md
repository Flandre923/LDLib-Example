
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


