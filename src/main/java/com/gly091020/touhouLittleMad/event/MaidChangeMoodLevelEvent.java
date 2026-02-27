package com.gly091020.touhouLittleMad.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.MoodLevelType;
import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;

@Getter
public class MaidChangeMoodLevelEvent extends Event {
    private final EntityMaid maid;
    private final MoodLevelType oldLevel;
    private final MoodLevelType newLevel;

    public MaidChangeMoodLevelEvent(EntityMaid maid, MoodLevelType old, MoodLevelType _new) {
        this.maid = maid;
        oldLevel = old;
        newLevel = _new;
    }
}
