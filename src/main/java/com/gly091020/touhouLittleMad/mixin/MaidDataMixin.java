package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.TextChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.LittleMadMod;
import com.gly091020.touhouLittleMad.MoodLevelType;
import com.gly091020.touhouLittleMad.event.MaidChangeMoodLevelEvent;
import com.gly091020.touhouLittleMad.util.MaidCooldown;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Implements({
        @Interface(
                iface = MaidMadExtraData.class, prefix = "MaidDataMixin$")
})
@Mixin(EntityMaid.class)
public abstract class MaidDataMixin implements MaidMadExtraData {
    @Unique
    private static final EntityDataAccessor<Integer> MOOD = SynchedEntityData.defineId(EntityMaid.class, EntityDataSerializers.INT);

    @Unique
    private MaidCooldown cooldown = new MaidCooldown();

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void addData(CompoundTag compound, CallbackInfo ci){
        compound.putInt("MaidMood", getMood());
        cooldown.saveToNbt(compound);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void readData(CompoundTag compound, CallbackInfo ci){
        if(compound.contains("MaidMood", CompoundTag.TAG_INT)){
            this.setHandledMood(compound.getInt("MaidMood"));
        }
        cooldown = MaidCooldown.readFromNbt(compound);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void defineData(SynchedEntityData.Builder builder, CallbackInfo ci){
        builder.define(MOOD, 0);
    }

    @Override
    public int getMood() {
        // 心情值的减少和增加相反
        return ((EntityMaid)(Object)this).getEntityData().get(MOOD);
    }

    @Override
    public void setHandledMood(int mood) {
        var magnification = 0f;
        var add = Math.abs(mood - getMood());
        if(mood > getMood()){
            magnification = getMoodLevel().getAddMagnification();
        } else if (mood < getMood()) {
            magnification = -getMoodLevel().getSubMagnification();
        }
        if(LittleMadMod.debug){
            ((EntityMaid)(Object)this).getChatBubbleManager().addChatBubble(TextChatBubbleData.create(20, Component.literal(
                    "心情变化：" + Objects.toString(add * magnification)
            ), TextChatBubbleData.TYPE_1, 1));
        }
        setMood((int)(getMood() + add * magnification));
    }

    @Override
    public void setMood(int mood) {
        var oldLevel = getMoodLevel();
        var maid = ((EntityMaid)(Object)this);
        maid.getEntityData().set(MOOD, Math.clamp(mood, 0, 180));
        var newLevel = getMoodLevel();
        if(oldLevel != newLevel){
            NeoForge.EVENT_BUS.post(new MaidChangeMoodLevelEvent(maid, oldLevel, newLevel));
        }
    }

    @Unique
    public MoodLevelType getMoodLevel(){
        return MoodLevelType.getType(getMood());
    }

    @Override
    public MaidCooldown getCooldown() {
        // 冷却理论上仅服务端计算
        if(((Entity)(Object)this).level().isClientSide){
            LittleMadMod.LOGGER.warn("有客户端代码调用Cooldown");
        }
        return cooldown;
    }

    @Inject(method = "getAmbientSound", at = @At("TAIL"), cancellable = true)
    public void getSound(CallbackInfoReturnable<SoundEvent> cir){
        // 没有用事件系统因为无法区分音效
        if(cir.getReturnValue() == null){return;}
        var maid = ((EntityMaid)(Object)this);
        if(maid instanceof MaidMadExtraData data && maid.getRandom().nextFloat() <= (Math.clamp(data.getMood(), 60, 120) - 60) / 60f){
            cir.setReturnValue(null);
        }
    }
}
