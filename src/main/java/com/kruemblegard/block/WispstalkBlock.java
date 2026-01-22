package com.kruemblegard.block;

import com.kruemblegard.registry.ModTags;
import com.kruemblegard.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class WispstalkBlock extends BushBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public WispstalkBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(ModTags.Blocks.WAYFALL_GROUND);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        boolean mature = age >= 2;

        if (!mature) {
            return InteractionResult.PASS;
        }

        int count = 1 + level.random.nextInt(age == 3 ? 2 : 1);
        popResource(level, pos, new ItemStack(ModItems.WISPSHOOT.get(), count));

        level.playSound(
            null,
            pos,
            SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
            SoundSource.BLOCKS,
            1.0F,
            0.9F + level.random.nextFloat() * 0.2F
        );

        level.setBlock(pos, state.setValue(AGE, 1), 2);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age >= 3) return;

        boolean nearEnchanted = random.nextInt(4) == 0 && isNearEnchantedBlocksSampled(level, pos, random);
        boolean moonlit = level.dimensionType().hasSkyLight()
                && level.isNight()
                && level.getMoonBrightness() > 0.6f;

        if ((nearEnchanted || moonlit) && random.nextInt(6) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) != 0) return;

        level.addParticle(
                ParticleTypes.END_ROD,
                pos.getX() + 0.2 + random.nextDouble() * 0.6,
                pos.getY() + 0.4 + random.nextDouble() * 0.6,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                0.0,
                0.01,
                0.0
        );
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        int age = state.getValue(AGE);
        if (age <= 0) return;

        entity.makeStuckInBlock(state, new Vec3(0.8D, 0.75D, 0.8D));
        if (level.isClientSide) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (living.isSteppingCarefully()) return;

        double dx = Math.abs(entity.getX() - entity.xOld);
        double dz = Math.abs(entity.getZ() - entity.zOld);
        if (dx >= 0.003D || dz >= 0.003D) {
            entity.hurt(level.damageSources().sweetBerryBush(), 1.0F);
        }
    }

    private static boolean isNearEnchantedBlocksSampled(Level level, BlockPos origin, RandomSource random) {
        // Full cube scans are extremely expensive when many plants are loaded.
        // Sampling gives us the “near enchanted” vibe without the 9^3 block checks.
        int radius = 4;
        int samples = 12;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i = 0; i < samples; i++) {
            cursor.set(
                    origin.getX() + Mth.nextInt(random, -radius, radius),
                    origin.getY() + Mth.nextInt(random, -radius, radius),
                    origin.getZ() + Mth.nextInt(random, -radius, radius)
            );

            BlockState st = level.getBlockState(cursor);
            if (st.is(net.minecraft.world.level.block.Blocks.ENCHANTING_TABLE)
                    || st.is(net.minecraft.world.level.block.Blocks.CHISELED_BOOKSHELF)
                    || st.is(net.minecraft.world.level.block.Blocks.LECTERN)
                    || st.is(net.minecraft.world.level.block.Blocks.BOOKSHELF)) {
                return true;
            }
        }

        return false;
    }
}
