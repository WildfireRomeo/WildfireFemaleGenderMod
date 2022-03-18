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
import com.wildfire.main.config.Configuration;
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
        this.gender = plr.getGender();
        this.bust_size = plr.get(Configuration.BUST_SIZE);
        this.hurtSounds = plr.get(Configuration.HURT_SOUNDS);

        //physics variables
        this.breast_physics = plr.get(Configuration.BREAST_PHYSICS);
        this.breast_physics_armor = plr.get(Configuration.BREAST_PHYSICS_ARMOR);
        this.show_in_armor = plr.get(Configuration.SHOW_IN_ARMOR);
        this.bounceMultiplier = plr.get(Configuration.BOUNCE_MULTIPLIER);
        this.floppyMultiplier = plr.get(Configuration.FLOPPY_MULTIPLIER);

        this.xOffset = plr.get(Configuration.BREASTS_OFFSET_X);
        this.yOffset = plr.get(Configuration.BREASTS_OFFSET_Y);
        this.zOffset = plr.get(Configuration.BREASTS_OFFSET_Z);
        this.uniboob = plr.get(Configuration.BREASTS_UNIBOOB);
        this.cleavage = plr.get(Configuration.BREASTS_CLEAVAGE);
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
        plr.update(Configuration.GENDER, gender);
        plr.update(Configuration.BUST_SIZE, bust_size);
        plr.update(Configuration.HURT_SOUNDS, hurtSounds);

        //physics
        plr.update(Configuration.BREAST_PHYSICS, breast_physics);
        plr.update(Configuration.BREAST_PHYSICS_ARMOR, breast_physics_armor);
        plr.update(Configuration.SHOW_IN_ARMOR, show_in_armor);
        plr.update(Configuration.BOUNCE_MULTIPLIER, bounceMultiplier);
        plr.update(Configuration.FLOPPY_MULTIPLIER, floppyMultiplier);
        //System.out.println(plr.username + " - " + plr.gender);

        plr.update(Configuration.BREASTS_OFFSET_X, xOffset);
        plr.update(Configuration.BREASTS_OFFSET_Y, yOffset);
        plr.update(Configuration.BREASTS_OFFSET_Z, zOffset);
        plr.update(Configuration.BREASTS_UNIBOOB, uniboob);
        plr.update(Configuration.BREASTS_CLEAVAGE, cleavage);
    }
}
