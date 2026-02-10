package com.kruemblegard.network;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.entity.ScaralonBeetleEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ScaralonFlightInputC2SPacket(int entityId, boolean ascendHeld, boolean descendHeld) {

    public static void encode(ScaralonFlightInputC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeBoolean(msg.ascendHeld);
        buf.writeBoolean(msg.descendHeld);
    }

    public static ScaralonFlightInputC2SPacket decode(FriendlyByteBuf buf) {
        return new ScaralonFlightInputC2SPacket(
                buf.readVarInt(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(ScaralonFlightInputC2SPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer sender = ctx.getSender();
        if (sender == null) {
            ctx.setPacketHandled(true);
            return;
        }

        ctx.enqueueWork(() -> {
            Entity e = sender.level().getEntity(msg.entityId);
            if (!(e instanceof ScaralonBeetleEntity scaralon)) {
                return;
            }

            if (scaralon.getControllingPassenger() != sender) {
                return;
            }

            scaralon.setFlightInputs(msg.ascendHeld, msg.descendHeld);

            Kruemblegard.LOGGER.info(
                    "Scaralon flight input: player={} entityId={} ascend={} descend={} flying={} onGround={} wet={} dy={} stamina={}/{}",
                    sender.getGameProfile().getName(),
                    msg.entityId,
                    msg.ascendHeld,
                    msg.descendHeld,
                    scaralon.isFlying(),
                    scaralon.onGround(),
                    scaralon.isInWaterOrBubble(),
                    String.format("%.3f", scaralon.getDeltaMovement().y),
                    scaralon.getFlightStaminaTicks(),
                    scaralon.getMaxFlightStaminaTicks()
            );
        });

        ctx.setPacketHandled(true);
    }
}
