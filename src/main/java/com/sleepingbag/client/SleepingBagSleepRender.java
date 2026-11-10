package com.sleepingbag.client;

import com.sleepingbag.SleepingBagMod;
import com.sleepingbag.block.SleepingBagBlock;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber(modid = SleepingBagMod.MOD_ID, value = Dist.CLIENT)
public class SleepingBagSleepRender {
    private static boolean hidden;
    private static boolean body;
    private static boolean leftArm;
    private static boolean rightArm;
    private static boolean leftLeg;
    private static boolean rightLeg;
    private static boolean jacket;
    private static boolean leftSleeve;
    private static boolean rightSleeve;
    private static boolean leftPants;
    private static boolean rightPants;

    @SubscribeEvent
    public static void beforeRender(RenderLivingEvent.Pre<?, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }
        if (!(event.getRenderer().getModel() instanceof PlayerModel<?> model)) {
            return;
        }
        var pos = player.getSleepingPos().orElse(null);
        if (!player.isSleeping() || pos == null || !(player.level().getBlockState(pos).getBlock() instanceof SleepingBagBlock)) {
            return;
        }
        body = model.body.visible;
        leftArm = model.leftArm.visible;
        rightArm = model.rightArm.visible;
        leftLeg = model.leftLeg.visible;
        rightLeg = model.rightLeg.visible;
        jacket = model.jacket.visible;
        leftSleeve = model.leftSleeve.visible;
        rightSleeve = model.rightSleeve.visible;
        leftPants = model.leftPants.visible;
        rightPants = model.rightPants.visible;
        model.body.visible = false;
        model.leftArm.visible = false;
        model.rightArm.visible = false;
        model.leftLeg.visible = false;
        model.rightLeg.visible = false;
        model.jacket.visible = false;
        model.leftSleeve.visible = false;
        model.rightSleeve.visible = false;
        model.leftPants.visible = false;
        model.rightPants.visible = false;
        hidden = true;
    }

    @SubscribeEvent
    public static void afterRender(RenderLivingEvent.Post<?, ?> event) {
        if (!hidden || !(event.getRenderer().getModel() instanceof PlayerModel<?> model)) {
            hidden = false;
            return;
        }
        model.body.visible = body;
        model.leftArm.visible = leftArm;
        model.rightArm.visible = rightArm;
        model.leftLeg.visible = leftLeg;
        model.rightLeg.visible = rightLeg;
        model.jacket.visible = jacket;
        model.leftSleeve.visible = leftSleeve;
        model.rightSleeve.visible = rightSleeve;
        model.leftPants.visible = leftPants;
        model.rightPants.visible = rightPants;
        hidden = false;
    }

    private SleepingBagSleepRender() {}
}
