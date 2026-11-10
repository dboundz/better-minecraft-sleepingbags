package com.sleepingbag.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SleepingBagBlockEntity extends BlockEntity {
    private ItemStack stack = ItemStack.EMPTY;

    public SleepingBagBlockEntity(BlockPos pos, BlockState state) {
        super(SleepingBagBlockEntities.SLEEPING_BAG.get(), pos, state);
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!stack.isEmpty()) {
            tag.put("Item", stack.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Item")) {
            stack = ItemStack.parse(registries, tag.getCompound("Item")).orElse(ItemStack.EMPTY);
        }
    }
}
