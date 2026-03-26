package com.kruemblegard.entity.adultform;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

import software.bernie.geckolib.core.animation.RawAnimation;

public class DriftSkimmerEntity extends CephalariAdultFormEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.driftskimmer.idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.driftskimmer.move");
    private static final RawAnimation MANIFEST = RawAnimation.begin().thenPlay("animation.driftskimmer.zombie_manifest");

    public DriftSkimmerEntity(EntityType<? extends DriftSkimmerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CephalariAdultFormEntity.createBaseAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.30D);
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
    protected RawAnimation getManifestAnimation() {
        return MANIFEST;
    }
}
