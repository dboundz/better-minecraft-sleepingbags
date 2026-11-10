package com.sleepingbag.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sleepingbag.SleepingBagMod;
import com.sleepingbag.block.SleepingBagBlock;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;

public class SleepingBagCoverLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation QUILT = ResourceLocation.fromNamespaceAndPath(SleepingBagMod.MOD_ID, "textures/block/quilt.png");
    private final ModelPart blanket;

    public SleepingBagCoverLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("blanket",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -3.15F, 10.0F, 20.0F, 1.0F),
                PartPose.ZERO);
        this.blanket = LayerDefinition.create(mesh, 16, 16).bakeRoot();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float age, float yaw, float pitch) {
        if (!player.isSleeping()) {
            return;
        }
        var pos = player.getSleepingPos().orElse(null);
        if (pos == null) {
            return;
        }
        var state = player.level().getBlockState(pos);
        if (!(state.getBlock() instanceof SleepingBagBlock bag)) {
            return;
        }
        int color = 0xFF000000 | bag.color().getTextureDiffuseColor();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(QUILT));
        pose.pushPose();
        this.getParentModel().body.translateAndRotate(pose);
        this.blanket.render(pose, consumer, light, OverlayTexture.NO_OVERLAY, color);
        pose.popPose();
    }
}
