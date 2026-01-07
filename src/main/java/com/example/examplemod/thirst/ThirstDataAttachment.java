package com.example.examplemod.thirst;

import com.example.examplemod.ExampleMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ThirstDataAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ExampleMod.MODID);
    // 注册附件类型
    public static final Supplier<AttachmentType<ThirstData>> THIRST_DATA = ATTACHMENT_TYPES.register(
            "thirst_data",
            () -> AttachmentType.builder(ThirstData::new)
                    .serialize(ThirstData.CODEC)
                    .sync(new AttachmentSyncHandler<ThirstData>() {
                        @Override
                        public void write(RegistryFriendlyByteBuf buf, ThirstData attachment, boolean initialSync) {
                                buf.writeInt(attachment.getThirst());
                        }

                        @Override
                        public @Nullable ThirstData read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable ThirstData previousValue) {
                            var data = new ThirstData();
                            data.setThirst(buf.readInt());
                            return data;
                        }
                    })
                    .copyOnDeath()
                    .build()
    );

    // 辅助方法：方便获取玩家数据
    public static ThirstData getThirstData(Player player) {
        return player.getData(THIRST_DATA.get());
    }


}
