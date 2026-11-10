package com.sleepingbag.logic;

import com.sleepingbag.SleepingBagMod;
import com.sleepingbag.block.SleepingBagBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public final class SleepLogic {
    public static final int NORMAL_TICKS = 200;
    public static final int QUICK_TICKS = 100;
    public static final ResourceKey<Enchantment> QUICK_NAP = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(SleepingBagMod.MOD_ID, "quick_nap"));

    private SleepLogic() {}

    public static boolean isNight(Level level) {
        long time = level.getDayTime() % 24000L;
        return time >= 12542L && time <= 23458L;
    }

    public static int requiredTicks(Level level, BlockPos pos) {
        if (pos == null) {
            return NORMAL_TICKS;
        }
        var state = level.getBlockState(pos);
        if (state.getBlock() instanceof SleepingBagBlock && state.getValue(SleepingBagBlock.QUICK)) {
            return QUICK_TICKS;
        }
        return NORMAL_TICKS;
    }

    public static boolean hasQuickNap(Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        RegistryAccess access = level.registryAccess();
        Registry<Enchantment> registry = access.registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> holder = registry.getHolder(QUICK_NAP).orElse(null);
        return holder != null && stack.getEnchantmentLevel(holder) > 0;
    }
}
