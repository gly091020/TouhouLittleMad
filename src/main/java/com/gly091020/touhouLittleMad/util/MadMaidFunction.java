package com.gly091020.touhouLittleMad.util;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public class MadMaidFunction {
    public static boolean canRecoverMood(EntityMaid maid){
        // 女仆只有非全天工作才能回复心情
        return maid.getSchedule() != MaidSchedule.ALL;
    }

    public static void getDebugTooltip(EntityMaid maid, List<Component> list){
        if(maid instanceof MaidMadExtraData data){
            list.add(Component.literal("LittleMad调试："));
            list.add(Component.literal("内部心情：" + Objects.toString(data.getMood())));
            list.add(Component.literal("心情等级：" + data.getMoodLevel().getDebugText()));
        }
    }
}
