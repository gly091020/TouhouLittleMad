package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.ai.manager.entity.MaidAIChatManager;
import com.github.tartaricacid.touhoulittlemaid.ai.service.llm.LLMMessage;
import com.gly091020.touhouLittleMad.event.MaidAddChatListEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MaidAIChatManager.class)
public class MaidAIChatMixin {
    @Inject(method = "getChatCompletion", at = @At(value = "RETURN"))
    private void genSetting(MaidAIChatManager chatManager, String language, CallbackInfoReturnable<List<LLMMessage>> cir){
        NeoForge.EVENT_BUS.post(new MaidAddChatListEvent(chatManager.getMaid(), cir.getReturnValue()));
    }
}
