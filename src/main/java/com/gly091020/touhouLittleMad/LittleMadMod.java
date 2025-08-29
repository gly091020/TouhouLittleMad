package com.gly091020.touhouLittleMad;

import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(LittleMadMod.ModID)
public class LittleMadMod {
    public static boolean debug = true;

    public static final String ModID = "touhou_little_mad";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LittleMadMod(){
        MadMaidFunction.registryBuiltInTask();
    }
}
