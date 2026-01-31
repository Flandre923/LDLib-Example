PersistedParser
PersistedParser is a tool class to serialize and deserialize the object fields with @Persisted or @Configurable annotation.

Serialization

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

var instance = new TestData(100f);
var output = PersistedParser.serialize(JsonOps.INSTANCE, instance, provider).result().get();
System.out.println(output);
var newInstance = new TestData(0f);
var data = PersistedParser.deserialize(JsonOps.INSTANCE, output, newInstance, provider);
System.out.println(newInstance.equals(instance));
Console log should be


{
    "numberFloat": 100,
    "booleanValue": false,
    "stringValue": "default",
}
true
Create Codec
Another useful tool provided by the PersistedParser is to create a Codec based on annotations.


 public class MyObject implements IPersistedSerializable {
    public final static Codec<MyObject> CODEC = PersistedParser.createCodec(MyObject::new);

    @Persisted(key = "rl")
    private ResourceLocation resourceLocation = LDLib2.id("test");
    @Persisted(key = "enum")
    private Direction enumValue = Direction.NORTH;
    @Persisted(key = "item")
    private ItemStack itemstack = ItemStack.EMPTY;
}
equals to


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