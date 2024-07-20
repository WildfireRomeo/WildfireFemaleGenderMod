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

import com.mojang.datafixers.util.Function6;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.Gender;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

import java.util.UUID;

abstract class AbstractSyncPacket {

    protected static <T extends AbstractSyncPacket> PacketCodec<ByteBuf, T> codec(SyncPacketConstructor<T> constructor) {
        return PacketCodec.tuple(
                Uuids.PACKET_CODEC, p -> p.uuid,
                Gender.CODEC, p -> p.gender,
                PacketCodecs.FLOAT, p -> p.bustSize,
                PacketCodecs.BOOL, p -> p.hurtSounds,
                BreastPhysics.CODEC, p -> p.physics,
                Breasts.CODEC, p -> p.breasts,
                constructor
        );
    }

    protected final UUID uuid;
    protected final Gender gender;
    protected final float bustSize;
    protected final boolean hurtSounds;
    protected final BreastPhysics physics;
    protected final Breasts breasts;

    protected AbstractSyncPacket(UUID uuid, Gender gender, float bustSize, boolean hurtSounds, BreastPhysics physics, Breasts breasts) {
        this.uuid = uuid;
        this.gender = gender;
        this.bustSize = bustSize;
        this.hurtSounds = hurtSounds;
        this.physics = physics;
        this.breasts = breasts;
    }

    protected AbstractSyncPacket(PlayerConfig plr) {
        this(plr.uuid, plr.getGender(), plr.getBustSize(), plr.hasHurtSounds(), new BreastPhysics(plr), plr.getBreasts());
    }

    protected void updatePlayerFromPacket(PlayerConfig plr) {
        plr.updateGender(gender);
        plr.updateBustSize(bustSize);
        plr.updateHurtSounds(hurtSounds);
        physics.applyTo(plr);
        plr.getBreasts().copyFrom(breasts);
    }

    protected record BreastPhysics(boolean physics, boolean showInArmor, float bounceMultiplier, float floppyMultiplier) {

        public static final PacketCodec<ByteBuf, BreastPhysics> CODEC = PacketCodec.tuple(
                PacketCodecs.BOOL, BreastPhysics::physics,
                PacketCodecs.BOOL, BreastPhysics::showInArmor,
                PacketCodecs.FLOAT, BreastPhysics::bounceMultiplier,
                PacketCodecs.FLOAT, BreastPhysics::floppyMultiplier,
                BreastPhysics::new
        );

        private BreastPhysics(PlayerConfig plr) {
            this(plr.hasBreastPhysics(), plr.showBreastsInArmor(), plr.getBounceMultiplier(), plr.getFloppiness());
        }

        private void applyTo(PlayerConfig plr) {
            plr.updateBreastPhysics(physics);
            plr.updateShowBreastsInArmor(showInArmor);
            plr.updateBounceMultiplier(bounceMultiplier);
            plr.updateFloppiness(floppyMultiplier);
        }
    }

    @FunctionalInterface
    protected interface SyncPacketConstructor<T extends AbstractSyncPacket> extends Function6<UUID, Gender, Float, Boolean, BreastPhysics, Breasts, T> {
    }
}
