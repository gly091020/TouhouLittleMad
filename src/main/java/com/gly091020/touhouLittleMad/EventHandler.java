package com.gly091020.touhouLittleMad;

import com.github.tartaricacid.touhoulittlemaid.api.event.*;
import com.github.tartaricacid.touhoulittlemaid.api.event.client.RenderMaidEvent;
import com.gly091020.touhouLittleMad.behavior.MaidSendGiftGoal;
import com.gly091020.touhouLittleMad.datagen.DataGenerators;
import com.gly091020.touhouLittleMad.event.MaidChangeMoodLevelEvent;
import com.gly091020.touhouLittleMad.event.MaidStopSleepingEvent;
import com.gly091020.touhouLittleMad.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LittleMadMod.ModID)
public class EventHandler {
    @SubscribeEvent
    public static void onHurt(MaidHurtEvent event){
        // 当女仆被攻击，如果是主人直接掉最多30心情，否则掉最多5心情并冷却50tick
        // 心情回复冷却1分钟
        if(event.getMaid().level().isClientSide){return;}
        if(!(event.getMaid() instanceof MaidMadExtraData data)){return;}
        var owner = event.getMaid().getOwner();
        if(owner != null){
            if(owner == event.getSource().getEntity()){
                data.setHandledMood(data.getMood() + (int) (Math.clamp(event.getAmount() * 10, 0, 10) / 10 * 30));
                if(owner instanceof ServerPlayer player){
                    MadMaidFunction.maidTrigger(player, AdvancementTriggerKeys.HURT_BY_OWNER);
                }
            }else if(data.getCooldown().notInCooldown(CooldownKeys.HURT)){
                data.setHandledMood(data.getMood() + (int) (Math.clamp(event.getAmount() * 10, 0, 10) / 10 * 5));
                data.getCooldown().setTimer(CooldownKeys.HURT, 50);
            }
            data.getCooldown().setTimer(CooldownKeys.RECOVER, 60 * 20);
        }
    }

    @SubscribeEvent
    public static void tick(MaidTickEvent event){
        // 心情每次回复冷却10秒
        // 女仆工作时随机减少心情
        if(event.getEntity().level().isClientSide){return;}
        var maid = event.getMaid();
        if(event.getEntity() instanceof MaidMadExtraData data){
            var cooldown = data.getCooldown();
            cooldown.tick();
            if(cooldown.notInCooldown(CooldownKeys.RECOVER) && MadMaidFunction.canRecoverMood(maid)){
                data.setHandledMood(data.getMood() - 1);
                data.getCooldown().setTimer(CooldownKeys.RECOVER, 10 * 20);
            }
            var probability = TaskMoodRegistry.getProbability(maid.getTask().getClass());
            if(probability > 0 && maid.getRandom().nextFloat() < probability){
                data.setHandledMood(data.getMood() + 1);
            }
            if(data.getMoodLevel() == MoodLevelType.GOOD && cooldown.notInCooldown(CooldownKeys.ADD_POINT)){
                // 女仆开心时增加好感度并冷却8~10分钟
                maid.getFavorabilityManager().add(1);
                cooldown.setTimer(CooldownKeys.ADD_POINT, (int) ((8 + 2 * maid.getRandom().nextFloat()) * 10 * 20));
            }
        }
    }

    @SubscribeEvent
    public static void stopSleepEvent(MaidStopSleepingEvent event){
        // 女仆起床如果是被攻击扣30点心情并冷却回复两分钟，否则回复10点心情
        var maid = event.getMaid();
        if(maid.level().isClientSide){return;}
        if(maid instanceof MaidMadExtraData data){
            if (event.isByHurt()) {
                data.getCooldown().setTimer(CooldownKeys.RECOVER, 2 * 60 * 20);
                data.setHandledMood(data.getMood() + 30);
            }else{
                data.getCooldown().setTimer(CooldownKeys.RECOVER, 10 * 20);
                data.setHandledMood(data.getMood() - 10);
                if(data.getMoodLevel() == MoodLevelType.GOOD && maid.getOwner() instanceof Player player && maid.position().closerThan(player.position(), 10)){
                    maid.goalSelector.addGoal(10, new MaidSendGiftGoal(maid));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDie(MaidDeathEvent event){
        // 女仆死亡减少50心情并冷却恢复10分钟（最高110防止复活了接着打）
        if(event.getMaid().level().isClientSide){return;}
        if(event.getMaid() instanceof MaidMadExtraData data){
            data.setMood(Math.clamp(data.getMood() + 50, 50, 110));
            data.getCooldown().setTimer(CooldownKeys.RECOVER, 10 * 60 * 20);
        }
    }

    @SubscribeEvent
    public static void onEat(MaidAfterEatEvent event){
        // 吃东西加5心情
        if(event.getMaid().level().isClientSide){return;}
        if(event.getMaid() instanceof MaidMadExtraData data &&
                MadMaidFunction.canRecoverMood(event.getMaid())){
            data.setHandledMood(data.getMood() - 5);
        }
    }
    @SubscribeEvent
    public static void onInteractMaid(InteractMaidEvent event){
        // 女仆生气打人时不能打开ui
        if(!event.getPlayer().isCreative() && event.getMaid() instanceof MaidMadExtraData data &&
                data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()){
            event.getPlayer().sendSystemMessage(Component.translatable("gui.touhou_little_mad.not_open"));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRender(RenderMaidEvent event){
        // 生气粒子
        if(Minecraft.getInstance().isPaused()){return;}
        if(event.getMaid() instanceof MaidMadExtraData data &&
                data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()){
            var maid = event.getMaid().asEntity();
            if(maid.level().getGameTime() % 10 != 0){return;}
            double d0 = maid.getRandom().nextGaussian() * 0.02;
            double d1 = maid.getRandom().nextGaussian() * 0.02;
            double d2 = maid.getRandom().nextGaussian() * 0.02;
            maid.level().addParticle(ParticleTypes.ANGRY_VILLAGER, maid.getRandomX(1.0F), maid.getRandomY() + (double)0.8F, maid.getRandomZ(1.0F), d0, d1, d2);
        }
    }

    @SubscribeEvent
    public static void onChangeMoodLevel(MaidChangeMoodLevelEvent event){
        // 女仆心情好时增加攻击速度和攻击伤害（生气也会）
        // （但是女仆好像不使用攻击速度属性）
        var attack_speed = event.getMaid().getAttribute(Attributes.ATTACK_SPEED);
        if(attack_speed != null){
            attack_speed.setBaseValue(event.getNewLevel().getAttackSpeed());
        }

        var attack = event.getMaid().getAttribute(Attributes.ATTACK_DAMAGE);
        if(attack != null){
            var manager = event.getMaid().getFavorabilityManager();
            attack.setBaseValue(manager.getAttackByLevel(manager.getLevel()) *
                    event.getNewLevel().getAttackDamageMagnification());
        }

        if(event.getNewLevel() == MoodLevelType.MAD &&
                event.getMaid().getOwner() instanceof ServerPlayer player){
            MadMaidFunction.maidTrigger(player, AdvancementTriggerKeys.MAD);
        }
    }

    @SubscribeEvent
    public static void onDataGen(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        if (event.includeServer()) {
            generator.addProvider(true, new DataGenerators.LootTableGen(packOutput, lookupProvider));
            generator.addProvider(true, new DataGenerators.AllAdvancementProvider(packOutput, lookupProvider, existingFileHelper));
        }else{

        }
    }
}
