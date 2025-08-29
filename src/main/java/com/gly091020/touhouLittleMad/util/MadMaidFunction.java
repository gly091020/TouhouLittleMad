package com.gly091020.touhouLittleMad.util;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.*;
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

    public static void registryBuiltInTask(){
        TaskMoodRegistry.registry(TaskAttack.class, 1f / 1200);
        TaskMoodRegistry.registry(TaskBoardGames.class, 0f);
        TaskMoodRegistry.registry(TaskBowAttack.class, 1f / 1400);
        TaskMoodRegistry.registry(TaskCocoa.class, 1f / 2000);
        TaskMoodRegistry.registry(TaskCrossBowAttack.class, 1f / 1400);
        TaskMoodRegistry.registry(TaskDanmakuAttack.class, 1f / 1400);
        TaskMoodRegistry.registry(TaskExtinguishing.class, 1f / 1800);
        TaskMoodRegistry.registry(TaskFeedAnimal.class, 1f / 1900);
        TaskMoodRegistry.registry(TaskFeedOwner.class, 1f / 1800);
        TaskMoodRegistry.registry(TaskFishing.class, 1f / 2500);
        TaskMoodRegistry.registry(TaskGrass.class, 1f / 2000);
        TaskMoodRegistry.registry(TaskHoney.class, 1f / 1900);
        TaskMoodRegistry.registry(TaskIdle.class, 0f);
        TaskMoodRegistry.registry(TaskMelon.class, 1f / 1900);
        TaskMoodRegistry.registry(TaskMilk.class, 1f / 1900);
        TaskMoodRegistry.registry(TaskNormalFarm.class, 1f / 2000);
        TaskMoodRegistry.registry(TaskShears.class, 1f / 1900);
        TaskMoodRegistry.registry(TaskSnow.class, 1f / 2000);
        TaskMoodRegistry.registry(TaskSugarCane.class, 1f / 2000);
        TaskMoodRegistry.registry(TaskTorch.class, 1f / 2000);
        TaskMoodRegistry.registry(TaskTridentAttack.class, 1f / 1400);
    }
}
