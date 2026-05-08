package xyz.quazaros.ghastfire;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import xyz.quazaros.ghastfire.Events.FireClass;

public class MyModEvents {
    public static void register() {
        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            return FireClass.fire(player, itemStack) ? ActionResult.SUCCESS : ActionResult.PASS;
        });
    }
}