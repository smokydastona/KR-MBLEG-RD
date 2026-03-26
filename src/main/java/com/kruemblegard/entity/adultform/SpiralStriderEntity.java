package com.kruemblegard.entity.adultform;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

import software.bernie.geckolib.core.animation.RawAnimation;

public class SpiralStriderEntity extends CephalariAdultFormEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.spiral_strider.idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.spiral_strider.move");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("animation.spiral_strider.sleep");
    private static final RawAnimation MANIFEST = RawAnimation.begin().thenPlay("animation.spiral_strider.zombie_manifest");

    public SpiralStriderEntity(EntityType<? extends SpiralStriderEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CephalariAdultFormEntity.createBaseAttributes();
    }

    @Override
    protected RawAnimation getIdleAnimation() {
        return IDLE;
    }

    @Override
    protected RawAnimation getMoveAnimation() {
        return MOVE;
    }

    @Override
    protected RawAnimation getSleepAnimation() {
        return SLEEP;
    }

    @Override
    protected RawAnimation getManifestAnimation() {
        return MANIFEST;
    }
}
