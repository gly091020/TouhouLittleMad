package com.gly091020.touhouLittleMad;

import com.github.tartaricacid.touhoulittlemaid.api.event.*;
import com.github.tartaricacid.touhoulittlemaid.api.event.client.RenderMaidEvent;
import com.gly091020.touhouLittleMad.event.MaidStopSleepingEvent;
import com.gly091020.touhouLittleMad.util.CooldownKeys;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import com.gly091020.touhouLittleMad.util.TaskMoodRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onHurt(MaidHurtEvent event){
        // 当女仆被攻击，如果是主人直接掉最多20心情，否则掉最多5心情并冷却50tick
        // 心情回复冷却1分钟
        if(event.getMaid().level().isClientSide){return;}
        if(!(event.getMaid() instanceof MaidMadExtraData data)){return;}
        var owner = event.getMaid().getOwner();
        if(owner != null){
            if(owner == event.getSource().getEntity()){
                data.setHandledMood(data.getMood() + (int) (Math.clamp(event.getAmount() * 10, 0, 10) / 10 * 20));
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
        }
    }

    @SubscribeEvent
    public static void stopSleepEvent(MaidStopSleepingEvent event){
        // 女仆起床如果是被攻击扣30点心情并冷却回复两分钟，否则回复10点心情
        if(event.getMaid().level().isClientSide){return;}
        if(event.getMaid() instanceof MaidMadExtraData data){
            if (event.isByHurt()) {
                data.getCooldown().setTimer(CooldownKeys.RECOVER, 2 * 60 * 20);
                data.setHandledMood(data.getMood() + 30);
            }else{
                data.getCooldown().setTimer(CooldownKeys.RECOVER, 10 * 20);
                data.setHandledMood(data.getMood() - 10);
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
            event.getPlayer().sendSystemMessage(Component.literal("占位符：不能打开女仆界面"));  // todo:占位文本
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
}
