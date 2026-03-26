package com.kruemblegard.entity.adultform;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

import software.bernie.geckolib.core.animation.RawAnimation;

public class EchoHarnessEntity extends CephalariAdultFormEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.echo_harness.idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.echo_harness.move");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("animation.echo_harness.sleep");
    private static final RawAnimation MANIFEST = RawAnimation.begin().thenPlay("animation.echo_harness.zombie_manifest");

    public EchoHarnessEntity(EntityType<? extends EchoHarnessEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CephalariAdultFormEntity.createBaseAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.28D);
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
