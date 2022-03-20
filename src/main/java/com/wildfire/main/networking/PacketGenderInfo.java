/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022 WildfireRomeo

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

import com.wildfire.main.Breasts;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.GenderPlayer.Gender;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

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
        this.bust_size = plr.getBustSize();
        this.hurtSounds = plr.hasHurtSounds();

        //physics variables
        this.breast_physics = plr.hasBreastPhysics();
        this.breast_physics_armor = plr.hasArmorBreastPhysics();
        this.show_in_armor = plr.showBreastsInArmor();
        this.bounceMultiplier = plr.getBounceMultiplierRaw();
        this.floppyMultiplier = plr.getFloppiness();

        Breasts breasts = plr.getBreasts();
        this.xOffset = breasts.getXOffset();
        this.yOffset = breasts.getYOffset();
        this.zOffset = breasts.getZOffset();

        this.uniboob = breasts.isUniboob();
        this.cleavage = breasts.getCleavage();
    }

    protected PacketGenderInfo(PacketByteBuf buffer) {
        this.uuid = buffer.readUuid();
        this.gender = buffer.readEnumConstant(Gender.class);
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

    public void encode(PacketByteBuf buffer) {
        buffer.writeUuid(this.uuid);
        buffer.writeEnumConstant(this.gender);
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
        plr.updateGender(gender);
        plr.updateBustSize(bust_size);
        plr.updateHurtSounds(hurtSounds);

        //physics
        plr.updateBreastPhysics(breast_physics);
        plr.updateArmorBreastPhysics(breast_physics_armor);
        plr.updateShowBreastsInArmor(show_in_armor);
        plr.updateBounceMultiplier(bounceMultiplier);
        plr.updateFloppiness(floppyMultiplier);
        //System.out.println(plr.username + " - " + plr.gender);

        Breasts breasts = plr.getBreasts();
        breasts.updateXOffset(xOffset);
        breasts.updateYOffset(yOffset);
        breasts.updateZOffset(zOffset);
        breasts.updateUniboob(uniboob);
        breasts.updateCleavage(cleavage);
    }
}
