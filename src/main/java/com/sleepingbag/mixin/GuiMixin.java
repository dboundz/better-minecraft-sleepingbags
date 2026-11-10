package com.sleepingbag.mixin;

import com.sleepingbag.block.SleepingBagBlock;
import com.sleepingbag.logic.SleepLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyConstant(method = "renderSleepOverlay", constant = @Constant(floatValue = 100.0F))
    private float sleepingbag$fade(float original) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return original;
        }
        BlockPos pos = player.getSleepingPos().orElse(null);
        if (pos == null || !(player.level().getBlockState(pos).getBlock() instanceof SleepingBagBlock)) {
            return original;
        }
        return SleepLogic.requiredTicks(player.level(), pos);
    }
}
