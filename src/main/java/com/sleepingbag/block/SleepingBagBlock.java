package com.sleepingbag.block;

import com.sleepingbag.item.SleepingBagItem;
import com.sleepingbag.logic.SleepLogic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SleepingBagBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<SleepingBagBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            net.minecraft.world.item.DyeColor.CODEC.fieldOf("color").forGetter(SleepingBagBlock::color),
            propertiesCodec()
    ).apply(instance, SleepingBagBlock::new));
    public static final BooleanProperty QUICK = BooleanProperty.create("quick");

    private static final VoxelShape FOOT = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape HEAD = Block.box(0, 0, 0, 16, 4, 16);
    private static final ThreadLocal<Boolean> BUSY = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> CREATIVE_BREAK = ThreadLocal.withInitial(() -> false);

    private final net.minecraft.world.item.DyeColor color;

    public SleepingBagBlock(net.minecraft.world.item.DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BedBlock.PART, BedPart.FOOT)
                .setValue(BedBlock.OCCUPIED, false)
                .setValue(QUICK, false));
    }

    public net.minecraft.world.item.DyeColor color() {
        return color;
    }

    @Override
    public MapCodec<SleepingBagBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, LivingEntity sleeper) {
        return true;
    }

    @Override
    public void setBedOccupied(BlockState state, Level level, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        BlockPos head = headPos(pos, state);
        BlockState headState = level.getBlockState(head);
        if (headState.getBlock() instanceof SleepingBagBlock) {
            setOccupied(level, head, headState, occupied);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BedBlock.PART, BedBlock.OCCUPIED, QUICK);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos foot = context.getClickedPos();
        BlockPos head = foot.relative(facing);
        Level level = context.getLevel();
        if (!level.getBlockState(head).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(head)) {
            return null;
        }
        boolean quick = SleepLogic.hasQuickNap(level, context.getItemInHand());
        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(BedBlock.PART, BedPart.FOOT)
                .setValue(QUICK, quick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos head = pos.relative(state.getValue(FACING));
        level.setBlock(head, state.setValue(BedBlock.PART, BedPart.HEAD), Block.UPDATE_ALL);
        ItemStack source = SleepingBagItem.placingStack();
        if (source == null) {
            source = stack;
        }
        if (level.getBlockEntity(head) instanceof SleepingBagBlockEntity bag) {
            ItemStack stored = source.copy();
            stored.setCount(1);
            bag.setStack(stored);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(BedBlock.PART) == BedPart.HEAD ? new SleepingBagBlockEntity(pos, state) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
            pos = pos.relative(state.getValue(FACING));
            state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof SleepingBagBlock)) {
                return InteractionResult.CONSUME;
            }
        }
        if (state.getValue(BedBlock.OCCUPIED)) {
            player.displayClientMessage(Component.translatable("block.sleepingbag.occupied"), true);
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.CONSUME;
        }
        if (!level.dimensionType().bedWorks()) {
            player.displayClientMessage(Component.translatable("block.sleepingbag.not_here"), true);
            return InteractionResult.SUCCESS;
        }
        if (level.isThundering()) {
            player.displayClientMessage(Component.translatable("block.sleepingbag.thunderstorm"), true);
            return InteractionResult.SUCCESS;
        }
        if (!SleepLogic.isNight(level)) {
            player.displayClientMessage(Component.translatable("block.sleepingbag.not_night"), true);
            return InteractionResult.SUCCESS;
        }

        var result = serverPlayer.startSleepInBed(pos);
        if (result.left().isPresent()) {
            var problem = result.left().get();
            if (problem.getMessage() != null) {
                player.displayClientMessage(problem.getMessage(), true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    public static void setOccupied(Level level, BlockPos headPos, BlockState headState, boolean occupied) {
        level.setBlock(headPos, headState.setValue(BedBlock.OCCUPIED, occupied), Block.UPDATE_ALL);
        BlockPos foot = headPos.relative(headState.getValue(FACING).getOpposite());
        BlockState footState = level.getBlockState(foot);
        if (footState.getBlock() instanceof SleepingBagBlock) {
            level.setBlock(foot, footState.setValue(BedBlock.OCCUPIED, occupied), Block.UPDATE_ALL);
        }
    }

    public static void consumeUse(ServerPlayer player, BlockPos head) {
        if (!(player.level().getBlockEntity(head) instanceof SleepingBagBlockEntity bag)) {
            return;
        }
        ItemStack stack = bag.getStack();
        if (stack.isEmpty()) {
            return;
        }
        stack.hurtAndBreak(1, player.serverLevel(), player, broken -> {});
        if (stack.isEmpty()) {
            BlockState state = player.level().getBlockState(head);
            bag.setStack(ItemStack.EMPTY);
            BUSY.set(true);
            try {
                BlockPos foot = otherHalf(head, state);
                player.level().setBlock(head, Blocks.AIR.defaultBlockState(), 35);
                if (player.level().getBlockState(foot).getBlock() instanceof SleepingBagBlock) {
                    player.level().setBlock(foot, Blocks.AIR.defaultBlockState(), 35);
                }
            } finally {
                BUSY.set(false);
            }
        } else {
            bag.setChanged();
        }
    }

    public static void wakeAndClear(Level level, BlockPos headPos) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        for (ServerPlayer player : server.players()) {
            if (player.isSleeping() && headPos.equals(player.getSleepingPos().orElse(null))) {
                player.stopSleepInBed(true, true);
            }
        }
        BlockState state = level.getBlockState(headPos);
        if (state.getBlock() instanceof SleepingBagBlock && state.getValue(BedBlock.OCCUPIED)) {
            setOccupied(level, headPos, state, false);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.getAbilities().instabuild) {
            CREATIVE_BREAK.set(true);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide && !BUSY.get()) {
            BUSY.set(true);
            try {
                BlockPos head = headPos(pos, state);
                wakeAndClear(level, head);
                if (!CREATIVE_BREAK.get()) {
                    dropStored(level, head);
                }
                BlockPos other = otherHalf(pos, state);
                if (level.getBlockState(other).getBlock() instanceof SleepingBagBlock) {
                    level.setBlock(other, Blocks.AIR.defaultBlockState(), 35);
                }
            } finally {
                BUSY.set(false);
                CREATIVE_BREAK.set(false);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of();
    }

    private static void dropStored(Level level, BlockPos head) {
        if (level.getBlockEntity(head) instanceof SleepingBagBlockEntity bag && !bag.getStack().isEmpty()) {
            popResource(level, head, bag.getStack().copy());
            bag.setStack(ItemStack.EMPTY);
        }
    }

    public static BlockPos headPos(BlockPos pos, BlockState state) {
        if (state.getValue(BedBlock.PART) == BedPart.HEAD) {
            return pos;
        }
        return pos.relative(state.getValue(FACING));
    }

    public static BlockPos otherHalf(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        if (state.getValue(BedBlock.PART) == BedPart.FOOT) {
            return pos.relative(facing);
        }
        return pos.relative(facing.getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(BedBlock.PART) == BedPart.FOOT) {
            return FOOT;
        }
        return HEAD;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean isPathfindable(BlockState state, net.minecraft.world.level.pathfinder.PathComputationType type) {
        return true;
    }
}
