package com.gly091020.touhouLittleMad;

import com.gly091020.touhouLittleMad.config.ConfigScreenGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = LittleMadMod.ModID, dist = Dist.CLIENT)
public class LittleMadClient {
    public LittleMadClient(ModContainer container){
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigScreenGetter::getConfigScreen);
    }
}
