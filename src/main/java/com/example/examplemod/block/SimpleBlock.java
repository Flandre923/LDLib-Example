package com.example.examplemod.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.appliedenergistics.yoga.YogaEdge;
import org.appliedenergistics.yoga.YogaGutter;
import org.appliedenergistics.yoga.YogaJustify;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 简单方块示例
 * 右键点击打开简单UI
 */
public class SimpleBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

    public SimpleBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(1.0f));
    }

    @Override
    public InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                            @NotNull BlockPos pos, @NotNull Player player,
                                            @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player instanceof ServerPlayer serverPlayer) {
            BlockUIMenuType.openUI(serverPlayer, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SimpleBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                             @NotNull BlockState state,
                                                                             @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> ((SimpleBlockEntity) be).tick();
    }

    @Override
    public ModularUI createUI(@NotNull BlockUIMenuType.BlockUIHolder holder) {
        if (holder.player.level().getBlockEntity(holder.pos) instanceof SimpleBlockEntity blockEntity) {
            return blockEntity.createUI(holder);
        }

        // 降级UI：如果方块实体不存在
        var root = new UIElement()
                .layout(layout -> layout
                        .setPadding(YogaEdge.ALL, 10)
                        .setGap(YogaGutter.ALL, 5)
                        .setJustifyContent(YogaJustify.CENTER)
                );

        root.addChild(new Label().setText("Simple Block"));

        return new ModularUI(UI.of(root), holder.player);
    }
}
