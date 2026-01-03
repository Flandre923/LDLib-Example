package com.example.examplemod.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.block.RepairStationMenu;
import com.example.examplemod.gui.RepairStationScreen;
import com.example.examplemod.gui.tutorial.Tutorial6Menu;
import com.example.examplemod.gui.tutorial.Tutorial6Screen;
import com.example.examplemod.gui.tutorial.Tutorial7Menu;
import com.example.examplemod.gui.tutorial.Tutorial7Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 客户端事件处理
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = ExampleMod.MODID)
public class ClientSetup {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(RepairStationMenu.TYPE.get(), RepairStationScreen::new);
        event.register(Tutorial6Menu.TYPE.get(), Tutorial6Screen::new);
        event.register(Tutorial7Menu.TYPE.get(), Tutorial7Screen::new);
    }
}
