package com.sleepingbag.block;

import com.sleepingbag.SleepingBagMod;
import com.sleepingbag.item.SleepingBagItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public final class SleepingBagBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SleepingBagMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SleepingBagMod.MOD_ID);
    private static final List<Entry> ALL = new ArrayList<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            ALL.add(register(color));
        }
    }

    private SleepingBagBlocks() {}

    public static List<Entry> all() {
        return ALL;
    }

    public static Entry byColor(DyeColor color) {
        return ALL.get(color.getId());
    }

    public static Block[] blockArray() {
        return ALL.stream().map(entry -> (Block) entry.block().get()).toArray(Block[]::new);
    }

    private static Entry register(DyeColor color) {
        String name = color.getName() + "_sleeping_bag";
        DeferredBlock<SleepingBagBlock> block = BLOCKS.register(name, () -> new SleepingBagBlock(color,
                BlockBehaviour.Properties.of()
                        .mapColor(color.getMapColor())
                        .sound(SoundType.WOOL)
                        .strength(0.2F)
                        .noOcclusion()));
        DeferredItem<SleepingBagItem> item = ITEMS.register(name, () -> new SleepingBagItem(block.get(), new Item.Properties().durability(10), color));
        return new Entry(color, block, item);
    }

    public record Entry(DyeColor color, DeferredBlock<SleepingBagBlock> block, DeferredItem<SleepingBagItem> item) {}
}
