package xyz.quazaros.ghastfire;

import net.fabricmc.api.ModInitializer;

public class GhastFire implements ModInitializer {
    @Override
    public void onInitialize() {
        MyModEvents.register();
        System.out.println("GhastFire Initialized!");
    }
}
