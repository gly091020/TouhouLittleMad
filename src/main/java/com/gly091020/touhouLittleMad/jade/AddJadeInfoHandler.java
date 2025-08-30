package com.gly091020.touhouLittleMad.jade;

import com.github.tartaricacid.touhoulittlemaid.api.event.AddJadeInfoEvent;
import com.gly091020.touhouLittleMad.LittleMadMod;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = LittleMadMod.ModID)
public class AddJadeInfoHandler {
    @SubscribeEvent
    public static void onAddInfo(AddJadeInfoEvent event){
        if(event.getMaid() instanceof MaidMadExtraData data){
            event.getTooltip().add(Component.translatable("gui.touhou_little_mad.mood_level.mood").append(": ")
                    .append(data.getMoodLevel().getName()));
        }
    }
}
