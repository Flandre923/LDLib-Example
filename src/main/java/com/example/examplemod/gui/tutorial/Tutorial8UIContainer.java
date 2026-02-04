package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.*;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lowdragmc.lowdraglib2.utils.XmlUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.appliedenergistics.yoga.YogaFlexDirection;
import org.codehaus.plexus.util.xml.XmlUtil;

import java.util.concurrent.atomic.AtomicInteger;

//1. 如何使用xml
//2. 如何加载xml并打开
//3. 如何获得xml中的元素

public class Tutorial8UIContainer {
    public static ModularUI createModularUI(Player player) {
        var xml = XmlUtils.loadXml(ResourceLocation.parse("examplemod:resouces/tuto.xml"));
        var ui = UI.of(xml);

        var buttons = ui.select(".panel_bg > button").toList();
        var source_slot = ui.select("#source");
        if(source_slot instanceof ItemSlot itemSlot){
        }
        if(buttons.size() > 0 && buttons.get(0) instanceof Button button){
            button.setOnClick(e->{});
        }
//        var container = ui.selectRegex("container").findFirst().orElseThrow();
        return ModularUI.of(ui);
    }

}
