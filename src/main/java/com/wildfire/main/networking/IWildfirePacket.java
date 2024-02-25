package com.wildfire.main.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IWildfirePacket<CONTEXT extends IPayloadContext> extends CustomPacketPayload {

    void handle(CONTEXT context);

    default void handleMainThread(CONTEXT context) {
        context.workHandler().execute(() -> handle(context));
    }
}