Types Support
Since 2.0.0

LDLib2 has provided massive types support for synchronization and persistence already.

Builtin Supports
Note

Check github to get the latest support list.

native types in java (number, boolean, string, enum, etc)
Type(s)	Priority	Read-only
int / Integer	-1	-
long / Long	-1	-
float / Float	-1	-
double / Double	-1	-
boolean / Boolean	-1	-
byte / Byte	-1	-
short / Short	-1	-
char / Character	-1	-
String	-1	-
Enum<?>	-1	-
Number	1000	-
UUID	100	-
T[]	-1	depeends on T
Collection<?>	-1	✅
types in minecraft (Block, Item, Fluid, etc)
Type(s)	Priority	Read-only
Block	100	-
Item	100	-
Fluid	100	-
EntityType<?>	100	-
BlockEntityType<?>	100	-
BlockState	100	-
ResourceLocation	100	-
AABB	1000	-
BlockPos	1000	-
FluidStack	1000	-
ItemStack	1000	-
RecipeHolder<?>	1000	-
Tag	2000	-
Component	2000	-
INBTSerializable<?>	2000	✅
types in LDLib2 or others.
Type(s)	Priority	Read-only
UIEvent	100	-
Position	100	-
Size	100	-
Pivot	100	-
Range	100	-
Vector3f	1000	-
Vector4f	1000	-
Vector2f	1000	-
Vector2i	1000	-
Quaternionf	1000	-
IGuiTexture	1000	-
IRenderer	1000	-
IResourcePath	1000	-
IManaged	1500	✅
Add Custom Type Support
To add a support of a new type, you need to register an IAccessor<TYPE> of this type. All types can be divided into two group, direct and read-only.

Important

direct refer to the type that can be null, and there is known method to create a new instance of this type during management life cycle.
read-only refer to the type that can not be null and immutable during management life cycle. (e.g. INBTSerializable<?> amd Collection<?>). All modification should be done by its APIs.
You can register accessor by using AccessorRegistries.registerAccessor. In general, you can register your accessors anywhere you want, while we'd recommend to do it in the LDLibPlugin#onLoad.

Register for a direct type
You can use CustomDirectAccessor to register new type easily.

What is Mark?

Mark is a snapshot during management life cycle. LDLib2 will generate mark for current value and caompare it later to determine whether it has changes. If Mark is not defined. It will store current value as mark. It works if the type's internal values are immutable. (e.g. UUID, ResoruceLocation). Otherwise, you'd better set a way to obtain mark.

Method	Optional	Note
codec	Required	Provide a codec for persistence
streamCodec	Required	Provide a StreamCodec for synchronization
customMark	Optional	Provide functions to get and compare marks
copyMark	Optional	Copy the mark from the value. This will use the Objects#equals(Object, Object) to compare the mark. Make sure the object supports Object#equals(Object).
codecMark	Optional	This will use the JavaOps to generate the mark based on current value.

AccessorRegistries.registerAccessor(CustomDirectAccessor.builder(Quaternionf.class)
    .codec(ExtraCodecs.QUATERNIONF)
    .streamCodec(ByteBufCodecs.QUATERNIONF)
    .copyMark(Quaternionf::new)
    .build());

AccessorRegistries.registerAccessor(CustomDirectAccessor.builder(ItemStack.class)
    .codec(ItemStack.OPTIONAL_CODEC)
    .streamCodec(ItemStack.OPTIONAL_STREAM_CODEC)
    .customMark(ItemStack::copy, ItemStack::matches)
    .build());
Register for a read-only type
In general, you don't really need it. Cuz, you can make your own class to inherite from INBTSerializable. If you do need it, please implement IReadOnlyAccessor<TYPE> and register it, check code comments for more usage details .