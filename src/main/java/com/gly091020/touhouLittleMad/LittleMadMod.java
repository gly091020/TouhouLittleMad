package com.gly091020.touhouLittleMad;

import com.gly091020.touhouLittleMad.config.LittleMadConfig;
import com.gly091020.touhouLittleMad.util.AdvancementIconItem;
import com.gly091020.touhouLittleMad.util.GLYToolsRegistry;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(LittleMadMod.ModID)
public class LittleMadMod {
    public static boolean debug = true;

    public static final String ModID = "touhou_little_mad";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static LittleMadConfig CONFIG;

    public LittleMadMod(IEventBus bus){
        AutoConfig.register(LittleMadConfig.class, Toml4jConfigSerializer::new);
        MadMaidFunction.loadConfig();
        MadMaidFunction.registryBuiltInTask();
        AdvancementIconItem.registryBuiltInItem();
        AdvancementIconItem.registry(bus);
        GLYToolsRegistry.registry(bus);
    }
}
