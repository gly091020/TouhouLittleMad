package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidClearSleepTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.event.MaidStopSleepingEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SleepingMixins {
    @Mixin(MaidClearSleepTask.class)
    public static class StopSleepingMixin{
        @Inject(method = "start(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;J)V",
                at = @At(value = "INVOKE", target = "Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;stopSleeping()V"))
        public void onClearSleep(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn, CallbackInfo ci){
            NeoForge.EVENT_BUS.post(new MaidStopSleepingEvent(entityIn, false));
        }
    }

    @Mixin(LivingEntity.class)
    public static class StopSleepingByHurtMixin{
        @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;stopSleeping()V"))
        public void onStopSleep(DamageSource damageSource, float p_21017_, CallbackInfoReturnable<Boolean> cir){
            if(damageSource.getEntity() instanceof EntityMaid maid){
                NeoForge.EVENT_BUS.post(new MaidStopSleepingEvent(maid, true));
            }
        }
    }
}
