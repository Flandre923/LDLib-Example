package com.example.examplemod.gui;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.*;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacketDistributor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 药水注射器UI容器
 * 包含选择框选择药水效果，以及注射按钮
 */
public class PotionInjectorUIContainer {

    // 可用的药水效果列表
    private static final List<PotionEffectOption> POTION_OPTIONS = new ArrayList<>();

    static {
        // 使用注册表ID而不是Holder对象
        POTION_OPTIONS.add(new PotionEffectOption("speed", "速度", "minecraft:speed"));
        POTION_OPTIONS.add(new PotionEffectOption("strength", "力量", "minecraft:strength"));
        POTION_OPTIONS.add(new PotionEffectOption("regeneration", "再生", "minecraft:regeneration"));
        POTION_OPTIONS.add(new PotionEffectOption("resistance", "抗性", "minecraft:resistance"));
        POTION_OPTIONS.add(new PotionEffectOption("fire_resistance", "抗火", "minecraft:fire_resistance"));
        POTION_OPTIONS.add(new PotionEffectOption("night_vision", "夜视", "minecraft:night_vision"));
        POTION_OPTIONS.add(new PotionEffectOption("water_breathing", "水下呼吸", "minecraft:water_breathing"));
        POTION_OPTIONS.add(new PotionEffectOption("invisibility", "隐身", "minecraft:invisibility"));
        POTION_OPTIONS.add(new PotionEffectOption("jump_boost", "跳跃提升", "minecraft:jump_boost"));
        POTION_OPTIONS.add(new PotionEffectOption("haste", "急迫", "minecraft:haste"));
    }

    /**
     * 创建药水注射器UI
     */
    public static ModularUI createModularUI(Player player) {
        var root = new UIElement();

        // 存储当前选中的药水效果索引
        AtomicInteger selectedIndex = new AtomicInteger(0);

        root.layout(layout -> layout
                .width(200)
                .paddingAll(10)
                .gapAll(8)
        );

        root.addChildren(
                // 标题
                new Label()
                        .setText("药水注射器")
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER))
                        .layout(layout -> layout.marginBottom(5)),

                // 药水选择器 - 使用下拉选择框
                new Selector<String>()
                        .setCandidates(POTION_OPTIONS.stream().map(opt -> opt.name).toList())
                        .setSelected(POTION_OPTIONS.get(0).name, false)
                        .setOnValueChanged(selectedName -> {
                            // 根据名称找到对应的索引
                            for (int i = 0; i < POTION_OPTIONS.size(); i++) {
                                if (POTION_OPTIONS.get(i).name.equals(selectedName)) {
                                    selectedIndex.set(i);
                                    break;
                                }
                            }
                        })
                        .layout(layout -> layout.width(180)),

                // 说明标签
                new Label()
                        .setText("效果持续时间: 10秒")
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER))
                        .layout(layout -> layout.marginTop(5)),

                // 注射按钮
                new Button()
                        .setText("注射")
                        .setOnClick(e -> {
                            // 通过RPC发送选择的药水效果索引到服务器
                            int index = selectedIndex.get();
                            String potionId = POTION_OPTIONS.get(index).effectId;
                            RPCPacketDistributor.rpcToServer("potion_injector:apply", potionId);
                        })
                        .layout(layout -> layout
                                .width(180)
                                .height(25)
                        )
        );

        root.style(style -> style.background(Sprites.BORDER));

        var ui = UI.of(root);
        return ModularUI.of(ui);
    }

    /**
     * 药水效果选项
     */
    private record PotionEffectOption(String id, String name, String effectId) {
    }
}
