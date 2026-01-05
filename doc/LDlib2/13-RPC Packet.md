# 告别繁琐网络编程！LDLib2 RPC Packet 让MC模组跨端通信变简单
在 Minecraft 模组开发中，跨端通信（客户端与服务端数据交互）是绕不开的核心需求——比如玩家点击UI按钮后同步服务端数据、服务端触发事件后通知所有客户端刷新界面。但在原版或 Forge 模组开发中，自定义网络数据包的流程堪称“劝退级”：要手动定义数据包类、注册数据包、处理序列化与反序列化，一堆样板代码写下来，不仅耗时还容易出错。

而 LDLib2 推出的 **@RPCPacket 注解式RPC系统**，直接把这堆复杂操作“砍”到最简。今天就从基础概念到实战代码，带大家吃透这个高效工具，让跨端通信像调用普通方法一样简单！

## 一、先搞懂：什么是RPC？为什么需要它？
### 1. 基础概念科普
- **RPC**：全称 Remote Procedure Call（远程过程调用），简单说就是“调用另一个端（客户端/服务端）的方法”——比如客户端调用服务端的方法修改玩家数据，服务端调用所有客户端的方法显示提示信息。
- **跨端通信的核心痛点**：Minecraft 是客户端-服务端（C/S）架构，两端数据不互通，必须通过“数据包”传递信息。传统方式需要手动处理数据包的创建、发送、解析，步骤繁琐且易出错。
- **LDLib2 RPC的优势**：用注解替代手动编写数据包代码，自动完成序列化、注册、分发，开发者只需关注“要传递什么数据”和“收到数据后做什么”。

### 2. 适用场景
- 客户端点击UI按钮，通知服务端执行逻辑（如扣除物品、发放奖励）；
- 服务端触发事件后，同步消息给所有玩家客户端（如广播公告、刷新UI）；
- 跨端传递简单数据（字符串、布尔值、数字、物品栈等）。

## 二、LDLib2 RPC Packet 核心用法
### 1. 前置准备
- 确保模组已集成 LDLib2（2.1.0+版本，适配1.19.x+ Minecraft）；
- 无需额外配置网络通道，LDLib2 已内置统一的RPC分发器。

### 2. 三步实现跨端通信
#### 第一步：用@RPCPacket注解声明处理方法
在代码任意位置定义静态方法，添加 `@RPCPacket("唯一ID")` 注解，该方法就是跨端调用的“处理逻辑”。

```java
import lowdrag.lib.sync.rpc.RPCPacket;
import lowdrag.lib.sync.rpc.RPCSender;
import lowdrag.lib.LDLib2;

// 注解参数是唯一标识符，必须全局唯一（建议用“模组ID:功能名”格式避免冲突）
@RPCPacket("my_mod:rpc_test")
public static void handleRpcCall(RPCSender sender, String message, boolean isSuccess) {
    // RPCSender 是可选参数，用于判断调用方是客户端还是服务端
    if (sender.isServer()) {
        // 若为服务端收到客户端的调用
        LDLib2.LOGGER.info("服务端收到客户端消息：{}，状态：{}", message, isSuccess);
        // 这里可添加服务端逻辑：如修改玩家数据、触发事件等
    } else {
        // 若为客户端收到服务端的调用
        LDLib2.LOGGER.info("客户端收到服务端消息：{}，状态：{}", message, isSuccess);
        // 这里可添加客户端逻辑：如显示UI提示、播放音效等
    }
}
```

**关键说明**：
- 注解ID必须唯一：建议格式为“模组ID:功能名”（如“my_mod:give_reward”），避免与其他模组冲突；
- 参数规则：
    - 第一个参数可选为 `RPCSender`，用于获取调用方信息（是否为服务端、调用玩家等）；
    - 其他参数为要传递的数据，支持的类型包括：基本类型（int、boolean、String等）、ItemStack、FluidStack、ResourceLocation 等（完整支持类型见 LDLib2 官方“Types Support”文档）；
    - 无需手动处理序列化：LDLib2 自动将参数序列化后传递，接收端自动反序列化。

#### 第二步：发送RPC调用
使用 `RPCPacketDistributor` 工具类，调用对应方法发送RPC请求，支持“客户端→服务端”“服务端→所有客户端”“服务端→指定客户端”等场景。

##### 场景1：客户端向服务端发送调用
```java
// 在客户端代码中（如UI按钮点击事件里）
// 格式：RPCPacketDistributor.rpcToServer("注解ID", 参数1, 参数2, ...)
RPCPacketDistributor.rpcToServer("my_mod:rpc_test", "请求发放奖励", true);
```

##### 场景2：服务端向所有客户端发送调用
```java
// 在服务端代码中（如事件触发后）
// 格式：RPCPacketDistributor.rpcToAllPlayers("注解ID", 参数1, 参数2, ...)
RPCPacketDistributor.rpcToAllPlayers("my_mod:rpc_test", "服务器维护通知", false);
```

##### 场景3：服务端向指定客户端发送调用
```java
// 需传入目标玩家的ServerPlayer对象
ServerPlayer targetPlayer = ...; // 获取目标玩家（如触发事件的玩家）
RPCPacketDistributor.rpcToPlayer(targetPlayer, "my_mod:rpc_test", "专属奖励已发放", true);
```

#### 第三步：测试跨端通信
- 启动游戏，触发发送逻辑（如点击UI按钮）；
- 查看日志：服务端/客户端会打印对应的日志信息，说明跨端通信成功。

### 3. 完整示例：UI按钮点击同步服务端
结合之前讲的 LDLib2 UI事件，实现“客户端点击按钮→服务端执行奖励发放”的完整流程：

```java
// 1. 定义RPC处理方法（服务端接收）
@RPCPacket("my_mod:give_reward")
public static void givePlayerReward(RPCSender sender, ServerPlayer player) {
    if (sender.isServer()) {
        // 服务端逻辑：给玩家发放1个钻石
        player.getInventory().add(ItemStack.EMPTY.copyWithCount(1));
        LDLib2.LOGGER.info("给玩家 {} 发放钻石奖励", player.getName().getString());
        // 向该玩家客户端发送通知
        RPCPacketDistributor.rpcToPlayer(player, "my_mod:reward_notify", "奖励已发放！", true);
    }
}

// 2. 客户端UI按钮点击事件（发送RPC）
Button button = new Button().setText("领取奖励");
button.addEventListener(UIEvents.CLICK, e -> {
    // 客户端向服务端发送RPC请求
    RPCPacketDistributor.rpcToServer("my_mod:give_reward", Minecraft.getInstance().player);
});

// 3. 客户端接收服务端通知（显示提示）
@RPCPacket("my_mod:reward_notify")
public static void showRewardNotify(RPCSender sender, String message, boolean isSuccess) {
    if (!sender.isServer()) {
        // 客户端显示提示信息
        Minecraft.getInstance().player.displayClientMessage(Component.literal(message), true);
    }
}
```

## 三、核心特性与注意事项
### 1. 核心优势
- 零样板代码：无需定义数据包类、注册网络通道，注解+方法即可实现跨端通信；
- 自动序列化：支持大多数MC常用数据类型，无需手动处理序列化逻辑；
- 灵活分发：支持向服务端、所有客户端、指定客户端发送，满足多种场景需求；
- 与LDLib2生态无缝集成：可直接结合UI事件、数据绑定使用，简化模组开发流程。

### 2. 注意事项
- 避免传递大量数据：RPC适用于“小数据+即时响应”场景，如传递指令、状态通知等，不建议传递超大文件或复杂数据集；
- 唯一ID不可重复：注解的ID必须全局唯一，否则会导致调用冲突；
- 支持类型限制：仅支持 LDLib2 官方列出的“Types Support”类型，自定义类需实现序列化接口（或使用LDLib2的`@SyncData`注解）；
- 服务端权限校验：客户端可能发送恶意调用，服务端处理时需添加权限校验（如判断玩家是否有权限执行操作）。

## 四、传统数据包与LDLib2 RPC对比
| 对比维度 | 传统数据包开发 | LDLib2 RPC Packet |
|----------|----------------|-------------------|
| 代码量   | 需编写数据包类、注册、序列化/反序列化 | 仅需注解+处理方法 |
| 开发效率 | 低（步骤繁琐，易出错） | 高（三步完成，自动处理底层逻辑） |
| 维护成本 | 高（修改参数需同步修改序列化代码） | 低（修改参数无需额外操作） |
| 兼容性   | 需适配不同加载器（Forge/Fabric） | LDLib2 统一适配，无需关注加载器差异 |

## 总结
LDLib2 的 RPC Packet 系统，把 Minecraft 模组开发中繁琐的跨端通信，简化成了“注解声明方法+调用分发器”的简单流程。无论是新手还是老手，都能快速实现客户端与服务端的 data 交互，大幅节省开发时间。

如果你正在开发需要跨端通信的模组（如UI交互、玩家数据同步、事件广播等），强烈建议试试这个工具——告别重复的样板代码，把精力集中在核心玩法开发上！

