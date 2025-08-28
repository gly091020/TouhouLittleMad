package com.gly091020.touhouLittleMad.util;

import com.gly091020.touhouLittleMad.MoodLevelType;

public interface MaidMadExtraData {
    int getMood();
    void setMood(int mood);
    void setHandledMood(int mood);
    MoodLevelType getMoodLevel();

    MaidCooldown getCooldown();
}
