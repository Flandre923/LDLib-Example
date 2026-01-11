package com.example.examplemod.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MyFurnBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

    public MyFurnBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(2.0f)
                .requiresCorrectToolForDrops());
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if(level.isClientSide){
            return InteractionResult.SUCCESS;
        }else if(player instanceof ServerPlayer serverPlayer){
            BlockUIMenuType.openUI(serverPlayer,pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        if(holder.player.level().getBlockEntity(holder.pos) instanceof  MyFurnBlockEntity furnBlockEntity){
            return furnBlockEntity.createUI(holder);
        }
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MyFurnBlockEntity( pos, state);
    }
}
