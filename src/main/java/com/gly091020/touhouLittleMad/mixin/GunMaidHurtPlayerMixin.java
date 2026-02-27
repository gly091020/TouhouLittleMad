package com.gly091020.touhouLittleMad.mixin;

import com.atsuishio.superbwarfare.api.event.ProjectileHitEvent;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare.event.GunHurtMaidEvent;
import com.gly091020.touhouLittleMad.MoodLevelType;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GunHurtMaidEvent.class, remap = false)
public class GunMaidHurtPlayerMixin {
    @Inject(method = "onGunHurt", at = @At("TAIL"), remap = false)
    public void hurtPlayer(ProjectileHitEvent.HitEntity event, CallbackInfo ci) {
        if (event.getOwner() instanceof MaidMadExtraData data) {
            if (data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()) {
                event.setCanceled(false);
            }
        }
    }

    @Inject(method = "onPlayerHurt", at = @At("TAIL"), remap = false)
    public void hurtPlayer1(LivingAttackEvent event, CallbackInfo ci) {
        if (event.getSource().getEntity() instanceof MaidMadExtraData data) {
            if (data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()) {
                event.setCanceled(false);
            }
        }
    }
}
