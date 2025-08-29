package com.gly091020.touhouLittleMad.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskIdle;
import com.gly091020.touhouLittleMad.MoodLevelType;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class IdieToMadBehavior extends Behavior<EntityMaid> {
    private int trigger = 0;
    private MeleeAttackGoal goal;
    private final Random random;
    public IdieToMadBehavior() {
        super(Map.of());
        random = new Random();
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        // 不 要 开 和 平
        if(maid instanceof MaidMadExtraData data){
            if(data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()){
                var owner = maid.getOwner();
                if(owner instanceof Player) {
                    maid.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, owner);
                    maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(owner, 1f, 2));
                    maid.getBrain().setActiveActivityIfPossible(Activity.FIGHT);
                    maid.setHomeModeEnable(false);

                    maid.getSchedulePos().setIdlePos(owner.blockPosition());

                    if(trigger == 2){return;}
                    if(maid.getTask().getClass() != TaskIdle.class){
                        maid.setTask(new TaskIdle());
                        return;
                    }
                    trigger = 1;
                }else{
                    if(trigger != 3 && goal != null){
                        maid.goalSelector.removeGoal(goal);
                        goal = null;
                    }
                    trigger = 3;
                }
                if(trigger == 1){
                    goal = new MeleeAttackGoal(maid, 1f, true);
                    maid.goalSelector.addGoal(1000, goal);
                    trigger = 2;
                }
                var target = maid.getTarget();
                if(target != null && !target.isAlive()) {
                    data.setMood(100);
                }
            }else{
                if(goal != null){
                    maid.goalSelector.removeGoal(goal);
                    goal = null;
                }
            }
            if (data.getMood() >= 90) {
                // 心情一般(超过90)有概率罢工
                // 每 tick 0 ~ 1%
                if(random.nextFloat() <= (Math.clamp(data.getMood(), 90, 120) - 90) / 30f / 50){
                    maid.setTask(new TaskIdle());
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
    protected void stop(@NotNull ServerLevel level, @NotNull EntityMaid entity, long gameTime) {
        super.stop(level, entity, gameTime);
        entity.goalSelector.removeGoal(goal);
        entity.refreshBrain(level);
    }
}
