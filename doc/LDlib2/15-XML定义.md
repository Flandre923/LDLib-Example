# LDLib2 XML UI 元素详解

本文档介绍 LDLib2 中所有可用的 XML UI 元素，包括每个元素的属性、作用和使用方法。

## 目录

- [概述](#概述)
- [通用属性](#通用属性)
- [基础元素 (Basic)](#基础元素-basic)
- [容器元素 (Container)](#容器元素-container)
- [库存元素 (Inventory)](#库存元素-inventory)
- [工具元素 (Utils)](#工具元素-utils)
- [其他元素 (Misc)](#其他元素-misc)
- [特殊元素](#特殊元素)
- [完整示例](#完整示例)

---

## 概述

LDLib2 使用 XML 格式定义 UI 界面。XML 文件需要符合以下结构：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ldlib2-ui xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:noNamespaceSchemaLocation="https://raw.githubusercontent.com/Low-Drag-MC/LDLib2/refs/heads/1.21/ldlib2-ui.xsd">
    <stylesheet location="ldlib2:lss/mc.lss"/>
    <root id="main-panel" class="panel_bg" style="width: 200; height: 300">
        <!-- 子元素 -->
    </root>
</ldlib2-ui>
```

### 加载 XML

```java
// 从字符串加载
Document document = XmlUtils.loadXml(xmlString);
UI ui = UI.of(document);

// 从资源文件加载
Document document = XmlUtils.loadXml(ResourceLocation.parse("mymod:ui/main.xml"));
```

---

## 通用属性

以下属性对所有 UI 元素都有效：

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `id` | 字符串 | 空 | 元素的唯一标识符，用于样式选择和事件绑定 |
| `class` | 字符串 | 空 | LSS 类名，多个类用空格分隔 |
| `style` | 字符串 | 空 | 内联样式，格式：`属性:值; 属性:值;` |
| `visible` | 布尔值 | `true` | 控制元素是否可见 |
| `focusable` | 布尔值 | `false` | 控制元素是否可以获得焦点 |
| `active` | 布尔值 | `true` | 控制元素是否处于激活状态 |

### 可用的内联样式属性

| 属性 | 说明 | 示例 |
|------|------|------|
| `width` | 宽度 | `width: 100` |
| `height` | 高度 | `height: 50` |
| `flex` | 弹性因子 | `flex: 1` |
| `flex-direction` | 排列方向 | `flex-direction: ROW` / `COLUMN` |
| `justify-content` | 主轴对齐 | `justify-content: CENTER` |
| `align-items` | 交叉轴对齐 | `align-items: CENTER` |
| `padding` | 内边距 | `padding: 5` |
| `padding-left/right/top/bottom` | 各方向内边距 | `padding-left: 10` |
| `margin` | 外边距 | `margin: 5` |
| `margin-left/right/top/bottom` | 各方向外边距 | `margin-top: 10` |
| `display` | 显示模式 | `display: FLEX` / `NONE` |
| `background` | 背景纹理 | `background: mymod:textures/gui/button.png` |
| `opacity` | 不透明度 | `opacity: 0.5` |
| `z-index` | 层级 | `z-index: 10` |

---

## 基础元素 (Basic)

### 1. button - 按钮

**说明**：可点击的按钮元素，支持点击事件和三种状态（默认/悬停/按下）。

**XML 标签**：`<button>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `text` | 字符串 | 按钮上显示的文本 |
| `translate` | 布尔值 | 是否将 text 作为翻译键（默认 false） |

**示例**：
```xml
<button text="Click Me" style="width: 100; height: 30"/>
<button text="gui.button.save" style="margin: 5"/> <!-- 翻译键 -->
```

---

### 2. label - 标签

**说明**：用于显示文本的标签元素，支持数据绑定自动更新文本。

**XML 标签**：`<label>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `text` | 字符串 | 标签显示的文本 |
| `translate` | 布尔值 | 是否翻译 |

**示例**：
```xml
<label text="Hello World"/>
<label class="title" style="font-size: 16; color: #FFFFFF"/>
```

---

### 3. text - 文本元素

**说明**：更灵活的文本显示元素，支持多种文本样式和自动换行。

**XML 标签**：`<text>`

**文本样式属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `text-wrap` | 字符串 | 文本换行模式：`NONE`/`WRAP`/`ROLL`/`HOVER_ROLL`/`HIDE` |
| `text-align` | 字符串 | 水平对齐：`LEFT`/`CENTER`/`RIGHT` |
| `vertical-align` | 字符串 | 垂直对齐：`TOP`/`CENTER`/`BOTTOM` |
| `font-size` | 数值 | 字体大小 |
| `font` | 资源位置 | 字体资源 |
| `text-color` | 颜色值 | 文本颜色（十六进制） |
| `text-shadow` | 布尔值 | 是否显示阴影 |
| `adaptive-width` | 布尔值 | 是否自适应宽度 |
| `adaptive-height` | 布尔值 | 是否自适应高度 |
| `line-spacing` | 数值 | 行间距 |

**示例**：
```xml
<text text="这是一段很长的文本..."
      style="text-wrap: WRAP; width: 200; text-color: #FF0000"/>
<text text="滚动的文字"
      style="text-wrap: ROLL; font-size: 14"/>
```

---

### 4. text-field - 文本输入框

**说明**：用户可以输入文本的输入框，支持多种输入模式。

**XML 标签**：`<text-field>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `value` | 字符串 | 输入框的初始值 |
| `mode` | 字符串 | 输入模式：`STRING`/`NUMBER_INT`/`NUMBER_LONG`/`NUMBER_FLOAT`/`NUMBER_DOUBLE`/`RESOURCE_LOCATION`/`COMPOUND_TAG` |
| `regex-validator` | 字符串 | 正则表达式验证器（仅 STRING 模式） |
| `placeholder` | 字符串 | 占位符文本 |
| `min` | 数值 | 最小值（数字模式） |
| `max` | 数值 | 最大值（数字模式） |

**示例**：
```xml
<!-- 普通文本输入 -->
<text-field value="" style="width: 200; height: 20"/>

<!-- 数字输入框 -->
<text-field mode="NUMBER_INT" min="0" max="100" value="50"/>

<!-- 资源位置输入 -->
<text-field mode="RESOURCE_LOCATION"/>

<!-- 带验证的输入 -->
<text-field regex-validator="^[a-zA-Z0-9_]+$"/>
```

---

### 5. toggle - 开关/复选框

**说明**：二元状态的开关按钮，可单独使用或组合成组。

**XML 标签**：`<toggle>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `text` | 字符串 | 开关旁边的标签文本 |
| `is-on` | 布尔值 | 初始开关状态 |
| `translate` | 布尔值 | 是否翻译文本 |

**示例**：
```xml
<toggle text="启用功能" is-on="true"/>
<toggle text="Show Advanced Options"/>
```

---

### 6. switch - 滑动开关

**说明**：类似移动端的滑动开关组件。

**XML 标签**：`<switch>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `is-on` | 布尔值 | 初始状态 |

**示例**：
```xml
<switch is-on="false" style="width: 40; height: 20"/>
```

---

### 7. selector - 选择器

**说明**：下拉选择器，用于从多个选项中选择一个。

**XML 标签**：`<selector>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `options` | 字符串 | 选项列表，用逗号分隔 |
| `selected` | 字符串 | 默认选中项 |

**示例**：
```xml
<selector options="Option A,Option B,Option C" selected="Option A"/>
```

---

### 8. progress-bar - 进度条

**说明**：显示进度百分比的组件。

**XML 标签**：`<progress-bar>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `progress` | 数值 | 当前进度值（0-100） |
| `direction` | 字符串 | 填充方向：`RIGHT`/`DOWN`/`LEFT`/`UP` |

**示例**：
```xml
<progress-bar progress="50" style="width: 200; height: 20"/>
```

---

### 9. text-area - 多行文本区域

**说明**：支持多行文本输入的区域。

**XML 标签**：`<text-area>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `value` | 字符串 | 初始内容 |
| `rows` | 数值 | 行数 |
| `editable` | 布尔值 | 是否可编辑 |

**示例**：
```xml
<text-area value="多行文本..." rows="5" style="width: 300"/>
```

---

### 10. tag-field - 标签字段

**说明**：输入标签/标签词的组件。

**XML 标签**：`<tag-field>`

**示例**：
```xml
<tag-field style="width: 200"/>
```

---

### 11. search-component - 搜索组件

**说明**：带有搜索图标的输入框组件。

**XML 标签**：`<search-component>`

**示例**：
```xml
<search-component placeholder="搜索..." style="width: 200"/>
```

---

## 容器元素 (Container)

### 12. graph-view - 图形视图

**说明**：可缩放和平移的图形/画布容器。

**XML 标签**：`<graph-view>`

**示例**：
```xml
<graph-view style="flex: 1; width: 500; height: 400">
    <!-- 子元素将显示在图形视图中 -->
</graph-view>
```

---

### 13. scroller-view - 滚动视图

**说明**：带有滚动条的容器，当内容超出大小时显示滚动条。

**XML 标签**：`<scroller-view>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `scroll-x` | 布尔值 | 是否允许水平滚动（默认 true） |
| `scroll-y` | 布尔值 | 是否允许垂直滚动（默认 true） |

**示例**：
```xml
<scroller-view style="width: 200; height: 300">
    <label text="很长的内容..."/>
    <!-- 更多子元素 -->
</scroller-view>
```

---

### 14. tab-view - 标签页视图

**说明**：多标签页容器，通过 tab 元素切换内容。

**XML 标签**：`<tab-view>`

**示例**：
```xml
<tab-view style="width: 400; height: 300">
    <tab text="Tab 1">
        <label text="Tab 1 内容"/>
    </tab>
    <tab text="Tab 2">
        <label text="Tab 2 内容"/>
    </tab>
</tab-view>
```

---

### 15. split-view-horizontal - 水平分割视图

**说明**：水平分割两个区域的容器，用户可调整分割比例。

**XML 标签**：`<split-view-horizontal>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `percentage` | 数值（0-100） | 初始分割比例 |

**示例**：
```xml
<split-view-horizontal percentage="50">
    <label text="左侧区域"/>
    <label text="右侧区域"/>
</split-view-horizontal>
```

---

### 16. split-view-vertical - 垂直分割视图

**说明**：垂直分割两个区域的容器。

**XML 标签**：`<split-view-vertical>`

**示例**：
```xml
<split-view-vertical percentage="30">
    <label text="顶部区域"/>
    <label text="底部区域"/>
</split-view-vertical>
```

---

## 库存元素 (Inventory)

### 17. item-slot - 物品槽

**说明**：Minecraft 风格的物品槽，支持物品存取和快捷移动。

**XML 标签**：`<item-slot>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `allow-xei-lookup` | 布尔值 | 是否允许 XEI 查找（默认 true） |
| `item` | 字符串 | 编辑器中显示的物品（用于设计时） |
| `show-tooltips` | 布尔值 | 是否显示物品提示 |

**示例**：
```xml
<!-- 物品槽 -->
<item-slot style="width: 18; height: 18"/>

<!-- 带有预设物品的槽（仅编辑器显示） -->
<item-slot item="minecraft:diamond" style="width: 18; height: 18"/>
```

---

### 18. fluid-slot - 流体槽

**说明**：用于显示和操作流体的槽，支持流体显示和容器交互。

**XML 标签**：`<fluid-slot>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `capacity` | 数值 | 流体容量（毫升） |
| `allow-xei-lookup` | 布尔值 | 是否允许 XEI 查找 |
| `fill-direction` | 字符串 | 填充方向：`BOTTOM_TO_TOP`/`TOP_TO_BOTTOM`/`LEFT_TO_RIGHT`/`RIGHT_TO_LEFT` |
| `show-tooltips` | 布尔值 | 是否显示流体提示 |

**示例**：
```xml
<fluid-slot capacity="1000" style="width: 18; height: 18"/>
```

---

### 19. inventory-slots - 物品栏

**说明**：完整的玩家物品栏（9x4 槽位）。

**XML 标签**：`<inventory-slots>`

**示例**：
```xml
<inventory-slots style="width: 162; height: 72"/>
```

---

## 工具元素 (Utils)

### 20. scroller-vertical - 垂直滚动条

**说明**：独立的垂直滚动条组件。

**XML 标签**：`<scroller-vertical>`

**示例**：
```xml
<scroller-vertical style="width: 10; height: 100"/>
```

---

### 21. scroller-horizontal - 水平滚动条

**说明**：独立的水平滚动条组件。

**XML 标签**：`<scroller-horizontal>`

**示例**：
```xml
<scroller-horizontal style="width: 100; height: 10"/>
```

---

### 22. tab - 标签页

**说明**：用于 tab-view 中的单个标签页。

**XML 标签**：`<tab>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `text` | 字符串 | 标签标题 |
| `translate` | 布尔值 | 是否翻译标题 |

**示例**：
```xml
<tab text="设置">
    <label text="设置内容"/>
</tab>
```

---

### 23. toggle-group - 开关组

**说明**：管理多个互斥的 toggle 元素。

**XML 标签**：`<toggle-group>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `allow-empty` | 布尔值 | 是否允许不选中任何项 |

**示例**：
```xml
<toggle-group allow-empty="false">
    <toggle text="选项 A" is-on="true"/>
    <toggle text="选项 B"/>
    <toggle text="选项 C"/>
</toggle-group>
```

---

### 24. bindable-value - 可绑定值

**说明**：用于数据绑定的通用值容器。

**XML 标签**：`<bindable-value>`

**示例**：
```xml
<bindable-value style="flex: 1"/>
```

---

### 25. tree-list - 树形列表

**说明**：可展开/折叠的树形结构列表。

**XML 标签**：`<tree-list>`

**示例**：
```xml
<tree-list style="width: 200; height: 300"/>
```

---

## 其他元素 (Misc)

### 26. color-selector - 颜色选择器

**说明**：用于选择颜色的组件。

**XML 标签**：`<color-selector>`

**示例**：
```xml
<color-selector style="width: 200; height: 20"/>
```

---

### 27. code-editor - 代码编辑器

**说明**：支持语法高亮的代码编辑器。

**XML 标签**：`<code-editor>`

**特有属性**：

| 属性 | 类型 | 说明 |
|------|------|------|
| `language` | 字符串 | 语法高亮语言：`xml`/`json`/`java`/`lua`等 |

**示例**：
```xml
<code-editor language="lua" style="width: 500; height: 400"/>
```

---

### 28. scene - 场景

**说明**：用于渲染 3D 场景的容器。

**XML 标签**：`<scene>`

**示例**：
```xml
<scene style="width: 200; height: 200"/>
```

---

### 29. inspector - 检查器

**说明**：用于调试和检查 UI 元素的工具。

**XML 标签**：`<inspector>`

**示例**：
```xml
<inspector style="width: 300; height: 400"/>
```

---

### 30. template - 模板

**说明**：可重用的 UI 模板定义。

**XML 标签**：`<template>`

**示例**：
```xml
<template id="my-button">
    <button text="Template Button" style="width: 100"/>
</template>
```

---

## 特殊元素

### root - 根元素

每个 UI 必须有一个 `<root>` 元素作为所有其他元素的容器。

```xml
<root class="main-panel" style="width: 400; height: 300">
    <button text="OK"/>
    <button text="Cancel"/>
</root>
```

---

### style - 内联样式

在 XML 中直接定义 CSS 样式规则：

```xml
<style>
    .title {
        font-size: 18;
        color: #FFFFFF;
        font-weight: bold;
    }
    .button {
        background: mymod:textures/gui/button.png;
    }
</style>
```

---

### stylesheet - 外部样式表

引入外部 LSS 样式表文件：

```xml
<!-- 引入内置样式表 -->
<stylesheet location="ldlib2:lss/mc.lss"/>
<stylesheet location="ldlib2:lss/modern.lss"/>

<!-- 引入自定义样式表 -->
<stylesheet location="mymod:lss/mystyle.lss"/>
```

---

### internal - 内部子元素

标记为内部元素，不会被编辑器修改：

```xml
<root>
    <internal index="0">
        <label text="内部标签"/>
    </internal>
    <button text="普通按钮"/>
</root>
```

---

## 完整示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ldlib2-ui xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:noNamespaceSchemaLocation="https://raw.githubusercontent.com/Low-Drag-MC/LDLib2/refs/heads/1.21/ldlib2-ui.xsd">
    <stylesheet location="ldlib2:lss/mc.lss"/>
    <style>
        .panel {
            background: mymod:textures/gui/panel.png;
        }
        .title {
            font-size: 18;
            color: #FFD700;
        }
    </style>

    <root id="main-panel" class="panel" style="width: 400; height: 300; padding: 10">
        <!-- 标题 -->
        <label id="title" class="title" text="设置面板"/>

        <!-- 输入区域 -->
        <text-field id="name-input" placeholder="输入名称..."
                    style="width: 380; height: 20; margin: 5"/>

        <!-- 开关选项 -->
        <toggle id="enable-feature" text="启用功能" is-on="true"
                style="margin: 5"/>

        <!-- 物品槽网格 -->
        <scroller-view style="width: 380; height: 100; margin: 5">
            <split-view-horizontal percentage="30">
                <item-slot item="minecraft:diamond" style="width: 18; height: 18"/>
                <item-slot item="minecraft:iron_ingot" style="width: 18; height: 18"/>
                <item-slot item="minecraft:gold_ingot" style="width: 18; height: 18"/>
            </split-view-horizontal>
        </scroller-view>

        <!-- 按钮区域 -->
        <ui-element layout="flex-direction: ROW; justify-content: FLEX-END; margin: 10">
            <button id="cancel-btn" text="取消" style="margin: 5"/>
            <button id="save-btn" text="保存" style="margin: 5"/>
        </ui-element>
    </root>
</ldlib2-ui>
```

---

## 常用样式表位置

LDLib2 内置了以下样式表：

| 样式表 | 路径 | 说明 |
|--------|------|------|
| Minecraft 风格 | `ldlib2:lss/mc.lss` | 原版 MC 风格 |
| 现代风格 | `ldlib2:lss/modern.lss` | 现代扁平风格 |
| GDP 风格 | `ldlib2:lss/gdp.lss` | GDP 编辑器风格 |

---

## 事件绑定

虽然 XML 本身不直接支持事件绑定，但可以通过 Java 代码查找元素并添加事件监听器：

```java
// 查找元素
ui.select("#save-btn").findFirst().ifPresent(button -> {
    button.addEventListener(UIEvents.MOUSE_DOWN, event -> {
        // 处理点击事件
        saveData();
    });
});

// 或者使用 RPC 在服务端处理
button.setOnServerClick(event -> {
    // 服务端逻辑
    handleSave();
});
```
