/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.wildfire.main.networking;

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.GenderPlayer.Gender;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;

public abstract class PacketGenderInfo {
    protected final UUID uuid;
    private final Gender gender;
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

    protected PacketGenderInfo(GenderPlayer plr) {
        this.uuid = plr.uuid;
        this.gender = plr.gender;
        this.bust_size = plr.getBustSize();
        this.hurtSounds = plr.hurtSounds;

        //physics variables
        this.breast_physics = plr.hasBreastPhysics;
        this.breast_physics_armor = plr.hasArmorBreastPhysics;
        this.show_in_armor = plr.showBreastsInArmor;
        this.bounceMultiplier = plr.bounceMultiplier;
        this.floppyMultiplier = plr.floppyMultiplier;

        this.xOffset = plr.getBreasts().xOffset;
        this.yOffset = plr.getBreasts().yOffset;
        this.zOffset = plr.getBreasts().zOffset;

        this.uniboob = plr.getBreasts().isUniboob;
        this.cleavage = plr.getBreasts().cleavage;
    }

    protected PacketGenderInfo(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.gender = buffer.readEnum(Gender.class);
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
        buffer.writeUUID(this.uuid);
        buffer.writeEnum(this.gender);
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

    protected void updatePlayerFromPacket(GenderPlayer plr) {
        plr.gender = gender;
        plr.updateBustSize(bust_size);
        //plr.capeURL = capeURL;
        plr.hurtSounds = hurtSounds;

        //physics
        plr.hasBreastPhysics = breast_physics;
        plr.hasArmorBreastPhysics = breast_physics_armor;
        plr.showBreastsInArmor = show_in_armor;
        plr.bounceMultiplier = bounceMultiplier;
        plr.floppyMultiplier = floppyMultiplier;
        //System.out.println(plr.username + " - " + plr.gender);

        plr.getBreasts().xOffset = xOffset;
        plr.getBreasts().yOffset = yOffset;
        plr.getBreasts().zOffset = zOffset;
        plr.getBreasts().isUniboob = uniboob;
        plr.getBreasts().cleavage = cleavage;
    }
}
