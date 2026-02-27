package com.gly091020.touhouLittleMad;

import com.gly091020.touhouLittleMad.config.ConfigScreenGetter;
import com.gly091020.touhouLittleMad.config.LittleMadConfig;
import com.gly091020.touhouLittleMad.util.AdvancementIconItem;
import com.gly091020.touhouLittleMad.util.GLYToolsRegistry;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import com.gly091020.touhouLittleMad.jade.AddJadeInfoHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(LittleMadMod.ModID)
public class LittleMadMod {
    public static boolean debug = !FMLEnvironment.production;

    public static final String ModID = "touhou_little_mad";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static LittleMadConfig CONFIG;

    public LittleMadMod(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        AutoConfig.register(LittleMadConfig.class, Toml4jConfigSerializer::new);
        MadMaidFunction.loadConfig();
        MadMaidFunction.registryBuiltInTask();
        AdvancementIconItem.registryBuiltInItem();
        AdvancementIconItem.registry(bus);
        GLYToolsRegistry.registry(bus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModList.get().getModContainerById(ModID).ifPresent(container ->
                    container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ConfigScreenGetter::getConfigScreen)));
            if (ModList.get().isLoaded("jade")) {
                MinecraftForge.EVENT_BUS.register(AddJadeInfoHandler.class);
            }
        }
        if (!FMLEnvironment.production) {
            MinecraftForge.EVENT_BUS.register(DebugEventHandler.class);
        }
    }
}
