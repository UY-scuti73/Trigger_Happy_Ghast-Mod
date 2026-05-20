package xyz.quazaros.ghastfire73.Events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import xyz.quazaros.ghastfire73.Config.ConfigManager;

public class FireClass {
    public static boolean fire(Player player, ItemStack itemStack) {

        if (itemStack.getItem() != Items.WARPED_FUNGUS_ON_A_STICK) { return false; }

        Entity ridingMob = player.getVehicle();
        if (!(ridingMob instanceof HappyGhast)) { return false; }

        if (ridingMob.getPassengers().isEmpty()) { return false; }
        if (!ridingMob.getPassengers().get(0).equals(player)) { return false; }

        if (!(player instanceof ServerPlayer serverPlayer)) { return false; }
        if (player.level().isClientSide()) { return false; }

        int explosionValue = ConfigManager.get().explosionValue;
        int durCnt = ConfigManager.get().durabilityDamageValue;

        Vec3 direction = player.getLookAngle();
        Vec3 position = player.position();

        Fireball fireball = new LargeFireball(player.level(), player, direction, explosionValue);
        fireball.setPos(position.x, position.y - 3, position.z);

        player.level().addFreshEntity(fireball);

        itemStack.hurtAndBreak(durCnt, serverPlayer, EquipmentSlot.MAINHAND);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        return true;
    }
}
