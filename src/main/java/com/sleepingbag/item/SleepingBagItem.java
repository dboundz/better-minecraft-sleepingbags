package com.sleepingbag.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class SleepingBagItem extends BlockItem {
    private static final ThreadLocal<net.minecraft.world.item.ItemStack> PLACING = new ThreadLocal<>();
    private final DyeColor color;

    public SleepingBagItem(Block block, Properties properties, DyeColor color) {
        super(block, properties);
        this.color = color;
    }

    public DyeColor color() {
        return color;
    }

    public static net.minecraft.world.item.ItemStack placingStack() {
        return PLACING.get();
    }

    @Override
    public net.minecraft.world.InteractionResult place(BlockPlaceContext context) {
        PLACING.set(context.getItemInHand().copy());
        try {
            return super.place(context);
        } finally {
            PLACING.remove();
        }
    }
}
