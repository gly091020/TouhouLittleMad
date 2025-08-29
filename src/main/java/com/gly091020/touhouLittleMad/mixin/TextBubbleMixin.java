package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.RandomEmoji;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomEmoji.class)
public abstract class TextBubbleMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/tartaricacid/touhoulittlemaid/entity/chatbubble/ChatBubbleManager;addChatBubble(Lcom/github/tartaricacid/touhoulittlemaid/entity/chatbubble/IChatBubbleData;)J"), cancellable = true)
    private static void addBubble(EntityMaid maid, CallbackInfo ci){
        // 女仆生气时减少对话气泡显示
        if(maid instanceof MaidMadExtraData data && maid.getRandom().nextFloat() <= (Math.clamp(data.getMood(), 60, 120) - 60) / 60f){
            ci.cancel();
        }
    }
}
