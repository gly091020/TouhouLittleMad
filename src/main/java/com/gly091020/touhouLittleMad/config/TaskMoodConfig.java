package com.gly091020.touhouLittleMad.config;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.gly091020.touhouLittleMad.LittleMadMod;
import com.gly091020.touhouLittleMad.util.TaskMoodRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskMoodConfig {
    private static final Gson GSON = new Gson();
    private static Map<String, Float> mood = new HashMap<>();

    public static Map<String, Float> getMood() {
        return mood;
    }

    public static boolean hasConfigMood(Class<? extends IMaidTask> task){
        return mood.containsKey(task.getName());
    }

    public static void setConfigMood(Class<? extends IMaidTask> task, float moodProbability){
        mood.put(task.getName(), moodProbability);
    }

    public static float getConfigMood(Class<? extends IMaidTask> task){
        if(mood.containsKey(task.getName())){
            return mood.get(task.getName());
        }
        return TaskMoodRegistry.getProbability(task);
    }

    private static void clearDefaultValue(){
        List<String> deleteList = new ArrayList<>();
        for(Class<? extends IMaidTask> task: TaskMoodRegistry.getAllTaskMood().keySet()){
            if(TaskMoodRegistry.getAllTaskMood().getOrDefault(task, 0.01f) == getConfigMood(task)) {
                deleteList.add(task.getName());
            }
        }
        for(String k: deleteList){
            mood.remove(k);
        }
    }

    public static void save(){
        clearDefaultValue();
        LittleMadMod.CONFIG.taskMood = GSON.toJson(mood);
    }

    public static void load(){
        try{
            mood = GSON.fromJson(LittleMadMod.CONFIG.taskMood, new TypeToken<Map<String, Float>>(){}.getType());
        } catch (JsonSyntaxException e) {
            LittleMadMod.CONFIG.taskMood = "{}";
            save();
        }
    }
}
