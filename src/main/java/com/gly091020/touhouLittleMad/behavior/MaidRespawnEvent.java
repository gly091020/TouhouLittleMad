package com.gly091020.touhouLittleMad.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.neoforged.bus.api.Event;

public class MaidRespawnEvent extends Event {
    private final EntityMaid maid;
    public MaidRespawnEvent(EntityMaid maid){
        this.maid = maid;
    }

    public EntityMaid getMaid() {
        return maid;
    }
}
