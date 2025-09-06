package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.block.BlockShrine;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.behavior.MaidRespawnEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockShrine.class)
public class MaidRespawnMixin {
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lcom/github/tartaricacid/touhoulittlemaid/advancements/maid/MaidEventTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;)V"))
    private static void onRespawn(ItemStack itemStack, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir){
        // 很烂的写法
        var entities = worldIn.getNearbyEntities(EntityMaid.class, TargetingConditions.DEFAULT, playerIn, new AABB(pos).inflate(1));
        if(!entities.isEmpty()){
            NeoForge.EVENT_BUS.post(new MaidRespawnEvent(entities.getFirst()));
        }
    }
}
