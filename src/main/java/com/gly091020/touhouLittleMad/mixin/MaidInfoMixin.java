package com.gly091020.touhouLittleMad.mixin;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.gly091020.touhouLittleMad.LittleMadMod;
import com.gly091020.touhouLittleMad.util.MadMaidFunction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(AbstractMaidContainerGui.class)
public abstract class MaidInfoMixin {
    @Shadow
    @Final
    protected EntityMaid maid;

    @Redirect(method = "renderMaidInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V"))
    public void addData(GuiGraphics instance, Font font, List<Component> list, int x, int y){
        if(LittleMadMod.debug){
            MadMaidFunction.getDebugTooltip(this.maid, list);
        }
        instance.renderComponentTooltip(font, list, x, y);
    }
}
