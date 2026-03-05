package com.kruemblegard.entity.mount;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.core.animation.RawAnimation;

public class DriftSkimmerEntity extends CephalariMountEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.driftskimmer.idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.driftskimmer.move");
    private static final RawAnimation MANIFEST = RawAnimation.begin().thenPlay("animation.driftskimmer.zombie_manifest");

    public DriftSkimmerEntity(EntityType<? extends DriftSkimmerEntity> type, Level level) {
        super(type, level,
            "assets/kruemblegard/geo/driftskimmer.geo.json",
            new Vec3(0.0D, 1.05D, 0.0D)
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CephalariMountEntity.createBaseAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.30D);
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
