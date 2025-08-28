package com.gly091020.touhouLittleMad;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAfterEatEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidHurtEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.event.MaidStopSleepingEvent;
import com.gly091020.touhouLittleMad.util.CooldownKeys;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
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
        if(event.getEntity().level().isClientSide){return;}
        if(event.getEntity() instanceof EntityMaid maid &&
                event.getEntity() instanceof MaidMadExtraData data){
            var cooldown = data.getCooldown();
            cooldown.tick();
            if(cooldown.notInCooldown(CooldownKeys.RECOVER) && MadMaidFunction.canRecoverMood(maid)){
                data.setHandledMood(data.getMood() - 1);
                data.getCooldown().setTimer(CooldownKeys.RECOVER, 10 * 20);
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
        // 女仆死亡减少50心情并冷却恢复10分钟
        if(event.getMaid().level().isClientSide){return;}
        if(event.getMaid() instanceof MaidMadExtraData data){
            data.setHandledMood(data.getMood() + 50);
            data.getCooldown().setTimer(CooldownKeys.RECOVER, 10 * 60 * 20);
        }
    }

    @SubscribeEvent
    public static void onEat(MaidAfterEatEvent event){
        if(event.getMaid().level().isClientSide){return;}
        if(event.getMaid() instanceof MaidMadExtraData data &&
                MadMaidFunction.canRecoverMood(event.getMaid())){
            data.setHandledMood(data.getMood() - 5);
        }
    }
}
