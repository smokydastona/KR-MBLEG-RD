package com.kruemblegard.network;

import com.kruemblegard.Kruemblegard;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Kruemblegard.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean initialized = false;

    private ModNetworking() {}

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        int id = 0;
        CHANNEL.messageBuilder(ScaralonFlightInputC2SPacket.class, id++)
                .encoder(ScaralonFlightInputC2SPacket::encode)
                .decoder(ScaralonFlightInputC2SPacket::decode)
                .consumerMainThread(ScaralonFlightInputC2SPacket::handle)
                .add();
    }
}
