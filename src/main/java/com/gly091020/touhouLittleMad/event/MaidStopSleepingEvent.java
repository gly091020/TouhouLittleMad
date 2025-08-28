package com.gly091020.touhouLittleMad.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.neoforged.bus.api.Event;

public class MaidStopSleepingEvent extends Event {
    private final EntityMaid maid;
    private final boolean byHurt;
    public MaidStopSleepingEvent(EntityMaid maid, boolean byHurt){
        this.byHurt = byHurt;
        this.maid = maid;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public boolean isByHurt() {
        return byHurt;
    }
}
