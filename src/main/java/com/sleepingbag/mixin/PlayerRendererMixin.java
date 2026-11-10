package com.sleepingbag.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sleepingbag.client.SleepingBagSleepRender;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    private boolean sleepingbag$hidden;

    @Shadow
    private void setModelProperties(AbstractClientPlayer player) {}

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setModelProperties(Lnet/minecraft/client/player/AbstractClientPlayer;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void sleepingbag$hideBody(AbstractClientPlayer player, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!SleepingBagSleepRender.inBag(player)) {
            return;
        }
        PlayerModel<?> model = ((PlayerRenderer) (Object) this).getModel();
        model.setAllVisible(false);
        model.head.visible = true;
        model.hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
        this.sleepingbag$hidden = true;
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void sleepingbag$restore(AbstractClientPlayer player, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!this.sleepingbag$hidden) {
            return;
        }
        this.sleepingbag$hidden = false;
        this.setModelProperties(player);
    }
}
