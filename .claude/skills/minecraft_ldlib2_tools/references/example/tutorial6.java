Tutorial 6: ModularUI for Menu
In the previous tutorials, we focused on rendering　ModularUI inside client-side screens. This works well for purely visual or client-only interfaces.

However, most real-world GUIs in Minecraft are server–client synchronized. When a GUI involves gameplay logic or persistent data, the server must stay authoritative. In vanilla Minecraft, this is handled through a Menu, which manages synchronization between the server and client.

Unlike UI libraries that only support client-side rendering, LDLib2 provides first-class support for server-backed Menus. You can use ModularUI directly with a Menu, and no additional networking or synchronization code is required.

Let's create a simple Menu-based UI that displays the player’s inventory.


private static ModularUI createModularUI(Player player) {
    var root = new UIElement();
    root.addChildren(
            new Label().setText("Menu UI"),
            // add player invotry 
            new InventorySlots()
    ).addClass("panel_bg");

    var ui = UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP));
    // pass the player to the Modular UI
    return ModularUI.of(ui, player);
}
You have to create a ModularUI with a Player, which is necessary for the menu-based UI. Besides, not only the screen, you should also init the ModularUI for your Menu as well:

The initialization should be done after creation and before writing the extra data buffer.
Don't forget to set a correct image size of your screen if necessary.

public class MyContainerMenu extends AbstractContainerMenu {
    // you can do initialization in the constructor
    public MyContainerMenu(...) {
        super(...)

        var modularUI = createModularUI(player)
        // we have added mixin to make the AbstractContainerMenu implementing the interface
        if (this instanceof IModularUIHolderMenu holder) {
            holder.setModularUI(modularUI);
        }
    }

    // .....
}

public class MyContainerScreen extends AbstractContainerScreen<MyContainerMenu> {
    @Override
    public void init() {
        // the modular widget has already added + init by events
        this.imageWidth = (int) getMenu().getModularUI().getWidth();
        this.imageHeight = (int) getMenu().getModularUI().getHeight();
        super.init();
    }

    // .....
}
Quick Test

To use and open a menu-based UI, you need to register your own MenuType, LDLib2 also provide the ModularUIContainerScreen and ModularUIContainerMenu to help you set this up quickly. Check screen and menu page for more details.

Alternatively, you can get started even faster by using the provided factories. They allow you to create menu-based UIs for blocks, items, or players with minimal setup—without dealing with manual registration or boilerplate code. In this case, we use the PlayerUIMenuType for quick demo.


public static final ResourceLocation UI_ID = LDLib2.id("unique_id");

// register your ui somewhere, e.g. during your mod initialization.
public static void registerPlayerUI() {
    PlayerUIMenuType.register(UI_ID, ignored -> player -> createModularUI(player));
}

public static void openMenuUI(Player player) {
    PlayerUIMenuType.openUI(player, UI_ID);
}
Tutorial 6 Result