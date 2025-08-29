package com.gly091020.touhouLittleMad.util;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;

import java.util.HashMap;
import java.util.Map;

public class TaskMoodRegistry {
    private static final Map<Class<? extends IMaidTask>, Float> registryData = new HashMap<>();

    public static void registry(Class<? extends IMaidTask> task, float moodProbability){
        // 控制女仆工作时的心情变化概率（0~1）
        registryData.put(task, moodProbability);
    }

    public static float getProbability(Class<? extends IMaidTask> task){
        return registryData.getOrDefault(task, 0.01f);
    }
}
