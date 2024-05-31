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
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public abstract class AbstractSyncPacket implements IWildfirePacket {

    protected static <PACKET extends AbstractSyncPacket> StreamCodec<ByteBuf, PACKET> streamCodec(
          Function6<UUID, Gender, Float, Boolean, BreastPhysics, Breasts, PACKET> constructor) {
        return StreamCodec.composite(
              UUIDUtil.STREAM_CODEC, packet -> packet.uuid,
              Gender.STREAM_CODEC, packet -> packet.gender,
              ByteBufCodecs.FLOAT, packet -> packet.bustSize,
              ByteBufCodecs.BOOL, packet -> packet.hurtSounds,
              BreastPhysics.STREAM_CODEC, packet -> packet.breastPhysics,
              Breasts.STREAM_CODEC, packet -> packet.breasts,
              constructor
        );
    }

    protected final UUID uuid;
    protected final Gender gender;
    protected final float bustSize;
    protected final boolean hurtSounds;
    protected final BreastPhysics breastPhysics;
    protected final Breasts breasts;

    protected AbstractSyncPacket(PlayerConfig plr) {
        this(plr.uuid, plr.getGender(), plr.getBustSize(), plr.hasHurtSounds(), new BreastPhysics(plr), plr.getBreasts());
    }

    protected AbstractSyncPacket(UUID uuid, Gender gender, float bustSize, boolean hurtSounds, BreastPhysics breastPhysics, Breasts breasts) {
        this.uuid = uuid;
        this.gender = gender;
        this.bustSize = bustSize;
        this.hurtSounds = hurtSounds;
        this.breastPhysics = breastPhysics;
        this.breasts = breasts;
    }

    protected void updatePlayerFromPacket(PlayerConfig plr) {
        plr.updateGender(gender);
        plr.updateBustSize(bustSize);
        plr.updateHurtSounds(hurtSounds);
        breastPhysics.updatePlayer(plr);
        plr.getBreasts().updateFrom(breasts);
        //WildfireGender.logger.debug("{} - {}", plr.username, plr.gender);
    }

    protected record BreastPhysics(boolean physics, boolean showInArmor, float bounceMultiplier, float floppyMultiplier) {

        public static final StreamCodec<ByteBuf, BreastPhysics> STREAM_CODEC = StreamCodec.composite(
              ByteBufCodecs.BOOL, BreastPhysics::physics,
              ByteBufCodecs.BOOL, BreastPhysics::showInArmor,
              ByteBufCodecs.FLOAT, BreastPhysics::bounceMultiplier,
              ByteBufCodecs.FLOAT, BreastPhysics::floppyMultiplier,
              BreastPhysics::new
        );

        private BreastPhysics(PlayerConfig plr) {
            this(plr.hasBreastPhysics(), plr.showBreastsInArmor(), plr.getBounceMultiplier(), plr.getFloppiness());
        }

        private void updatePlayer(PlayerConfig plr) {
            plr.updateBreastPhysics(physics);
            plr.updateShowBreastsInArmor(showInArmor);
            plr.updateBounceMultiplier(bounceMultiplier);
            plr.updateFloppiness(floppyMultiplier);
        }
    }
}
