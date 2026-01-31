package com.example.examplemod.gui.element;

import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;

import java.util.function.Supplier;

public class BigItemSlot extends ItemSlot {
    // 假设你的数据结构提供了获取真实数量的方法
    private final Supplier<Long> countSupplier;

    public BigItemSlot(Slot slot, Supplier<Long> countSupplier) {
        super(slot);
        this.countSupplier = countSupplier;
    }

    @Override
    public void drawBackgroundAdditional(GUIContext guiContext) {
        // 1. 调用父类绘制背景和物品模型（但父类会绘制原生的数量，我们需要覆盖它）
        super.drawBackgroundAdditional(guiContext);

        // 2. 获取真实的大额数量
        long totalCount = countSupplier.get();
        if (totalCount <= 0) return;

        // 3. 准备渲染自定义数字
        String text = formatBigCount(totalCount);

        float contentX = getContentX();
        float contentY = getContentY();
        float contentWidth = getContentWidth();
        float contentHeight = getContentHeight();

        // 4. 使用 DrawerHelper 绘制文本，覆盖原生的小数字
        // 技巧：原生数字在 z=200 左右，我们需要稍微偏移确保覆盖
        guiContext.pose.pushPose();
        // 移动到槽位的右下角附近
        guiContext.pose.translate(contentX, contentY, 300);
        float scale = 0.5f; // 这里的缩放取决于你文本的长度
        guiContext.pose.scale(scale, scale, 1);

        // 绘制文字 (白色带阴影)
        var font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        // 计算右下角位置
        float tx = (contentWidth / scale) - textWidth - 2;
        float ty = (contentHeight / scale) - font.lineHeight;

        guiContext.graphics.drawString(font, text, (int)tx, (int)ty, 0xFFFFFF, true);
        guiContext.pose.popPose();
    }

    /**
     * 将大额数字格式化为类似 AE2 的显示方式 (1000 -> 1K)
     */
    private String formatBigCount(long count) {
        if (count < 1000) return String.valueOf(count);
        if (count < 1000000) return String.format("%.1fk", count / 1000f);
        return String.format("%.1fM", count / 1000000f);
    }
}