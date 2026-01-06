package com.example.examplemod.block;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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

import java.util.List;

/**
 * 修复台方块
 * 右键点击打开修复界面
 */
public class RepairStationBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

    public RepairStationBlock() {
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
        if(holder.player.level().getBlockEntity(holder.pos) instanceof  RepairStationBlockEntity repairStationBlockEntity){
            return repairStationBlockEntity.createUI(holder);
        }
        var root = new UIElement().layout(layout -> layout
                .setWidth(100)                         // 设置宽度100像素
                .setHeight(100)                        // 设置高度100像素
                .setPadding(YogaEdge.ALL, 4)          // 内边距:所有方向4像素
                .setGap(YogaGutter.ALL, 2)            // 间距:所有方向2像素
                .setJustifyContent(YogaJustify.CENTER) // 内容居中对齐
        ).style(style -> style.backgroundTexture(Sprites.BORDER)); // 背景纹理:边框样式


        // 添加标题标签
        root.addChild(new Label().setText("Test Block UI"));

        // 添加文本输入框(演示用)
        root.addChild(new TextField());


        return new ModularUI(UI.of(root), holder.player);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RepairStationBlockEntity(pos,state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> ((RepairStationBlockEntity) be).tick();
    }
}
