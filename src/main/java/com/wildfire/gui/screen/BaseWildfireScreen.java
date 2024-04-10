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

package com.wildfire.gui.screen;

import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.WildfireGender;
import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public abstract class BaseWildfireScreen extends Screen {

    protected final UUID playerUUID;
    protected final Screen parent;

    protected BaseWildfireScreen(Text title, Screen parent, UUID uuid) {
        super(title);
        this.parent = parent;
        this.playerUUID = uuid;
    }

    public PlayerConfig getPlayer() {
        return WildfireGender.getPlayerById(this.playerUUID);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // this is adapted from InventoryScreen#drawEntity to allow for applying our own scissor calls, and instead
    // simply accept an origin point to render from
    public static void drawEntityOnScreen(DrawContext ctx, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float i = (float) Math.atan(mouseX / 40.0F);
        float j = (float) Math.atan(mouseY / 40.0F);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(j * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf2);
        float k = entity.bodyYaw;
        float l = entity.getYaw();
        float m = entity.getPitch();
        float n = entity.prevHeadYaw;
        float o = entity.headYaw;
        entity.bodyYaw = 180.0F + i * 20.0F;
        entity.setYaw(180.0F + i * 40.0F);
        entity.setPitch(-j * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        // divide by entity scale to ensure that we always draw the entity at a constant size, avoiding the entity
        // being either too small or far too large for the gui
        float renderSize = size / entity.getScale();
        InventoryScreen.drawEntity(ctx, x, y, renderSize, new Vector3f(0f), quaternionf, quaternionf2, entity);
        entity.bodyYaw = k;
        entity.setYaw(l);
        entity.setPitch(m);
        entity.prevHeadYaw = n;
        entity.headYaw = o;
    }
}