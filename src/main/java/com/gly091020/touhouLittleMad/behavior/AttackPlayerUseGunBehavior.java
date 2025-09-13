package com.gly091020.touhouLittleMad.behavior;

import com.github.tartaricacid.touhoulittlemaid.compat.gun.common.GunCommonUtil;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.common.ai.GunShootTargetTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.MoodLevelType;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.server.level.ServerLevel;

public class AttackPlayerUseGunBehavior extends GunShootTargetTask {
    @Override
    protected boolean canStillUse(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        return true;
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected void tick(ServerLevel worldIn, EntityMaid owner, long gameTime) {
        if(((MaidMadExtraData)owner).getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal() &&
                GunCommonUtil.isGun(owner.getMainHandItem())){
            super.tick(worldIn, owner, gameTime);
        }
    }
}
