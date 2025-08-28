package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidSnowballTargetTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.MoodLevelType;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MaidSnowballTargetTask.class)
public class PlaySnowBallMixin {
    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;J)V", at = @At("HEAD"), cancellable = true)
    public void notPlay(ServerLevel worldIn, EntityMaid maid, long gameTime, CallbackInfo ci){
        if(maid instanceof MaidMadExtraData data && data.getMoodLevel().ordinal() >= MoodLevelType.NORMAL.ordinal()){
            ci.cancel();
        }
    }

    @Inject(method = "start(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;J)V", at = @At("HEAD"), cancellable = true)
    public void notStart(ServerLevel worldIn, EntityMaid maid, long gameTime, CallbackInfo ci){
        if(maid instanceof MaidMadExtraData data && data.getMoodLevel().ordinal() >= MoodLevelType.NORMAL.ordinal()){
            ci.cancel();
        }
    }
}
