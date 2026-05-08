package xyz.quazaros.ghastfire;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xyz.quazaros.ghastfire.Events.FireClass;

public class MyModEvents {
    public static void register() {
        UseItemCallback.EVENT.register((Player player, Level level, InteractionHand hand) -> {
            ItemStack itemStack = player.getItemInHand(hand);

            // InteractionResultHolder is gone! We just return InteractionResult directly now.
            return FireClass.fire(player, itemStack) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        });
    }
}