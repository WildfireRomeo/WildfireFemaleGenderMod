package com.wildfire.main.networking;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncToServerPacket extends SyncPacket {

    private static final Identifier IDENTIFIER = new Identifier(WildfireGender.MODID, "send_gender_info");
    public static final PacketType<SyncToServerPacket> PACKET_TYPE = PacketType.create(IDENTIFIER, SyncToServerPacket::new);

    protected SyncToServerPacket(PlayerConfig plr) {
        super(plr);
    }

    private SyncToServerPacket(PacketByteBuf buffer) {
        super(buffer);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public void handle(ServerPlayerEntity player) {
        if (player.getUuid().equals(uuid)) {
            PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
            updatePlayerFromPacket(plr);
            WildfireSync.sendToAllClients(player, plr);
        }
    }
}