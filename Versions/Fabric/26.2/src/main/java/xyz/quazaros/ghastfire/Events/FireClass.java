package xyz.quazaros.ghastfire.Events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;

import xyz.quazaros.ghastfire.Config.ConfigManager;

public class FireClass {

    public static boolean fire(Player player, ItemStack itemStack) {
        if (itemStack.getItem() != Items.WARPED_FUNGUS_ON_A_STICK) return false;
        if (!(player instanceof ServerPlayer sp)) return false;
        if (sp.level().isClientSide()) return false;

        Entity riding = sp.getVehicle();
        if (riding == null) return false;

        // Confirms the player is the driver
        if (riding.getPassengers().isEmpty()) return false;
        if (riding.getPassengers().getFirst() != sp) return false;

        int explosionValue = ConfigManager.get().explosionValue;
        int durCnt = ConfigManager.get().durabilityDamageValue;

        // Direction/position
        Vec3 direction = sp.getLookAngle();
        Vec3 position = sp.position();

        LargeFireball fireball = new LargeFireball(player.level(), player, direction, explosionValue);
        fireball.setPos(position.x, position.y - 3, position.z);
        sp.level().addFreshEntity(fireball);

        // Damage the item
        //InteractionHand hand = player.getUsedItemHand();
        EquipmentSlot hand = EquipmentSlot.MAINHAND;
        itemStack.hurtAndBreak(durCnt, player, hand);

        // Play sound
        sp.level().playSound(
                null,
                sp.getX(), sp.getY(), sp.getZ(),
                SoundEvents.GHAST_SHOOT,
                SoundSource.PLAYERS,
                1.0f, 1.0f
        );

        return true;
    }
}