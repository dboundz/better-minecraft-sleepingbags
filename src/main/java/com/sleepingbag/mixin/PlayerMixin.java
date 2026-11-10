package com.sleepingbag.mixin;

import com.sleepingbag.block.SleepingBagBlock;
import com.sleepingbag.logic.SleepLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    private int sleepCounter;

    private BlockPos sleepingbag$pos;
    private boolean sleepingbag$completed;
    private boolean sleepingbag$handling;

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 100))
    private int sleepingbag$sleepCap(int original) {
        return sleepingbag$ticks(original);
    }

    @ModifyConstant(method = "isSleepingLongEnough", constant = @Constant(intValue = 100))
    private int sleepingbag$longEnough(int original) {
        return sleepingbag$ticks(original);
    }

    private int sleepingbag$ticks(int original) {
        Player self = (Player) (Object) this;
        BlockPos pos = self.getSleepingPos().orElse(null);
        if (pos == null || !(self.level().getBlockState(pos).getBlock() instanceof SleepingBagBlock)) {
            return original;
        }
        return SleepLogic.requiredTicks(self.level(), pos);
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void sleepingbag$captureSleep(boolean wakeImmediately, boolean updateLevel, CallbackInfo ci) {
        if (this.sleepingbag$handling) {
            return;
        }
        Player self = (Player) (Object) this;
        this.sleepingbag$pos = null;
        this.sleepingbag$completed = false;
        BlockPos pos = self.getSleepingPos().orElse(null);
        if (pos == null || self.level().isClientSide) {
            return;
        }
        if (!(self.level().getBlockState(pos).getBlock() instanceof SleepingBagBlock)) {
            return;
        }
        this.sleepingbag$pos = pos;
        this.sleepingbag$completed = this.sleepCounter >= SleepLogic.requiredTicks(self.level(), pos);
    }

    @Inject(method = "stopSleepInBed", at = @At("TAIL"))
    private void sleepingbag$afterSleep(boolean wakeImmediately, boolean updateLevel, CallbackInfo ci) {
        if (this.sleepingbag$handling || this.sleepingbag$pos == null) {
            return;
        }
        this.sleepingbag$handling = true;
        try {
            Player self = (Player) (Object) this;
            BlockPos pos = this.sleepingbag$pos;
            BlockState state = self.level().getBlockState(pos);
            if (state.getBlock() instanceof SleepingBagBlock && state.getValue(BedBlock.OCCUPIED)) {
                SleepingBagBlock.setOccupied(self.level(), pos, state, false);
            }
            if (this.sleepingbag$completed && self instanceof ServerPlayer serverPlayer) {
                SleepingBagBlock.consumeUse(serverPlayer, pos);
            }
        } finally {
            this.sleepingbag$handling = false;
            this.sleepingbag$pos = null;
        }
    }
}
