package com.sleepingbag.recipe;

import com.sleepingbag.block.SleepingBagBlocks;
import com.sleepingbag.item.SleepingBagItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SleepingBagDyeRecipe extends CustomRecipe {
    public SleepingBagDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !assemble(input, level.registryAccess()).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack bag = ItemStack.EMPTY;
        DyeColor color = null;
        int bags = 0;
        int dyes = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof SleepingBagItem) {
                bags++;
                bag = stack;
            } else {
                DyeColor found = DyeColor.getColor(stack);
                if (found == null) {
                    return ItemStack.EMPTY;
                }
                dyes++;
                color = found;
            }
        }
        if (bags != 1 || dyes != 1 || color == null || !(bag.getItem() instanceof SleepingBagItem bagItem)) {
            return ItemStack.EMPTY;
        }
        if (bagItem.color() == color) {
            return ItemStack.EMPTY;
        }
        ItemStack result = new ItemStack(SleepingBagBlocks.byColor(color).item().get());
        result.applyComponents(bag.getComponents());
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SleepingBagRecipes.DYE.get();
    }
}
