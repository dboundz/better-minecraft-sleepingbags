package com.sleepingbag.recipe;

import com.sleepingbag.SleepingBagMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SleepingBagRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, SleepingBagMod.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SleepingBagDyeRecipe>> DYE =
            SERIALIZERS.register("sleeping_bag_dye", () -> new SimpleCraftingRecipeSerializer<>(SleepingBagDyeRecipe::new));

    private SleepingBagRecipes() {}
}
