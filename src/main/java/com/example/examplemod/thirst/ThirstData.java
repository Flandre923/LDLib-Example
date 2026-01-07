package com.example.examplemod.thirst;

import com.example.examplemod.ExampleMod;
import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacket;
import com.lowdragmc.lowdraglib2.syncdata.IManaged;
import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.rpc.RPCSender;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import com.lowdragmc.lowdraglib2.utils.PersistedParser;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ThirstData   {

    @DescSynced
    private int thirst = ThirstConstants.MAX_THIRST;
    public final static Codec<ThirstData> CODEC = PersistedParser.createCodec(ThirstData::new);


    @DescSynced
    private float exhaustion  = 0.0f;
    @DescSynced
    private float lastThirstForRender = 20.0f;        // 用于UI平滑渲染的插值值



    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        this.thirst = Math.min(ThirstConstants.MAX_THIRST, Math.max(0, thirst));
    }

    public void addThirst(int amount) {
        this.setThirst(this.thirst + amount);
    }

    @RPCPacket("addThirst")
    public static void onAddThirst(RPCSender sender, int amount){
        if(sender.isServer()){
//            ThirstDataAttachment.getThirstData(player).addThirst(amount);
            ExampleMod.LOGGER.info("ExampleMod::addThirst Server " + amount);
        }else{
            ExampleMod.LOGGER.info("ExampleMod::addThirst Client " + amount);
            ServerPlayer player = sender.asPlayer();
            var data = ThirstDataAttachment.getThirstData(player);
            data.addThirst(amount);
            player.setData(ThirstDataAttachment.THIRST_DATA,data);
        }
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public void addExhaustion(float amount) {
        this.exhaustion += amount;
    }

    public void tick() {
        if (this.exhaustion >= ThirstConstants.EXHAUSTION_THRESHOLD) {
            this.exhaustion -= ThirstConstants.EXHAUSTION_THRESHOLD;
            this.addThirst(-1);
        }
    }

    // 用于 UI 渲染的插值数据更新
    public float getRenderThirst(float partialTick) {
        float target = getThirst();
        // 简单的线性插值，使进度条变化平滑
        this.lastThirstForRender += (target - this.lastThirstForRender) * 0.1f;
        return this.lastThirstForRender;
    }


//    public CompoundTag save() {
//        CompoundTag tag = new CompoundTag();
//        tag.putInt("thirst", this.thirst);
//        tag.putFloat("exhaustion", this.exhaustion);
//        return tag;
//    }
//
//    public static ThirstData load(CompoundTag tag) {
//        ThirstData data = new ThirstData();
//        data.thirst = tag.getInt("thirst");
//        data.exhaustion = tag.getFloat("exhaustion");
//        return data;
//    }
}
