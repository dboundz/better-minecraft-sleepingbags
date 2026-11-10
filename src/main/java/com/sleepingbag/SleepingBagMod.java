package com.sleepingbag;

import com.sleepingbag.block.SleepingBagBlockEntities;
import com.sleepingbag.block.SleepingBagBlocks;
import com.sleepingbag.recipe.SleepingBagRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(SleepingBagMod.MOD_ID)
public class SleepingBagMod {
    public static final String MOD_ID = "sleepingbag";

    public SleepingBagMod(IEventBus modBus) {
        SleepingBagBlocks.BLOCKS.register(modBus);
        SleepingBagBlocks.ITEMS.register(modBus);
        SleepingBagBlockEntities.BLOCK_ENTITIES.register(modBus);
        SleepingBagRecipes.SERIALIZERS.register(modBus);
        SleepingBagTabs.TABS.register(modBus);
        NeoForge.EVENT_BUS.addListener(SleepingBagEvents::onSetSpawn);
    }
}
