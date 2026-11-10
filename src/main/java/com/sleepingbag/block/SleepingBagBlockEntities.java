package com.sleepingbag.block;

import com.sleepingbag.SleepingBagMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SleepingBagBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SleepingBagMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SleepingBagBlockEntity>> SLEEPING_BAG =
            BLOCK_ENTITIES.register("sleeping_bag", () -> BlockEntityType.Builder
                    .of(SleepingBagBlockEntity::new, SleepingBagBlocks.blockArray())
                    .build(null));

    private SleepingBagBlockEntities() {}
}
