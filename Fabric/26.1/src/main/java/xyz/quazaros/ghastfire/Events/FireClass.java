package xyz.quazaros.ghastfire.Events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import xyz.quazaros.ghastfire.Config.ConfigManager;

public class FireClass {

    public static boolean fire(Player player, ItemStack itemStack) {
        int durCnt = 5;

        if (itemStack.getItem() != Items.WARPED_FUNGUS_ON_A_STICK) return false;
        if (!(player instanceof ServerPlayer sp)) return false;
        if (sp.level().isClientSide()) return false;

        Entity riding = sp.getVehicle();
        if (riding == null) return false;

        // Keep your “must be the controlling passenger” intent
        if (riding.getPassengers().isEmpty()) return false;
        if (riding.getPassengers().getFirst() != sp) return false;

        // Direction/position in Mojang mappings
        Vec3 direction = sp.getLookAngle();
        Vec3 pos = sp.position();

        // ---- Projectile spawn (needs the *actual* class name available in your 26.1 sources) ----
        // Example possibilities (ONLY ONE will exist):
        // - net.minecraft.world.entity.projectile.LargeFireball
        // - net.minecraft.world.entity.projectile.Fireball
        // - another subclass used by ghasts
        //
        // Once you tell me which exists, we’ll instantiate it here and call:
        // sp.level().addFreshEntity(projectile);
        //
        // ---------------------------------------------------------------------------------------

        // Damage the item (26.1 Mojang method)
        itemStack.hurtAndBreak(durCnt, (LivingEntity) player, hand);

        // Play sound (Mojang names)
        sp.level().playSound(
                null,
                sp.getX(), sp.getY(), sp.getZ(),
                SoundEvents.GHAST_SHOOT,
                SoundSource.PLAYERS,
                1.0f, 1.0f
        );

        return true;
    }

    private static int getExplosionPower() {
        return ConfigManager.get().explosionValue;
    }
}