package xyz.quazaros.ghastfire.Config;

public class ModConfig {
    public int explosionValue = 1;

    public void validate() {
        if (explosionValue < 0) explosionValue = 0;
        if (explosionValue > 256) explosionValue = 256;
    }
}
