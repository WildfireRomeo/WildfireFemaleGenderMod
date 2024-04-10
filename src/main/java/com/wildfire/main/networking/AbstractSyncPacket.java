/*
    Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
    Copyright (C) 2023 WildfireRomeo

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.wildfire.main.networking;

import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.Gender;
import net.minecraft.network.PacketByteBuf;
import org.joml.Vector3f;

import java.util.UUID;

abstract class AbstractSyncPacket {
    protected final UUID uuid;
    private final Gender gender;
    private final float bustSize;

    //physics variables
    private final boolean breastPhysics;
    private final boolean showInArmor;
    private final float bounceMultiplier;
    private final float floppyMultiplier;

    private final Vector3f offsets;
    private final boolean uniboob;
    private final float cleavage;

    private final boolean hurtSounds;

    protected AbstractSyncPacket(PlayerConfig plr) {
        this.uuid = plr.uuid;
        this.gender = plr.getGender();
        this.bustSize = plr.getBustSize();
        this.hurtSounds = plr.hasHurtSounds();

        //physics variables
        this.breastPhysics = plr.hasBreastPhysics();
        this.showInArmor = plr.showBreastsInArmor();
        this.bounceMultiplier = plr.getBounceMultiplier();
        this.floppyMultiplier = plr.getFloppiness();

        Breasts breasts = plr.getBreasts();
        this.offsets = breasts.getOffsets();
        this.uniboob = breasts.isUniboob();
        this.cleavage = breasts.getCleavage();
    }

    protected AbstractSyncPacket(PacketByteBuf buffer) {
        this.uuid = buffer.readUuid();
        this.gender = buffer.readEnumConstant(Gender.class);
        this.bustSize = buffer.readFloat();
        this.hurtSounds = buffer.readBoolean();

        //physics variables
        this.breastPhysics = buffer.readBoolean();
        this.showInArmor = buffer.readBoolean();
        this.bounceMultiplier = buffer.readFloat();
        this.floppyMultiplier = buffer.readFloat();

        this.offsets = buffer.readVector3f();
        this.uniboob = buffer.readBoolean();
        this.cleavage = buffer.readFloat();
    }

    protected void encode(PacketByteBuf buffer) {
        buffer.writeUuid(this.uuid);
        buffer.writeEnumConstant(this.gender);
        buffer.writeFloat(this.bustSize);
        buffer.writeBoolean(this.hurtSounds);
        buffer.writeBoolean(this.breastPhysics);
        buffer.writeBoolean(this.showInArmor);
        buffer.writeFloat(this.bounceMultiplier);
        buffer.writeFloat(this.floppyMultiplier);

        buffer.writeVector3f(offsets);
        buffer.writeBoolean(this.uniboob);
        buffer.writeFloat(this.cleavage);
    }

    protected void updatePlayerFromPacket(PlayerConfig plr) {
        plr.updateGender(gender);
        plr.updateBustSize(bustSize);
        plr.updateHurtSounds(hurtSounds);

        //physics
        plr.updateBreastPhysics(breastPhysics);
        plr.updateShowBreastsInArmor(showInArmor);
        plr.updateBounceMultiplier(bounceMultiplier);
        plr.updateFloppiness(floppyMultiplier);
        //System.out.println(plr.username + " - " + plr.gender);

        Breasts breasts = plr.getBreasts();
        breasts.updateOffsets(offsets);
        breasts.updateUniboob(uniboob);
        breasts.updateCleavage(cleavage);
    }
}
