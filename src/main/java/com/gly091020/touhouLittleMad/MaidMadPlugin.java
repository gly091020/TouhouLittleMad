package com.gly091020.touhouLittleMad;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.behavior.IdieToMadBehavior;
import com.gly091020.touhouLittleMad.util.GunBehaviorsAdd;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
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
            var be = new ArrayList<Pair<Integer, BehaviorControl<? super EntityMaid>>>();
            be.add(Pair.of(100, new IdieToMadBehavior()));
            if(hasGunMod()){
                GunBehaviorsAdd.add(be);
            }
            return be;
        }
    }

    public static boolean hasGunMod(){
        return ModList.get().isLoaded("tacz") || ModList.get().isLoaded("superbwarfare");
    }
}
