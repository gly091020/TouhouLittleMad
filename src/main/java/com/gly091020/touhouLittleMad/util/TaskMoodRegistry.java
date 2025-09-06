package com.gly091020.touhouLittleMad.util;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.gly091020.touhouLittleMad.config.TaskMoodConfig;

import java.util.HashMap;
import java.util.Map;

public class TaskMoodRegistry {
    private static final Map<Class<? extends IMaidTask>, Float> registryData = new HashMap<>();

    public static void registry(Class<? extends IMaidTask> task, float moodProbability){
        // 控制女仆工作时的心情变化概率（0~1）
        registryData.put(task, moodProbability);
    }

    public static float getProbability(Class<? extends IMaidTask> task){
        if(TaskMoodConfig.hasConfigMood(task)){
            return TaskMoodConfig.getConfigMood(task);
        }
        return registryData.getOrDefault(task, 0.01f);
    }

    public static Map<Class<? extends IMaidTask>, Float> getAllTaskMood(){
        return registryData;
    }
}
