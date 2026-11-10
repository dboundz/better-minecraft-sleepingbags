package com.sleepingbag.client;

import com.sleepingbag.SleepingBagMod;
import com.sleepingbag.block.SleepingBagBlock;
import com.sleepingbag.block.SleepingBagBlocks;
import com.sleepingbag.item.SleepingBagItem;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = SleepingBagMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SleepingBagClient {
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new SleepingBagCoverLayer(renderer));
            }
        }
    }

    @SubscribeEvent
    public static void blockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tint) -> {
            if (state.getBlock() instanceof SleepingBagBlock bag) {
                return bag.color().getTextureDiffuseColor();
            }
            return -1;
        }, SleepingBagBlocks.blockArray());
    }

    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.Item event) {
        for (var entry : SleepingBagBlocks.all()) {
            event.register((stack, tint) -> {
                if (stack.getItem() instanceof SleepingBagItem item) {
                    return item.color().getTextureDiffuseColor();
                }
                return -1;
            }, entry.item().get());
        }
    }

    private SleepingBagClient() {}
}
