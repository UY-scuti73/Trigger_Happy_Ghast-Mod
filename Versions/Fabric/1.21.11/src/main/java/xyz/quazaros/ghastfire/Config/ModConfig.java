package xyz.quazaros.ghastfire.Config;

public class ModConfig {
    public int explosion_damage = 1;
    public int durability_damage = 5;

    public void validate() {
        if (explosion_damage < 0) explosion_damage = 0;
        if (explosion_damage > 256) explosion_damage = 256;
    }
}
