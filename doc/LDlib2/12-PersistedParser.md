# LDLib2 PersistedParser 详解：让序列化变得优雅而简单

在我多年从事 Minecraft 模组开发的历程中，数据序列化是一个反复出现的主题。无论是保存方块实体的状态、存储玩家数据、还是实现配置文件系统，序列化都是不可或缺的基础功能。传统的手动序列化方式不仅代码冗长，还容易出错——每次添加或修改字段都要同步更新读写逻辑，一不小心就会引入 Bug。而 LDLib2 的 PersistedParser 彻底改变了这一局面，它允许你通过注解来声明需要序列化的字段，然后自动生成序列化代码。这种声明式的编程方式让代码更加清晰，维护更加轻松。今天，我想通过这篇博客和大家详细讲解 PersistedParser 的强大功能。

## 为什么需要 PersistedParser

在深入技术细节之前，让我们先理解一下 PersistedParser 试图解决的问题。序列化在模组开发中无处不在，但你是否曾经为以下情况感到困扰：你有一个复杂的数据类，包含十几个字段，每次添加新字段都要手动更新读写代码；你需要在不同的格式之间转换数据，比如 NBT、JSON、或者 Minecraft 的 Codec 格式；你想要一个统一的方式来处理持久化和配置，而不需要为每种情况写重复的代码。

传统的做法是为每个类实现 INBTSerializable 接口，手动编写 read 和 write 方法。这在字段较少时还能接受，但随着项目增长，代码会变得臃肿且难以维护。更糟糕的是，读取和写入的代码必须保持同步——如果你在 write 中忘记了一个字段，在 read 中就可能读取到错误的默认值。

PersistedParser 的出现正是为了解决这些痛点。它利用 Java 注解和反射机制，自动分析类的字段并生成序列化逻辑。你只需要在字段上标记 @Persisted 或 @Configurable 注解，框架就会自动处理剩余的事情。这种方式不仅减少了代码量，更重要的是减少了出错的可能性——框架生成的代码是稳定和一致的，不会出现手写代码中常见的遗漏或错误。

## 序列化的基础：Serialize 和 Deserialize

PersistedParser 最基本的功能是对象的序列化和反序列化。序列化是将对象转换成可存储或传输的格式，反序列化则是这个过程的逆操作。PersistedParser 支持多种输出格式，包括 JSON（通过 JsonOps）和 NBT。

让我通过一个具体的例子来展示这个过程。首先定义一个测试数据类，它实现了 IPersistedSerializable 接口，并使用 @Persisted 和 @Configurable 注解标记需要序列化的字段：

```java
@EqualsAndHashCode
public class TestData implements IPersistedSerializable {
    @Persisted
    private float numberFloat = 0.0f;
    @Configurable
    private boolean booleanValue = false;
    @Persisted(key = "key")
    private String stringValue = "default";

    public TestData(float initialValue) {
        this.numberFloat = initialValue;
    }
}
```

在这个类中，numberFloat 字段使用 @Persisted 标记，表示它需要被持久化。booleanValue 字段使用 @Configurable 标记，这通常用于配置相关的字段。stringValue 字段同样使用 @Persisted，但通过 key 参数指定了在序列化数据中的键名为 "key"，而不是使用字段名作为键名。

IPersistedSerializable 接口是 LDLib2 提供的一个标记接口，它告诉框架这个类支持通过 PersistedParser 进行序列化。实现这个接口只需要很少的代码，但它打开了使用 PersistedParser 的大门。

现在让我们看看如何进行序列化和反序列化：

```java
var instance = new TestData(100f);
var output = PersistedParser.serialize(JsonOps.INSTANCE, instance, provider).result().get();
System.out.println(output);

var newInstance = new TestData(0f);
var data = PersistedParser.deserialize(JsonOps.INSTANCE, output, newInstance, provider);
System.out.println(newInstance.equals(instance));
```

首先创建一个 TestData 实例，初始值为 100f。然后调用 PersistedParser.serialize 方法进行序列化。JsonOps.INSTANCE 表示使用 JSON 格式作为输出，provider 是 LDLib2 的数据提供器。序列化返回的结果是一个 DataResult 对象，我们通过 .result().get() 来获取实际的输出。

反序列化过程类似，但我们需要先创建一个目标对象的实例（注意初始值是 0f），然后将序列化数据传入 deserialize 方法。deserialize 方法会将数据填充到新实例中，最后我们比较新旧实例是否相等。

控制台输出的结果应该是：

```
{
    "numberFloat": 100,
    "booleanValue": false,
    "stringValue": "default",
}
true
```

可以看到，序列化的输出是一个格式良好的 JSON 对象，只包含了被注解标记的字段。反序列化后的实例与原始实例相等，这证明了 PersistedParser 正确地处理了所有字段的序列化和反序列化。

值得注意的是 @Persisted 和 @Configurable 的区别。虽然在这个简单的例子中它们似乎做同样的事情，但在更复杂的场景中，它们可能用于不同的目的。@Persisted 通常用于需要持久化到存档的字段，而 @Configurable 可能用于配置文件中的可配置项。PersistedParser 会在处理时区分这两种注解，让你可以更精细地控制哪些字段需要序列化。

## 创建 Codec：注解的魔法

PersistedParser 另一个强大的功能是自动创建 Codec。Codec 是现代 Minecraft 中用于数据序列化的标准方式，它定义了如何将对象编码和解码为特定的数据格式。传统的 Codec 创建需要编写大量的样板代码——为每个字段定义编解码器、编写构造函数、提供 getter 方法。而 PersistedParser 允许你通过注解自动完成所有这些工作。

让我们看看如何为一个类创建基于注解的 Codec：

```java
public class MyObject implements IPersistedSerializable {
    public final static Codec<MyObject> CODEC = PersistedParser.createCodec(MyObject::new);

    @Persisted(key = "rl")
    private ResourceLocation resourceLocation = LDLib2.id("test");
    @Persisted(key = "enum")
    private Direction enumValue = Direction.NORTH;
    @Persisted(key = "item")
    private ItemStack itemstack = ItemStack.EMPTY;
}
```

仅仅一行代码：PersistedParser.createCodec(MyObject::new)，就创建了一个完整的 Codec。createCodec 方法接受一个Supplier，用来创建类的实例。框架会分析 MyObject 中所有被 @Persisted 注解的字段，自动生成对应的编解码逻辑。

这个自动生成的 Codec 等同于你手动编写以下代码：

```java
public class MyObject {
    public final static Codec<MyObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("rl").forGetter(MyObject::getResourceLocation),
            Direction.CODEC.fieldOf("enum").forGetter(MyObject::getEnumValue),
            ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(MyObject::getItemstack)
    ).apply(instance, MyObject::new));

    private ResourceLocation resourceLocation = LDLib2.id("test");
    private Direction enumValue = Direction.NORTH;
    private ItemStack itemstack = ItemStack.EMPTY;

    public MyObject(ResourceLocation resourceLocation, Direction enumValue, ItemStack itemstack) {
        this.resourceLocation = resourceLocation;
        this.enumValue = enumValue;
        this.itemstack = itemstack;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public Direction getEnumValue() {
        return enumValue;
    }

    public ItemStack getItemstack() {
        return itemstack;
    }
}
```

可以看到，手动编写的版本需要额外定义构造函数和 getter 方法，而且 Codec 定义部分需要为每个字段编写 fieldOf、forGetter 等调用。如果类中有十几个字段，这段代码会变得非常冗长。而使用 PersistedParser 的版本只需要一行代码，大大简化了代码结构。

这种自动生成 Codec 的能力在实际开发中非常有用。它不仅减少了样板代码，更重要的是让添加新字段变得简单——你只需要在字段上添加 @Persisted 注解，Codec 会自动更新，不需要手动修改任何编解码逻辑。

## 深入理解注解的作用

在使用 PersistedParser 时，理解各个注解的作用非常重要。@Persisted 注解标记的字段会被包含在序列化和 Codec 生成中，而 @Configurable 注解则通常用于配置相关的场景。

@Persisted 有一个可选的 key 参数，用于指定在序列化数据中使用的键名。如果不指定这个参数，框架会使用字段名作为键名。这在你想要使用更友好的名称来显示数据，或者需要与现有数据格式兼容时非常有用。

PersistedParser 能够处理多种类型的字段，包括基本类型（float、boolean、String 等）、Minecraft 游戏类型（ResourceLocation、Direction、ItemStack 等）、以及自定义类型（只要这些类型本身也被 LDLib2 支持）。框架会自动为每种类型选择合适的序列化方式，你不需要关心底层的实现细节。

## 实际应用场景

让我分享一些 PersistedParser 在实际开发中的应用场景。首先是方块实体的数据存储。每个方块实体都有自己的数据需要保存和加载，使用 PersistedParser 可以大大简化 saveAdditional 和 load 方法的实现。你只需要在字段上添加 @Persisted 注解，然后调用 PersistedParser 来处理序列化即可。

其次是配置文件的读写。很多模组需要提供配置文件让玩家自定义游戏体验，PersistedParser 可以帮助你轻松实现配置的序列化和反序列化。配合 @Configurable 注解，你可以清晰地标记哪些配置是玩家可以修改的。

第三是网络数据包的处理。在需要传输复杂数据结构时，Codec 是最佳选择。PersistedParser 自动生成的 Codec 可以直接用于网络包的编码和解码，确保服务器和客户端之间的数据正确传输。

第四是跨存档的数据迁移。当你的模组需要升级数据结构时，PersistedParser 可以帮助你读取旧格式的数据并转换到新格式，减少迁移过程中的数据丢失风险。

## 使用建议和最佳实践

在我自己的开发实践中，积累了一些使用 PersistedParser 的经验和建议。首先，始终让你的数据类实现 IPersistedSerializable 接口。这不仅让类能够使用 PersistedParser，还表明了这个类的设计意图，让其他开发者（包括未来的你）能够快速理解代码。

其次，合理使用 key 参数来命名序列化字段。当字段名不够直观或者需要与外部系统兼容时，指定一个更有意义的键名会让数据更易读。

第三，注意只读字段的处理。如果某些字段不应该被修改，确保它们不会被错误地包含在序列化中。LDLlib2 的类型系统会处理大多数情况，但在设计数据类时要有意识地考虑哪些字段应该是可变的。

第四，利用 @EqualsAndHashCode 注解（如 Lombok 的注解）来自动生成 equals 和 hashCode 方法。这在需要比较序列化数据或使用集合存储这些对象时非常重要。

最后，在测试中验证你的序列化逻辑。PersistedParser 虽然减少了出错的可能性，但错误的注解使用或类型不支持仍然可能导致问题。通过单元测试来验证序列化和反序列化的正确性是一个好习惯。

PersistedParser 是 LDLib2 提供的一个强大的工具，它将繁琐的序列化工作变得简单优雅。通过声明式的注解，你不再需要手动编写大量的编解码代码，而是可以专注于业务逻辑本身。希望这篇博客能帮助你更好地理解和使用 PersistedParser，让你的模组开发工作更加高效和愉快。