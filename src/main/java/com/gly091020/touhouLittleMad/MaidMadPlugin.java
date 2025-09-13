package com.gly091020.touhouLittleMad;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.behavior.AttackPlayerUseGunBehavior;
import com.gly091020.touhouLittleMad.behavior.IdieToMadBehavior;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

import java.util.List;

@LittleMaidExtension
public class MaidMadPlugin implements ILittleMaid {
    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
        // 女仆生气时会攻击主人
        manager.addExtraMaidBrain(new MadExtraMaidBrain());
    }

    public static class MadExtraMaidBrain implements IExtraMaidBrain {
        @Override
        public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> getCoreBehaviors() {
            return List.of(Pair.of(100, new IdieToMadBehavior()), Pair.of(10, new AttackPlayerUseGunBehavior()));
        }
    }
}
