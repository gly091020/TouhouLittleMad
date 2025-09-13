package com.gly091020.touhouLittleMad.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.behavior.AttackPlayerUseGunBehavior;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

import java.util.ArrayList;

public class GunBehaviorsAdd {
    public static void add(ArrayList<Pair<Integer, BehaviorControl<? super EntityMaid>>> list){
        list.add(Pair.of(10, new AttackPlayerUseGunBehavior()));
    }
}
