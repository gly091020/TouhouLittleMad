package com.gly091020.touhouLittleMad.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;

@Getter
public class MaidStopSleepingEvent extends Event {
    private final EntityMaid maid;
    private final boolean byHurt;

    public MaidStopSleepingEvent(EntityMaid maid, boolean byHurt) {
        this.byHurt = byHurt;
        this.maid = maid;
    }
}
