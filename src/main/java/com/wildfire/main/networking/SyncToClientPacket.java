package com.wildfire.main.networking;

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SyncToClientPacket extends SyncPacket {

    private static final Identifier IDENTIFIER = new Identifier(WildfireGender.MODID, "sync");
    public static final PacketType<SyncToClientPacket> PACKET_TYPE = PacketType.create(IDENTIFIER, SyncToClientPacket::new);

    protected SyncToClientPacket(PlayerConfig plr) {
        super(plr);
    }

    private SyncToClientPacket(PacketByteBuf buffer) {
        super(buffer);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public void handle(PlayerEntity player) {
        if (!player.getUuid().equals(uuid)) {
            PlayerConfig plr = WildfireGender.getOrAddPlayerById(uuid);
            updatePlayerFromPacket(plr);
            plr.syncStatus = PlayerConfig.SyncStatus.SYNCED;
        }
    }
}