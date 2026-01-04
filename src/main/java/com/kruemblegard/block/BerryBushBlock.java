package com.kruemblegard.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.registries.ForgeRegistries;

public class BerryBushBlock extends BushBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    private final ResourceLocation berryItemId;
    private final float damageOnTouch;

    public BerryBushBlock(Properties properties, String modId, String berryItemPath, float damageOnTouch) {
        super(properties);
        this.berryItemId = new ResourceLocation(modId, berryItemPath);
        this.damageOnTouch = damageOnTouch;
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void randomTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3 && random.nextInt(8) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        boolean mature = age >= 2;

        if (!mature) {
            return super.use(state, level, pos, player, hand, hit);
        }

        Item berryItem = ForgeRegistries.ITEMS.getValue(berryItemId);
        if (berryItem == null) {
            return InteractionResult.PASS;
        }

        int count = 1 + level.random.nextInt(age == 3 ? 2 : 1);
        popResource(level, pos, new ItemStack(berryItem, count));

        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F,
                0.8F + level.random.nextFloat() * 0.4F);

        level.setBlock(pos, state.setValue(AGE, 1), 2);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (damageOnTouch <= 0.0f) return;
        int age = state.getValue(AGE);
        if (age > 0) {
            entity.hurt(level.damageSources().sweetBerryBush(), damageOnTouch);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        Item berryItem = ForgeRegistries.ITEMS.getValue(berryItemId);
        return berryItem == null ? ItemStack.EMPTY : new ItemStack(berryItem);
    }
}
