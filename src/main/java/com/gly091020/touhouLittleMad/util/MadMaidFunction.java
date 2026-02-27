package com.gly091020.touhouLittleMad.util;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.*;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.gly091020.touhouLittleMad.LittleMadMod;
import com.gly091020.touhouLittleMad.config.LittleMadConfig;
import com.gly091020.touhouLittleMad.config.TaskMoodConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Objects;

public class MadMaidFunction {
    private static final ResourceLocation GIFT_LOOT_TABLE = new ResourceLocation(LittleMadMod.ModID, "maid_gift");

    public static boolean canRecoverMood(EntityMaid maid) {
        // 女仆只有非全天工作才能回复心情
        return maid.getSchedule() != MaidSchedule.ALL;
    }

    public static void getDebugTooltip(EntityMaid maid, List<Component> list) {
        if (maid instanceof MaidMadExtraData data) {
            list.add(Component.literal("LittleMad调试："));
            list.add(Component.literal("内部心情：" + Objects.toString(data.getMood())));
            list.add(Component.literal("心情等级：" + data.getMoodLevel().getDebugText()));
        }
    }

    public static void registryBuiltInTask() {
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

    public static ItemStack getGift(EntityMaid maid) {
        if (!(maid.level() instanceof ServerLevel level)) {
            return ItemStack.EMPTY.copy();
        }
        var loot_table = level.getServer().getLootData().getLootTable(GIFT_LOOT_TABLE);
        var params = new LootParams.Builder(level).create(LootContextParamSets.GIFT);
        var items = loot_table.getRandomItems(params);
        if (items.isEmpty()) {
            return ItemStack.EMPTY.copy();
        } else {
            ItemStack result = items.get(0);
            if (result.is(InitItems.GARAGE_KIT.get())) {
                var tag = new CompoundTag();
                maid.saveWithoutId(tag);
                tag.putString("id", Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(InitEntities.MAID.get())).toString());
                result.getOrCreateTag().put("MaidData", tag);
            }
            return result;
        }
    }

    public static Component getMadText(EntityMaid maid) {
        final int count = 5;
        return Component.translatable("entity.touhou_little_mad.mad." + (1 + maid.getRandom().nextInt(count)));
    }

    public static void maidTrigger(ServerPlayer serverPlayer, String key) {
        InitTrigger.MAID_EVENT.trigger(serverPlayer, key);
    }

    public static void loadConfig() {
        LittleMadMod.CONFIG = AutoConfig.getConfigHolder(LittleMadConfig.class).getConfig();
        TaskMoodConfig.load();
    }

    public static void saveConfig() {
        AutoConfig.getConfigHolder(LittleMadConfig.class).setConfig(LittleMadMod.CONFIG);
        AutoConfig.getConfigHolder(LittleMadConfig.class).save();
        TaskMoodConfig.save();
    }

    public static void reloadConfig() {
        saveConfig();
        loadConfig();
    }
}
