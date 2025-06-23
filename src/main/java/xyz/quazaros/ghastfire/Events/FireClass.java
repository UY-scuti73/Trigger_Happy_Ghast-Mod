package xyz.quazaros.ghastfire.Events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class FireClass {
    public static boolean fire(PlayerEntity player, ItemStack itemStack) {
        int durCnt = 5;

        //Fireball Logic

        if (itemStack.getItem() != Items.WARPED_FUNGUS_ON_A_STICK) {return false;}

        Entity ridingMob = player.getVehicle();
        if (!(ridingMob instanceof HappyGhastEntity)) {return false;}

        if (ridingMob.getPassengerList().isEmpty()) {return false;}
        if (!ridingMob.getPassengerList().get(0).equals(player)) {return false;}

        if (!(player instanceof ServerPlayerEntity)) {return false;}
        if (player.getWorld().isClient()) {return false;}

        //Fireball Action

        Vec3d direction = player.getRotationVector();
        Vec3d position = player.getPos();

        FireballEntity fireball = new FireballEntity(player.getWorld(), player, direction, 1);
        fireball.setPosition(position.x, position.y - 3, position.z);

        player.getWorld().spawnEntity(fireball);

        itemStack.damage(durCnt, player, EquipmentSlot.MAINHAND);

        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return true;
    }
}
