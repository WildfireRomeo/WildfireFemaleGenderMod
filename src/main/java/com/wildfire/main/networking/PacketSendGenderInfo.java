package com.wildfire.main.networking;

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketSendGenderInfo {
    private final String uuid;
    private final int gender;
    private final float bust_size;

    //physics variables
    private final boolean breast_physics;
    private final boolean breast_physics_armor;
    private final boolean show_in_armor;
    private final float bounceMultiplier;
    private final float floppyMultiplier;

    private final float xOffset, yOffset, zOffset;
    private final boolean uniboob;
    private final float cleavage;

    private final boolean hurtSounds;

    public PacketSendGenderInfo(GenderPlayer plr) {
        this.uuid = plr.username;
        this.gender = plr.gender;
        this.bust_size = plr.getBustSize();
        this.hurtSounds = plr.hurtSounds;

        //physics variables
        this.breast_physics = plr.breast_physics;
        this.breast_physics_armor = plr.breast_physics_armor;
        this.show_in_armor = plr.show_in_armor;
        this.bounceMultiplier = plr.bounceMultiplier;
        this.floppyMultiplier = plr.floppyMultiplier;

        this.xOffset = plr.getBreasts().xOffset;
        this.yOffset = plr.getBreasts().yOffset;
        this.zOffset = plr.getBreasts().zOffset;

        this.uniboob = plr.getBreasts().isUniboob;
        this.cleavage = plr.getBreasts().cleavage;
    }

    public PacketSendGenderInfo(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUtf(36);
        this.gender = buffer.readInt();
        this.bust_size = buffer.readFloat();
        this.hurtSounds = buffer.readBoolean();

        //physics variables
        this.breast_physics = buffer.readBoolean();
        this.breast_physics_armor = buffer.readBoolean();
        this.show_in_armor = buffer.readBoolean();
        this.bounceMultiplier = buffer.readFloat();
        this.floppyMultiplier = buffer.readFloat();

        this.xOffset = buffer.readFloat();
        this.yOffset = buffer.readFloat();
        this.zOffset = buffer.readFloat();
        this.uniboob = buffer.readBoolean();
        this.cleavage = buffer.readFloat();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.uuid);
        buffer.writeInt(this.gender);
        buffer.writeFloat(this.bust_size);
        buffer.writeBoolean(this.hurtSounds);
        buffer.writeBoolean(this.breast_physics);
        buffer.writeBoolean(this.breast_physics_armor);
        buffer.writeBoolean(this.show_in_armor);
        buffer.writeFloat(this.bounceMultiplier);
        buffer.writeFloat(this.floppyMultiplier);

        buffer.writeFloat(this.xOffset);
        buffer.writeFloat(this.yOffset);
        buffer.writeFloat(this.zOffset);
        buffer.writeBoolean(this.uniboob);
        buffer.writeFloat(this.cleavage);
    }

    public static void handle(final PacketSendGenderInfo packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if(!player.getStringUUID().equals(packet.uuid)) return;

            GenderPlayer plr = WildfireGender.getPlayerByName(packet.uuid);
            if(plr == null) {
                plr = new GenderPlayer(packet.uuid);
                WildfireGender.CLOTHING_PLAYER.add(plr);
            }
            plr.gender = packet.gender;
            plr.updateBustSize(packet.bust_size);
            //plr.capeURL = packet.capeURL;
            plr.hurtSounds = packet.hurtSounds;

            //physics
            plr.breast_physics = packet.breast_physics;
            plr.breast_physics_armor = packet.breast_physics_armor;
            plr.show_in_armor = packet.show_in_armor;
            plr.bounceMultiplier = packet.bounceMultiplier;
            plr.floppyMultiplier = packet.floppyMultiplier;
            //System.out.println(plr.username + " - " + plr.gender);

            plr.getBreasts().xOffset = packet.xOffset;
            plr.getBreasts().yOffset = packet.yOffset;
            plr.getBreasts().zOffset = packet.zOffset;
            plr.getBreasts().isUniboob = packet.uniboob;
            plr.getBreasts().cleavage = packet.cleavage;
            
            try {
                List<ServerPlayer> PLAYERS = player.getServer().getPlayerList().getPlayers();
                for(ServerPlayer sPlayer : PLAYERS) {
                    PacketSync.send(player, WildfireGender.getPlayerByName(sPlayer.getStringUUID()));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            //PacketSync.send(plr);
        });

        context.get().setPacketHandled(true);
    }

    // Send Packet

    public static void send(GenderPlayer plr) {
        if(plr == null) return;
        WildfireGender.NETWORK.sendToServer(new PacketSendGenderInfo(plr));
    }
}
