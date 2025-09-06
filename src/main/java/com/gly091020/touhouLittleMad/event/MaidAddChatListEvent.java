package com.gly091020.touhouLittleMad.event;

import com.github.tartaricacid.touhoulittlemaid.ai.service.llm.LLMMessage;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.neoforged.bus.api.Event;

import java.util.List;

public class MaidAddChatListEvent extends Event {
    private final EntityMaid maid;
    private final List<LLMMessage> chatList;
    public MaidAddChatListEvent(EntityMaid maid, List<LLMMessage> chatList){
        this.maid = maid;
        this.chatList = chatList;
    }

    public void addChat(LLMMessage message){
        chatList.add(message);
    }

    public EntityMaid getMaid() {
        return maid;
    }
}
