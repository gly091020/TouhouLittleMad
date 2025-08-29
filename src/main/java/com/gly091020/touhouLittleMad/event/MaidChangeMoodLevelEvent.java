package com.gly091020.touhouLittleMad.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.MoodLevelType;
import net.neoforged.bus.api.Event;

public class MaidChangeMoodLevelEvent extends Event {
    private final EntityMaid maid;
    private final MoodLevelType oldLevel;
    private final MoodLevelType newLevel;

    public MaidChangeMoodLevelEvent(EntityMaid maid, MoodLevelType old, MoodLevelType _new){
        this.maid = maid;
        oldLevel = old;
        newLevel = _new;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public MoodLevelType getNewLevel() {
        return newLevel;
    }

    public MoodLevelType getOldLevel() {
        return oldLevel;
    }
}
