package com.gly091020.touhouLittleMad.util;

import com.gly091020.touhouLittleMad.LittleMadMod;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MaidCooldown {
    private final Map<String, Integer> timers = new HashMap<>();

    private static final String KEY = "MadCooldown";

    public void saveToNbt(CompoundTag compoundTag){
        var tag1 = new CompoundTag();
        timers.forEach(tag1::putInt);
        compoundTag.put(KEY, tag1);
    }

    public static MaidCooldown readFromNbt(CompoundTag compoundTag){
        var tag1 = compoundTag.getCompound(KEY);
        var cooldown = new MaidCooldown();
        tag1.getAllKeys().forEach(k -> cooldown.setTimer(k, tag1.getInt(k)));
        return cooldown;
    }

    public void setTimer(String name, Integer value){
        timers.put(name, value);
    }

    public void addTimer(String name, Integer value){
        setTimer(name, getTimer(name) + value);
    }

    public void tick(){
        if(LittleMadMod.debug){
            timers.forEach((k, v) -> {
                if(v % 10 == 0){
                    LittleMadMod.LOGGER.debug("timer {}:{}", k, v);
                }
            });
        }

        var delete = new ArrayList<String>();
        timers.forEach((k, v) -> {
            timers.put(k, timers.get(k) - 1);
            if(v - 1 <= 0){
                delete.add(k);
            }
        });
        delete.forEach(timers::remove);
    }

    public int getTimer(String key){
        return timers.getOrDefault(key, 0);
    }

    public boolean notInCooldown(String key){
        return getTimer(key) == 0;
    }
}
