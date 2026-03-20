package com.kruemblegard.entity.adultform;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.core.animation.RawAnimation;

public class TreadwinderEntity extends CephalariAdultFormEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.treadwinder.idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.treadwinder.move");
    private static final RawAnimation MANIFEST = RawAnimation.begin().thenPlay("animation.treadwinder.zombie_manifest");

    public TreadwinderEntity(EntityType<? extends TreadwinderEntity> type, Level level) {
        super(type, level,
            "assets/kruemblegard/geo/treadwinder.geo.json",
            new Vec3(0.0D, 1.00D, 0.0D)
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CephalariAdultFormEntity.createBaseAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 30.0D);
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
