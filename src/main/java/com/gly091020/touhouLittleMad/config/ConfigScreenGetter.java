package com.gly091020.touhouLittleMad.config;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import com.gly091020.touhouLittleMad.util.TaskMoodRegistry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;

import static com.gly091020.touhouLittleMad.LittleMadMod.CONFIG;

public class ConfigScreenGetter {
    public static Screen getConfigScreen(ModContainer mc, Screen parent){
        var configBuilder = ConfigBuilder.create();
        configBuilder.setParentScreen(parent).setTitle(getComponent("title"));
        var category = configBuilder.getOrCreateCategory(configBuilder.getTitle());
        var entryBuilder = configBuilder.entryBuilder();
        addCategoryContent(entryBuilder, category);
        configBuilder.setSavingRunnable(MadMaidFunction::reloadConfig);
        return configBuilder.build();
    }

    public static void addCategoryContent(ConfigEntryBuilder entryBuilder, ConfigCategory category){
        category.addEntry(entryBuilder.startBooleanToggle(getComponent("enable_attack"), CONFIG.enableAttack)
                .setDefaultValue(true)
                .setSaveConsumer(b -> CONFIG.enableAttack = b)
                .setYesNoTextSupplier(ConfigScreenGetter::getEnableOrDisableText)
                .build());
        category.addEntry(entryBuilder.startBooleanToggle(getComponent("enable_gift"), CONFIG.enableGift)
                .setDefaultValue(true)
                .setSaveConsumer(b -> CONFIG.enableGift = b)
                .setYesNoTextSupplier(ConfigScreenGetter::getEnableOrDisableText)
                .build());
        var moods = entryBuilder.startSubCategory(getComponent("task_mood"));
        var taskMoods = TaskMoodRegistry.getAllTaskMood();
        for (Class<? extends IMaidTask> taskClass: taskMoods.keySet()) {
            try {
                moods.add(entryBuilder.startFloatField(taskClass.getDeclaredConstructor().newInstance().getName(), TaskMoodRegistry.getProbability(taskClass))
                                .setDefaultValue(TaskMoodRegistry.getAllTaskMood().getOrDefault(taskClass, 0.01f))
                        .setSaveConsumer(m -> TaskMoodConfig.setConfigMood(taskClass, m))
                        .build());
            } catch (Exception ignored) {}
        }
        category.addEntry(moods.build());
    }

    public static Component getEnableOrDisableText(boolean enable){
        return enable ? Component.literal("§a")
                .append(getComponent("enable")) : Component.literal("§a")
                .append(getComponent("disable"));
    }

    public static Component getComponent(String name){
        return Component.translatable("config.touhou_little_mad." + name);
    }
}
