package com.sleepingbag.mixin;

import com.sleepingbag.block.SleepingBagBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "startSleeping", at = @At("TAIL"))
    private void sleepingbag$lowerOntoGround(BlockPos pos, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().getBlockState(pos).getBlock() instanceof SleepingBagBlock) {
            self.setPos(pos.getX() + 0.5D, pos.getY() + 0.15D, pos.getZ() + 0.5D);
        }
    }
}
