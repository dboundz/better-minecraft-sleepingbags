package com.sleepingbag;

import com.sleepingbag.block.SleepingBagBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SleepingBagTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SleepingBagMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("sleeping_bag",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sleepingbag"))
                    .icon(() -> SleepingBagBlocks.byColor(DyeColor.RED).item().get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        for (var entry : SleepingBagBlocks.all()) {
                            output.accept(entry.item().get());
                        }
                    })
                    .build());

    private SleepingBagTabs() {}
}
