package com.kruemblegard.entity.mount;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.core.animation.RawAnimation;

public class EchoHarnessEntity extends CephalariMountEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.echo_harness.idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.echo_harness.move");

    public EchoHarnessEntity(EntityType<? extends EchoHarnessEntity> type, Level level) {
        super(type, level,
            "assets/kruemblegard/geo/echo_harness.geo.json",
            new Vec3(0.0D, 0.95D, 0.0D)
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CephalariMountEntity.createBaseAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.28D);
    }

    @Override
    protected RawAnimation getIdleAnimation() {
        return IDLE;
    }

    @Override
    protected RawAnimation getMoveAnimation() {
        return MOVE;
    }
}
