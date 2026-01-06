package com.kruemblegard.network.packet;

import com.kruemblegard.client.ClientCodexState;

import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CodexStateSyncS2CPacket(boolean givenCrumblingCodex) {

    public static void encode(CodexStateSyncS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.givenCrumblingCodex);
    }

    public static CodexStateSyncS2CPacket decode(FriendlyByteBuf buf) {
        return new CodexStateSyncS2CPacket(buf.readBoolean());
    }

    public static void handle(CodexStateSyncS2CPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> ClientCodexState.setGivenCrumblingCodex(msg.givenCrumblingCodex));
        ctx.setPacketHandled(true);
    }
}
