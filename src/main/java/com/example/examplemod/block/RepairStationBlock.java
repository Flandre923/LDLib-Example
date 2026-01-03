package com.example.examplemod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 修复台方块
 * 右键点击打开修复界面
 */
public class RepairStationBlock extends Block implements EntityBlock {

    public RepairStationBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(2.0f)
                .requiresCorrectToolForDrops());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RepairStationBlockEntity(pos, state);
    }

    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RepairStationBlockEntity) {
                ((RepairStationBlockEntity) blockEntity).clearContent();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    /**
     * Minecraft 1.21 新API：右键点击交互（无物品）
     */
    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                                      @NotNull BlockPos pos, @NotNull Player player,
                                                      @NotNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RepairStationBlockEntity repairStation) {
            // 在服务器端打开菜单
            player.openMenu(repairStation);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }

    public void appendHoverText(@NotNull net.minecraft.world.item.ItemStack stack, @Nullable BlockGetter level,
                                @NotNull List<Component> tooltip, @NotNull net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(Component.translatable("block.examplemod.repair_station_block.tooltip"));
    }
}
