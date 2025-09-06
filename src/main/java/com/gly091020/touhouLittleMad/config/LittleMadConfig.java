package com.gly091020.touhouLittleMad.config;

import com.gly091020.touhouLittleMad.LittleMadMod;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = LittleMadMod.ModID)
public class LittleMadConfig implements ConfigData {
    public boolean enableAttack = true;
    public boolean enableGift = true;
    public String taskMood = "{}";
}
