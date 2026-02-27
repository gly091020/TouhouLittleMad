package com.gly091020.touhouLittleMad;

import com.gly091020.touhouLittleMad.util.GLYToolsRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DebugEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (event.player.getInventory().contains(new ItemStack(GLYToolsRegistry.items.get("player_not_die").get(), 1))) {
            var player = event.player;
            var h = player.getAttribute(Attributes.MAX_HEALTH);
            if (h != null) {
                h.setBaseValue(100);
            }
            player.setHealth(100);
        }
    }
}
