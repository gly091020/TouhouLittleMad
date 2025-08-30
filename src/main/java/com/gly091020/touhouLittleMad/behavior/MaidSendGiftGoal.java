package com.gly091020.touhouLittleMad.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.implement.TextChatBubbleData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class MaidSendGiftGoal extends Goal {
    private final EntityMaid maid;
    @Override
    public boolean canUse() {
        var owner = maid.getOwner();
        if(owner == null){return false;}
        return !maid.position().closerThan(owner.position(), 3);
    }

    public MaidSendGiftGoal(EntityMaid maid){
        this.maid = maid;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public void tick() {
        var owner = maid.getOwner();
        if(owner == null){return;}
        maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(owner, 0.5f, 3));
        maid.getBrain().setMemory(MemoryModuleType.LOOK_TARGET,
                new BlockPosTracker(owner.position()));
    }

    @Override
    public void stop() {
        super.stop();
        var owner = maid.getOwner();
        if(owner instanceof Player player){
            player.addItem(MadMaidFunction.getGift(maid));
        }
        maid.getChatBubbleManager().addChatBubble(TextChatBubbleData.type2(Component.translatable("entity.touhou_little_mad.send_gift")));
        maid.goalSelector.removeGoal(this);
    }
}
