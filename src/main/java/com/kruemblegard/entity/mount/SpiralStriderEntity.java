package com.kruemblegard.entity.mount;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.core.animation.RawAnimation;

public class SpiralStriderEntity extends CephalariMountEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.spiral_strider.idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.spiral_strider.move");

    public SpiralStriderEntity(EntityType<? extends SpiralStriderEntity> type, Level level) {
        super(type, level,
            "assets/kruemblegard/geo/spiral_strider.geo.json",
            new Vec3(0.0D, 1.15D, 0.0D)
        );
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CephalariMountEntity.createBaseAttributes();
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
