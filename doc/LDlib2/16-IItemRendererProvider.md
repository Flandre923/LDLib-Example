
# IItemRendererProvider

作用：为物品提供自定义渲染器。

```java

/**
 * Author: KilaBash
 * Date: 2022/04/21
 * Description: 
 */
public interface IItemRendererProvider {
    
    /**
     * A switch to disable the deep rendering of the item stack. {@link  com.lowdragmc.lowdraglib2.core.mixins.ItemRendererMixin#injectRenderItem(ItemStack, ItemDisplayContext, boolean, PoseStack, MultiBufferSource, int, int, BakedModel, CallbackInfo)}
     */
    ThreadLocal<Boolean> disabled = ThreadLocal.withInitial(()->false);

    /**
     * Get the renderer for the item stack.
     * @return return null if the item stack does not have a renderer.
     */
    @Nullable
    IRenderer getRenderer(ItemStack stack);
}


```

这让物品使用方块的渲染器（ TestBlock 的渲染），而不是默认的物品模型渲染。

```java

  @Override
  public IRenderer getRenderer(ItemStack stack) {
      return TestBlock.BLOCK.getRenderer(TestBlock.BLOCK.defaultBlockState());
  }
```


# HeldItemUIMenuType.HeldItemUI
HeldItemUIMenuType.HeldItemUI 这是一个功能接口，用于在手持物品时打开自定义 GUI。

核心方法：
```
| 方法                        | 说明                                                    |
|----------------------------|---------------------------------------------------------|
| createUI(HeldItemUIHolder) | 必须实现 - 创建自定义 UI 界面                                 |
| createUIHolder(...)        | 创建一个 UI 持有者，保存玩家、手部、物品信息                           |
| stillValid(...)            |检查物品是否仍然有效（防止物品被替换/丢弃后 UI 仍存活） |
| getUIDisplayName(...)      | 获取 UI 的标题名称              |
```

```java
    //  表示这是一个函数式接口，只能有一个抽象方法。可以用 lambda 表达式简写。
    @FunctionalInterface
    public interface HeldItemUI {
        /**
         * Creates a {@code ModularUI} instance based on the provided {@link HeldItemUIHolder}.
         *
         * @param holder the {@link HeldItemUIHolder} containing contextual data
         *               required for generating the {@code ModularUI}
         * @return a {@code ModularUI} instance constructed using the*/
        ModularUI createUI(HeldItemUIHolder holder);

        /**
         * Creates a new instance of {@link HeldItemUIHolder} with the provided player, hand, and item stack.
         *
         * @param player The {@link Player} interacting with the UI.
         * @param hand The {@link InteractionHand} used by the player, specifying which hand is holding the item.
         * @param itemStack The {@link ItemStack} representing the item the player is interacting with.
         * @return A new {@link HeldItemUIHolder} instance initialized with the given context.
         */
        default HeldItemUIHolder createUIHolder(Player player, InteractionHand hand, ItemStack itemStack) {
            return new HeldItemUIHolder(this, player, hand, itemStack);
        }

        /**
         * Checks whether the given {@link HeldItemUIHolder} is still valid.
         * Validity is determined by comparing the {@link ItemStack} currently held in the specified player's hand
         * with the {@link ItemStack} stored in the {@link HeldItemUIHolder}.
         *
         * @param holder the {@link HeldItemUIHolder} which contains contextual information including the player,
         *               the hand being used, and the original {@link ItemStack}.
         * @return {@code true} if the current item held in the player's hand matches the stored {@link ItemStack},
         *         {@code false} otherwise.
         */
        default boolean stillValid(HeldItemUIHolder holder) {
            var current = holder.player.getItemInHand(holder.hand);
            return ItemStack.matches(current, holder.itemStack);
        }

        default Component getUIDisplayName(HeldItemUIHolder holder) {
            return Component.translatable(holder.itemStack.getDescriptionId());
        }
    }

```

# IRenderer

IRenderer 是 LDLib2 的自定义渲染接口，用于控制方块、物品的渲染方式。

```
  | 方法                    | 作用                    |
  |-------------------------|-------------------------|
  | renderItem(...)         | 渲染物品（手持/背包中） |
  | renderModel(...)        | 渲染方块模型            |
  | render(...)             | 渲染方块实体 (TESR)     |
  | getRenderTypes(...)     | 获取渲染层类型          |
  | getParticleTexture(...) | 获取粒子纹理            |
```
在 TestItem 中的使用

```java
@Override
public IRenderer getRenderer(ItemStack stack) {
    return TestBlock.BLOCK.getRenderer(TestBlock.BLOCK.defaultBlockState());
}
```

这表示 TestItem 使用 TestBlock 的渲染器 来渲染自己，而不是默认的物品模型。

简单理解：IRenderer 让你可以用代码控制"方块/物品长什么样"，而不是只靠 JSON 模型文件。

# IBlockRendererProvider

IBlockRendererProvider 是方块渲染器提供者接口，与 IItemRendererProvider 类似，但是用于方块。

```java


  public interface IBlockRendererProvider {
      @Nullable
      IRenderer getRenderer(BlockState state);

      default int getLightMap(...) { ... }

      default ModelState getModelState(...) { ... }
  }

```

```
  | 方法                    | 作用                                    |
  |-------------------------|-----------------------------------------|
  | getRenderer(BlockState) | 必须实现 - 根据方块状态返回对应的渲染器 |
  | getLightMap(...)        | 自定义光照（默认实现）                  |
  | getModelState(...)      | 自定义模型旋转/状态（默认实现）         |
```





然后 TestItem 可以复用这个渲染器：
```java
  public class TestItem extends BlockItem implements IItemRendererProvider {
      @Override
      public IRenderer getRenderer(ItemStack stack) {
          // 复用方块的渲染器
          return TestBlock.BLOCK.getRenderer(TestBlock.BLOCK.defaultBlockState());
      }
  }
```

