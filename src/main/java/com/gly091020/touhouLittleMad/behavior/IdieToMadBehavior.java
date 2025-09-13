package com.gly091020.touhouLittleMad.behavior;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskIdle;
import com.gly091020.touhouLittleMad.MoodLevelType;
import com.gly091020.touhouLittleMad.util.AdvancementTriggerKeys;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class IdieToMadBehavior extends Behavior<EntityMaid> {
    // 让AI重写了，我之前写的什么
    // 生气时攻击最后攻击者，疯狂时攻击主人
    private final RandomSource random;
    private MeleeAttackGoal attackGoal;
    private TriggerState triggerState = TriggerState.INITIAL;
    @Nullable
    private LivingEntity currentTarget;
    private int targetChangeCooldown = 0;

    public IdieToMadBehavior() {
        super(Map.of());
        random = RandomSource.create();
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        if (!(maid instanceof MaidMadExtraData data)) {
            return;
        }

        // 更新冷却时间
        if (targetChangeCooldown > 0) {
            targetChangeCooldown--;
        }

        // 检查心情是否恶劣
        if (data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()) {
            handleBadMoodBehavior(level, maid, data);
        } else {
            clearAttackGoal(maid);
            currentTarget = null;
            triggerState = TriggerState.INITIAL;
        }

        // 心情一般时可能罢工
        handleStrikeBehavior(maid, data);
    }

    /**
     * 处理心情恶劣时的行为
     */
    private void handleBadMoodBehavior(ServerLevel level, EntityMaid maid, MaidMadExtraData data) {
        // 确定当前目标
        LivingEntity newTarget = determineTarget(maid, data);

        // 检查目标是否发生变化
        if (hasTargetChanged(newTarget)) {
            handleTargetChange(maid, newTarget);
        }

        if (currentTarget != null) {
            handleTargetPresent(level, maid, data, currentTarget);
        } else {
            handleTargetAbsent(maid);
        }
    }

    /**
     * 确定攻击目标
     */
    @Nullable
    private LivingEntity determineTarget(EntityMaid maid, MaidMadExtraData data) {
        if (data.getMoodLevel() == MoodLevelType.MAD) {
            // MAD状态攻击主人
            return maid.getOwner();
        } else {
            // BAD状态攻击最后攻击者
            return maid.getLastAttacker();
        }
    }

    /**
     * 检查目标是否发生变化
     */
    private boolean hasTargetChanged(@Nullable LivingEntity newTarget) {
        if (targetChangeCooldown > 0) {
            return false; // 冷却期间不切换目标
        }

        if (currentTarget == null && newTarget == null) {
            return false;
        }
        if (currentTarget == null || newTarget == null) {
            return true;
        }
        return !currentTarget.equals(newTarget);
    }

    /**
     * 处理目标变化
     */
    private void handleTargetChange(EntityMaid maid, @Nullable LivingEntity newTarget) {
        // 清除旧目标
        clearAttackGoal(maid);
        maid.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);

        // 设置新目标
        currentTarget = newTarget;
        triggerState = TriggerState.INITIAL;

        // 设置目标切换冷却
        targetChangeCooldown = 20; // 1秒冷却
    }

    /**
     * 目标存在时的处理逻辑
     */
    private void handleTargetPresent(ServerLevel level, EntityMaid maid, MaidMadExtraData data, LivingEntity target) {
        // 检查目标是否有效
        if (!isTargetValid(target)) {
            handleTargetAbsent(maid);
            return;
        }

        // 设置攻击目标
        maid.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(target, 1.0f, 2));
        maid.getBrain().setActiveActivityIfPossible(Activity.FIGHT);
        maid.setHomeModeEnable(false);

        if (target instanceof Player player) {
            maid.getSchedulePos().setIdlePos(player.blockPosition());
        }

        // 检查目标是否已死亡
        if (isTargetDead(target)) {
            data.setMood(100);
            handleTargetAbsent(maid);
            return;
        }

        // 处理不同触发状态
        switch (triggerState) {
            case INITIAL -> handleInitialTrigger(level, maid, target, data);
            case ATTACKING -> {} // 攻击中，无需操作
            case RESET -> handleResetState(maid);
        }
    }

    /**
     * 检查目标是否有效
     */
    private boolean isTargetValid(@Nullable LivingEntity target) {
        return target != null && target.isAlive();
    }

    /**
     * 目标不存在时的处理逻辑
     */
    private void handleTargetAbsent(EntityMaid maid) {
        clearAttackGoal(maid);
        maid.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        currentTarget = null;
        triggerState = TriggerState.RESET;
    }

    /**
     * 处理初始触发状态
     */
    private void handleInitialTrigger(ServerLevel level, EntityMaid maid, LivingEntity target, MaidMadExtraData data) {
        // 如果目标是玩家，触发成就
        if (target instanceof ServerPlayer serverPlayer) {
            MadMaidFunction.maidTrigger(serverPlayer, AdvancementTriggerKeys.HURT_OWNER);
        }

        // 切换到空闲任务（如果需要）
        if (!maid.getTask().getClass().equals(TaskIdle.class)) {
            maid.setTask(new TaskIdle());
            return;
        }

        // 添加攻击目标
        addAttackGoal(maid);
        triggerState = TriggerState.ATTACKING;
    }

    /**
     * 添加攻击目标
     */
    private void addAttackGoal(EntityMaid maid) {
        if (attackGoal == null) {
            attackGoal = new MeleeAttackGoal(maid, 1.0f, true) {
                @Override
                public boolean canUse() {
                    return currentTarget != null && super.canUse();
                }

                @Override
                public boolean canContinueToUse() {
                    return currentTarget != null && super.canContinueToUse();
                }
            };
            maid.goalSelector.addGoal(1000, attackGoal);
        }
    }

    /**
     * 清除攻击目标
     */
    private void clearAttackGoal(EntityMaid maid) {
        if (attackGoal != null) {
            maid.goalSelector.removeGoal(attackGoal);
            attackGoal = null;
        }
    }

    /**
     * 处理重置状态
     */
    private void handleResetState(EntityMaid maid) {
        clearAttackGoal(maid);
        triggerState = TriggerState.INITIAL;
    }

    /**
     * 检查目标是否死亡
     */
    private boolean isTargetDead(@Nullable LivingEntity target) {
        return target != null && !target.isAlive();
    }

    /**
     * 处理罢工行为
     */
    private void handleStrikeBehavior(EntityMaid maid, MaidMadExtraData data) {
        int mood = data.getMood();
        if (mood >= 90) {
            // 计算罢工概率：心情90-120对应0%-1%的罢工概率
            float strikeChance = (Math.clamp(mood, 90, 120) - 90) / 30.0f / 50.0f;

            if (random.nextFloat() <= strikeChance) {
                maid.setTask(new TaskIdle());
            }
        }
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, @NotNull EntityMaid entity, long gameTime) {
        return entity instanceof MaidMadExtraData data &&
                data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal();
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected void stop(@NotNull ServerLevel level, @NotNull EntityMaid entity, long gameTime) {
        super.stop(level, entity, gameTime);
        clearAttackGoal(entity);
        entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        currentTarget = null;
        triggerState = TriggerState.INITIAL;
    }

    // 触发状态枚举
    private enum TriggerState {
        INITIAL,      // 初始状态
        ATTACKING,    // 攻击中
        RESET         // 重置状态
    }
}