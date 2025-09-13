package com.gly091020.touhouLittleMad;

import com.github.tartaricacid.touhoulittlemaid.api.event.client.RenderMaidEvent;
import com.gly091020.touhouLittleMad.util.MaidMadExtraData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = LittleMadMod.ModID, value = Dist.CLIENT)
public class EventHandlerClient {
    @SubscribeEvent
    public static void onRender(RenderMaidEvent event){
        // 生气粒子
        if(Minecraft.getInstance().isPaused()){return;}
        if(event.getMaid() instanceof MaidMadExtraData data &&
                data.getMoodLevel().ordinal() >= MoodLevelType.BAD.ordinal()){
            var maid = event.getMaid().asEntity();
            if(maid.level().getGameTime() % 10 != 0){return;}
            double d0 = maid.getRandom().nextGaussian() * 0.02;
            double d1 = maid.getRandom().nextGaussian() * 0.02;
            double d2 = maid.getRandom().nextGaussian() * 0.02;
            maid.level().addParticle(ParticleTypes.ANGRY_VILLAGER, maid.getRandomX(1.0F), maid.getRandomY() + (double)0.8F, maid.getRandomZ(1.0F), d0, d1, d2);
        }
    }
}
