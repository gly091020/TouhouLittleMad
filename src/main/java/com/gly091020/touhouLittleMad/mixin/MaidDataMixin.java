package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.LittleMadMod;
import com.gly091020.touhouLittleMad.util.MaidCooldown;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            setMood(compound.getInt("MaidMood"));
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
    public void setMood(int mood) {
        // todo:需要上限
        ((EntityMaid)(Object)this).getEntityData().set(MOOD, Math.clamp(mood, 0, Integer.MAX_VALUE));
    }

    @Override
    public MaidCooldown getCooldown() {
        // 冷却理论上仅服务端计算
        if(((Entity)(Object)this).level().isClientSide){
            LittleMadMod.LOGGER.warn("有客户端代码调用Cooldown");
        }
        return cooldown;
    }
}
