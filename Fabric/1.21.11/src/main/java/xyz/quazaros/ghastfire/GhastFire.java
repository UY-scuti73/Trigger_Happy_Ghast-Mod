package xyz.quazaros.ghastfire;

import net.fabricmc.api.ModInitializer;
import xyz.quazaros.ghastfire.Config.ConfigManager;

public class GhastFire implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigManager.load();
        MyModEvents.register();
        System.out.println("GhastFire Initialized!");
    }
}
