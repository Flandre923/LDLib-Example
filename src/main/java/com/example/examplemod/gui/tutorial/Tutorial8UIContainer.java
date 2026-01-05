package com.example.examplemod.gui.tutorial;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
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
        var xml = XmlUtils.loadXml(ResourceLocation.parse("examplemod:tuto.xml"));
        var ui = UI.of(xml);

        var buttons = ui.select(".panel_bg > button").toList();
//        var container = ui.selectRegex("container").findFirst().orElseThrow();
        return ModularUI.of(ui);
    }

}
