
# ISyncPersistRPCBlockEntity
这是一个组合接口，为 BlockEntity 提供同步 + 持久化 + RPC 功能。

```
  ISyncPersistRPCBlockEntity
      ├── ISyncBlockEntity      → 数据自动同步到客户端
      ├── IRPCBlockEntity       → 远程过程调用 (RPC)
      ├── IPersistManagedHolder → 数据持久化 (NBT保存)
      └── IBlockEntityManaged   → 方块实体管理
      
      
  | 接口                  | 功能                                     |
  |-----------------------|------------------------------------------|
  | ISyncBlockEntity      | 自动将数据同步到客户端（当数据变化时）   |
  | IRPCBlockEntity       | 支持远程过程调用（客户端调用服务端方法） |
  | IPersistManagedHolder | 数据持久化（保存/加载 NBT）              |
  | IBlockEntityManaged   | 管理 BlockEntity 生命周期                |
```

便捷方法
```java


// 客户端 → 服务端 RPC 调用
default void rpcToServer(String methodName, Object... args) { ... }

// 发送给指定玩家
default void rpcToPlayer(ServerPlayer player, String methodName, Object... args) { ... }

// 发送给追踪该方块的所有玩家
default void rpcToTracking(String methodName, Object... args) { ... }


```

使用示例

```java

  public class MyBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

      // 1. 数据会自动同步到客户端
      @Managed
      private int energy;

      // 2. 数据会自动保存/加载 NBT
      @Managed
      private String setting;

      // 3. 定义 RPC 方法 (客户端可调用)
      @RPCMethod
      public void doSomethingOnServer(String param) {
          // 在服务端执行
          this.energy = 100;
          markDirty();
      }
  }


  // 客户端调用
  blockEntity.rpcToServer("doSomethingOnServer", "hello");

```

# RPCMethod vs @RPCPacket 区别
```

  | 特性     | @RPCMethod                          | @RPCPacket      |
  |----------|-------------------------------------|-----------------|
  | 方法类型 | 实例方法                            | 静态方法        |
  | 位置限制 | 必须在实现 IRPCManagedHolder 的类中 | 可以在任何位置  |
  | 使用场景 | 与 BlockEntity/Item 绑定的 RPC      | 全局独立的 RPC  |
  | 注解参数 | 无                                  | 需要指定唯一 ID |

```
```java

  @RPCMethod

  // 必须在实现 IRPCManagedHolder 的类中 (如 ISyncPersistRPCBlockEntity)
  @RPCMethod
  public void rpcTest(RPCSender sender, String message) {
      // 处理逻辑
  }

  // 调用方式 - 通过 holder 调用
  blockEntity.rpcToServer("rpcTest", "hello");
  blockEntity.rpcToTracking("rpcTest", "world");

```

特点：
- 绑定到具体对象（BlockEntity 实例）
- 使用实例方法
- 可访问对象的字段和方法



```java


  @RPCPacket

  // 可以在任何位置 (不需要任何接口实现)
  @RPCPacket("myPacketId")
  public static void myPacketHandler(RPCSender sender, String msg) {
      // 处理逻辑
  }

  // 调用方式 - 使用 RPCPacketDistributor
  RPCPacketDistributor.rpcToServer("myPacketId", "hello");
  RPCPacketDistributor.rpcToAllPlayers("myPacketId", "world");
```

特点：
- 独立于任何类/对象
- 使用静态方法
- 全局注册，可从任何地方发送


