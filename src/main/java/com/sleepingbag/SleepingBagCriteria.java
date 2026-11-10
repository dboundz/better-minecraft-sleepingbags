package com.sleepingbag;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SleepingBagCriteria {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, SleepingBagMod.MOD_ID);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> SLEPT_IN_SLEEPING_BAG =
            TRIGGERS.register("slept_in_sleeping_bag", PlayerTrigger::new);

    private SleepingBagCriteria() {}
}
