package com.gly091020.touhouLittleMad.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;

@Getter
public class MaidRespawnEvent extends Event {
    private final EntityMaid maid;

    public MaidRespawnEvent(EntityMaid maid) {
        this.maid = maid;
    }
}
