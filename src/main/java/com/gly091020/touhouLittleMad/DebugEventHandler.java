package com.gly091020.touhouLittleMad;

import com.gly091020.touhouLittleMad.util.GLYToolsRegistry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class DebugEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerTick(PlayerTickEvent.Pre event){
        if(event.getEntity().getInventory().contains(GLYToolsRegistry.items.get("player_not_die").toStack(1))){
            var player = event.getEntity();
            var h = player.getAttribute(Attributes.MAX_HEALTH);
            if(h != null){
                h.setBaseValue(100);
            }
            player.setHealth(100);
        }
    }
}
