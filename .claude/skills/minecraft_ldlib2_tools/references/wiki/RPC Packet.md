RPC Packet
In vanilla or Forge-based mod development, maintaining custom network packets is often tedious. You typically need to maintain boilerplate networking code:

Define packet classes
Register them manually
Handle serialization and deserialization
To simplify this process, LDLib2 introduces an annotation-based RPC system using @RPCPacket.

With @RPCPacket, you can declare a static method ANYWHERE in your codebase and treat it as a network packet handler. The annotated method itself becomes the packet’s execution target, and its parameters represent the data transferred between client and server.

@RPCPacket("id"): Registers the method as an RPC handler with a unique identifier.
RPCSender (optional): If declared as the first parameter, LDLib2 injects sender-side information, allowing you to distinguish whether the
Method parameters: All parameters (except RPCSender) are automatically serialized and transferred. call is executed on the client or the server.
Note

The types of parameters should be supoorted in the Types Support.

RPCPacketDistributor Provides utility methods to send RPC calls to the server, all players, or specific targets.


// annotate your packet method anywhere you want
@RPCPacket("rpcPacketTest")
public static void rpcPacketTest(RPCSender sender, String message, boolean message2) {
    if (sender.isServer()) {
        LDLib2.LOGGER.info("Received RPC packet from server: {}, {}", message, message2);
    } else {
        LDLib2.LOGGER.info("Received RPC packet from client: {}, {}", message, message2);
    }
}

// send pacet to the remote/server 
RPCPacketDistributor.rpcToServer("rpcPacketTest", "Hello from client!", true)
RPCPacketDistributor.rpcToAllPlayers("rpcPacketTest", "Hello from server!", false)