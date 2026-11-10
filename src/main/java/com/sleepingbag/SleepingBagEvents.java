package com.sleepingbag;

import com.sleepingbag.block.SleepingBagBlock;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;

public final class SleepingBagEvents {
    private SleepingBagEvents() {}

    @SubscribeEvent
    public static void onSetSpawn(PlayerSetSpawnEvent event) {
        BlockPos pos = event.getNewSpawn();
        if (pos != null && event.getEntity().level().getBlockState(pos).getBlock() instanceof SleepingBagBlock) {
            event.setCanceled(true);
        }
    }
}
