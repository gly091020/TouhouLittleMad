package com.gly091020.touhouLittleMad.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskIdle;
import com.gly091020.touhouLittleMad.MoodLevelType;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class IdieToMadBehavior extends Behavior<EntityMaid> {
    private int trigger = 0;
    private MeleeAttackGoal goal;
    public IdieToMadBehavior() {
        super(Map.of());
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        // 不 要 开 和 平
        if(maid instanceof MaidMadExtraData data){
            if(data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()){
                var owner = maid.getOwner();
                if(owner != null) {
                    maid.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, owner);
                    maid.setTask(new TaskIdle());
                    maid.setHomeModeEnable(false);
                    if(trigger == 2){return;}
                    trigger = 1;
                }else{
                    if(trigger != 3 && goal != null){
                        maid.goalSelector.removeGoal(goal);
                    }
                    trigger = 3;
                }
                if(trigger == 1){
                    goal = new MeleeAttackGoal(maid, 1, true);
                    maid.goalSelector.addGoal(100, goal);
                    trigger = 2;
                }
                var target = maid.getTarget();
                if(target != null && !target.isAlive()) {
                    data.setMood(100);
                }
            }
        }
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, @NotNull EntityMaid entity, long gameTime) {
        return true;
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid entity, long gameTime) {
        super.stop(level, entity, gameTime);
        entity.goalSelector.removeGoal(goal);
    }
}
