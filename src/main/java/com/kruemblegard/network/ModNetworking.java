package com.kruemblegard.network;

import com.kruemblegard.Kruemblegard;
import com.kruemblegard.network.packet.CodexStateSyncS2CPacket;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private ModNetworking() {}

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Kruemblegard.MOD_ID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(CodexStateSyncS2CPacket.class, id++)
                .encoder(CodexStateSyncS2CPacket::encode)
                .decoder(CodexStateSyncS2CPacket::decode)
                .consumerMainThread(CodexStateSyncS2CPacket::handle)
                .add();
    }
}
