package xyz.quazaros.ghastfire73.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import xyz.quazaros.ghastfire73.Config.ModConfigScreen;

public class clientInitialization {
    public static void init_client(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (minecraft, parent) -> new ModConfigScreen(parent));
    }
}
