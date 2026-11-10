package com.sleepingbag.client;

import com.sleepingbag.block.SleepingBagBlock;
import net.minecraft.world.entity.player.Player;

public final class SleepingBagSleepRender {
    public static boolean inBag(Player player) {
        var pos = player.getSleepingPos().orElse(null);
        return player.isSleeping()
                && pos != null
                && player.level().getBlockState(pos).getBlock() instanceof SleepingBagBlock;
    }

    private SleepingBagSleepRender() {}
}
