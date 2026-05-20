package xyz.quazaros.ghastfire73;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import xyz.quazaros.ghastfire73.Config.ConfigManager;

@Mod(main.MOD_ID)
public class main {

    public static final String MOD_ID = "ghastfire73";
    public static final Logger LOGGER = LogUtils.getLogger();

    public main(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigManager.load();
        System.out.println("GhastFire Initialized!");
    }
}