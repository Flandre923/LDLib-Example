# LDLib2 类型支持完全指南：理解数据同步与持久化的基石

在我使用 LDLib2 进行模组开发的过程中，深刻体会到类型支持系统是整个框架的基石之一。当你给一个字段加上 @DescSynced 或 @Persisted 注解时，框架如何知道如何正确地序列化和同步这个值？答案就在于类型支持系统。LDLlib2 已经内置了大量常见类型的支持，从 Java 原始类型到 Minecraft 特有的游戏对象，再到 LDLib2 自己定义的向量和位置类型。但理解这个系统的工作原理，对于解决复杂问题和自己扩展新类型都至关重要。今天，我想通过这篇博客和大家详细讲解 LDLlib2 的类型支持系统。

## 为什么类型支持如此重要

在深入具体类型之前，我们需要理解类型支持在数据同步和持久化中扮演的角色。简单来说，当 LDLlib2 需要将一个字段的值发送到网络或者保存到 NBT 时，它需要一种方式来将这个值「序列化」成可传输的格式。反过来，当接收数据时，它也需要「反序列化」将数据还原成原来的对象。

这个序列化-反序列化的桥梁就是类型支持系统提供的。不同的类型有不同的特性：有些是不可变的（比如 UUID），有些是可变但有明确的修改方法（比如 ItemStack），有些是集合类型需要特殊处理。没有一套完善类型支持的框架是无法正确工作的。

LDLlib2 的类型支持系统被设计成可扩展的，不仅内置了大量常见类型的支持，还允许开发者注册自定义类型的访问器。这种设计既保证了开箱即用的便利性，又留有足够的扩展空间来应对特殊需求。

## 内置 Java 原始类型支持

LDLlib2 对 Java 语言的基础类型提供了全面的支持，这些类型是大多数模组开发中最常用的数据类型。让我们按照优先级来详细了解这些类型。

Java 的原始类型和它们的包装类都被支持，包括 int 和 Integer、long 和 Long、float 和 Float、double 和 Double、boolean 和 Boolean、byte 和 Byte、short 和 Short、char 和 Character。这些类型在 Java 中用于存储数值和布尔值，是最基本的数据类型。它们的优先级都是 -1，这意味着它们具有较高的处理优先级，优先于其他复杂类型。

String 类型也是基础类型之一，优先级为 -1，用于存储文本数据。枚举类型 Enum<?> 同样被支持，优先级为 -1，这在 Minecraft 中非常常见，比如物品类型、方块状态、方向等都使用枚举表示。

Number 类型是一个抽象类，Integer、Long、Float、Double 等都继承自它。Number 类型的优先级为 1000，这比原始包装类型要低一些。这是因为框架会优先尝试匹配更具体的类型，只有在没有更具体的类型匹配时才会使用 Number。

UUID 类型也被支持，优先级为 100。UUID 在 Minecraft 中用于唯一标识实体、物品等，是非常重要的标识类型。

数组类型 T[] 和集合类型 Collection<?> 同样被支持，它们的优先级都是 -1，但集合类型的只读标志为 true，表示集合类型被视为只读类型。这是很重要的一点——LDLlib2 将集合视为只读类型，意味着你不能直接替换整个集合，而需要通过集合提供的方法来修改内容。

理解这些优先级数字的含义是很重要的。优先级的数值决定了当有多种类型可能匹配时的选择顺序。数值越小优先级越高，框架会优先选择更高优先级的类型。这确保了具体类型（如 Integer）会被优先处理，而不是被更通用的类型（如 Number）拦截。

## Minecraft 游戏类型支持

Minecraft 本身包含大量的游戏对象类型，LDLlib2 对这些类型也提供了完善的支持。这些类型是模组开发中处理游戏逻辑的核心。

物品相关类型得到了强力支持。Item 类型优先级为 100，ItemStack 类型优先级为 1000。ItemStack 是 Minecraft 中表示物品数量的核心类型，它包含物品类型、数量、耐久度等多个属性。ItemStack.OPTIONAL_CODEC 和 ItemStack.OPTIONAL_STREAM_CODEC 提供了对物品栈的完整序列化支持，包括空物品栈的特殊处理。

方块相关类型同样被支持。Block 优先级为 100，BlockState 优先级为 1000，BlockEntityType<?> 优先级为 100。BlockState 特别重要，它表示方块的具体状态，比如橡木楼梯是朝哪个方向放置的、活塞是伸出还是缩回状态等。BlockPos 用于表示世界坐标中的位置，优先级为 1000。

资源标识类型在现代 Minecraft 开发中无处不在。ResourceLocation 优先级为 100，用于标识物品、方块、配方等游戏资源的路径。Tag<T> 优先级为 2000，用于表示游戏中的标签系统，比如哪些方块会被钻石镐采集。

组件系统是 Minecraft 文本渲染的核心，Component 类型优先级为 2000。TextComponent、TranslationComponent 等都继承自 Component，用于在游戏中显示彩色文本。

流体的表示也很重要。Fluid 类型优先级为 100，FluidStack 优先级为 1000。FluidStack 表示一定量的流体，包含流体类型和数量。

INBTSerializable<?> 是一个特殊接口，实现了这个接口的类型可以自定义自己的 NBT 序列化逻辑。LDLib2 对这个接口提供了特殊支持，优先级为 2000，只读标志为 true。这意味着任何实现 INBTSerializable 的类型都可以被 LDLib2 正确处理。

实体相关的类型也得到了支持。EntityType<?> 优先级为 100，用于标识实体类型。IRenderer 接口优先级为 1000，用于渲染相关的类型。AABB 即 Axis-Aligned Bounding Box，用于碰撞检测和范围计算，优先级为 1000。RecipeHolder<?> 用于表示配方持有者，优先级为 1000。

## LDLib2 扩展类型支持

除了 Java 原始类型和 Minecraft 游戏类型，LDLlib2 自己定义了大量有用的类型，用于 UI 渲染、向量计算等场景。这些类型在 UI 开发中特别常用。

位置和尺寸类型是最基础的一组。Position 表示位置，Size 表示尺寸，Pivot 表示锚点，Range 表示范围。这些类型的优先级都是 100。它们在 UI 布局中扮演着重要角色，理解它们对于使用 LDLib2 的 UI 系统至关重要。

向量类型在游戏开发中无处不在，用于表示空间中的点和方向。LDLib2 提供了完整的向量类型支持：Vector3f（三维浮点向量）、Vector4f（四维浮点向量）、Vector2f（二维浮点向量）、Vector2i（二维整数向量）、Quaternionf（四元数，用于旋转计算）。这些类型的优先级都是 1000。

四元数（Quaternionf）特别重要，它用于表示三维空间中的旋转。相比欧拉角（俯仰角、偏航角、滚转角），四元数可以避免万向节死锁问题，是游戏引擎中表示旋转的标准方式。LDLib2 提供了对 Quaternionf 的完整支持，包括专用的 Codec 和 StreamCodec。

纹理和资源相关类型也被支持。IGuiTexture 用于表示 GUI 纹理，IRenderer 用于渲染器，IResourcePath 用于资源路径。这些类型的优先级都是 1000。

IManaged 接口是一个重要的标记接口，实现了这个接口的类型会被 LDLib2 特殊处理，优先级为 1500，只读标志为 true。IManaged 类型通常具有自己的生命周期管理逻辑，LDLib2 会尊重这种管理方式。

## 类型优先级与只读类型：核心概念解析

在理解类型支持系统时，有两个核心概念需要深入理解：类型优先级和只读类型标志。

类型优先级决定了当存在多种可能的类型匹配时，框架如何选择使用哪个类型的访问器。优先级数值越小，优先级越高。例如，Integer 的优先级是 -1，Number 的优先级是 1000，所以当同步一个 Integer 类型的字段时，框架会使用 Integer 的访问器而不是 Number 的访问器。这种设计确保了更具体的类型会被优先处理，不会被更通用的类型「抢占」。

这种优先级机制在实际中很有用。比如，当你有一个 Object 类型的字段，实际存储的是 Integer 值，框架会正确识别并使用 Integer 的序列化方式。当你使用泛型时，比如 List<ItemStack>，框架会先匹配 List 的访问器，然后再根据 ItemStack 的访问器处理列表中的每个元素。

只读类型是 LDLib2 类型系统中的另一个重要概念。只读类型指的是那些在整个生命周期中不可变、且框架不知道如何创建新实例的类型。典型的只读类型包括 INBTSerializable<?> 的实现类（因为它们通常有自己复杂的内部状态）和 Collection<?>（集合类型）。

对于只读类型，LDLlib2 采用了一种特殊的处理方式。由于这些类型的实例不能被简单地「替换」，框架需要通过其他方式来检测和处理它们的变化。这就是为什么只读类型需要特殊访问器的原因。框架通过检查唯一标识符（UID）来判断是否需要同步数据，如果 UID 变化了，才会触发完整的同步流程。

区分直接类型和只读类型对于正确使用 LDLib2 非常重要。直接类型是可以为 null 的类型，在生命周期中有已知的方法可以创建新实例。大多数简单类型如 String、Integer、ItemStack 都是直接类型。只读类型是不可为 null 的，在生命周期中是不可变的，所有修改都需要通过类型自己的 API 来完成。

## 自定义类型支持：扩展无限可能

尽管 LDLlib2 已经内置了大量类型的支持，但在实际开发中，你可能会遇到需要支持新类型的情况。LDLlib2 提供了完善的机制来添加自定义类型支持。

添加新类型支持需要注册一个 IAccessor<TYPE> 类型的访问器。访问器分为两类：直接类型访问器（IDirectAccessor）和只读类型访问器（IReadOnlyAccessor）。大多数情况下，你只需要使用直接类型访问器。

注册访问器的位置没有严格限制，但官方建议在 LDLibPlugin 的 onLoad 方法中注册。这是因为 LDLibPlugin 是 LDLib2 的入口点，在这个阶段注册可以确保访问器在其他模块初始化之前就已经准备好。

在注册访问器时，你需要了解几个重要概念。首先是 codec，它用于持久化（序列化到 NBT）。每个直接类型访问器都需要提供一个 Codec 来处理 NBT 序列化。streamCodec 用于网络同步，它处理数据在服务器和客户端之间的传输。

mark（标记）是类型支持中的一个重要概念。mark 是整个生命周期中的一个快照，LDLib2 会生成当前值的标记，并在后续比较它来确定值是否发生了变化。如果类型内部的值是不可变的（比如 UUID、ResourceLocation），可以不使用自定义标记，系统会存储当前值作为标记。对于可变的类型，你应该提供一种获取标记的方式，以便框架能正确检测变化。

CustomDirectAccessor 提供了几个配置选项来满足不同需求。codec 方法用于设置持久化用的 Codec，streamCodec 方法用于设置同步用的 StreamCodec。copyMark 方法用于提供一种复制当前值作为标记的方式，框架会使用 Objects.equals 来比较标记值，所以确保你的类型正确实现了 equals 方法。customMark 方法允许你提供自定义的获取和比较标记的函数。codecMark 方法使用 JavaOps 基于当前值生成标记。

以下是两个实际注册的例子，展示了如何为不同类型的字段提供支持：

```java
AccessorRegistries.registerAccessor(CustomDirectAccessor.builder(Quaternionf.class)
    .codec(ExtraCodecs.QUATERNIONF)
    .streamCodec(ByteBufCodecs.QUATERNIONF)
    .copyMark(Quaternionf::new)
    .build());
```

```java
AccessorRegistries.registerAccessor(CustomDirectAccessor.builder(ItemStack.class)
    .codec(ItemStack.OPTIONAL_CODEC)
    .streamCodec(ItemStack.OPTIONAL_STREAM_CODEC)
    .customMark(ItemStack::copy, ItemStack::matches)
    .build());
```

对于 Quaternionf，由于它是一个简单的向量类型，框架可以直接复制当前值作为标记。对于 ItemStack，由于它有复杂的内部状态和专门的匹配方法，框架使用了自定义的标记函数来更准确地检测变化。

对于只读类型，一般情况下你不需要自己注册访问器。如果你的类需要只读类型支持，更简单的方法是让它继承 INBTSerializable 接口。如果确实需要实现自定义的只读访问器，可以实现 IReadOnlyAccessor<TYPE> 接口并注册它，具体的用法可以参考代码中的注释说明。

## 实践中的类型选择建议

在我自己的开发实践中，积累了一些关于类型选择的经验和建议。对于简单的配置数据，如数字、字符串、布尔值，直接使用对应的 Java 类型即可。对于 Minecraft 游戏对象，如物品、方块、流体，优先使用框架内置支持的对应类型，这样可以减少很多潜在的兼容性问题。

在使用集合类型时要注意，Collection<?> 在 LDLib2 中被视为只读类型。这意味着你不能直接替换整个集合，只能通过集合提供的方法（如 add、remove）来修改内容。如果你确实需要替换整个集合，可能需要使用可变的包装类型或者使用数组类型。

对于自定义的数据结构，如果需要在服务器和客户端之间同步，建议实现 INBTSerializable 接口。这样你可以完全控制序列化逻辑，同时也能利用 LDLib2 对这个接口的内置支持。

在使用向量类型时，根据你的实际需求选择合适的类型。如果只需要二维坐标，使用 Vector2f 或 Vector2i；如果涉及三维空间，使用 Vector3f；如果需要表示旋转，使用 Quaternionf。这些类型在 LDLib2 的 UI 和渲染系统中被广泛使用，框架提供了完整的支持。

LDLlib2 的类型支持系统是整个框架能够优雅处理数据同步和持久化的基础。理解这个系统不仅能帮助你更好地使用框架，还能在遇到问题时更快地定位和解决问题。希望这篇博客能帮助你建立对 LDLib2 类型支持系统的全面认识。