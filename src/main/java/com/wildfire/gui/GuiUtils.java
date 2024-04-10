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

package com.wildfire.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class GuiUtils {
	private GuiUtils() {
		throw new UnsupportedOperationException();
	}

	// Reimplementation of DrawContext#drawCenteredTextWithShadow but with the text shadow removed
	public static void drawCenteredText(DrawContext ctx, TextRenderer textRenderer, Text text, int x, int y, int color) {
	    int centeredX = x - textRenderer.getWidth(text) / 2;
	    ctx.drawText(textRenderer, text, centeredX, y, color, false);
	}

	// Reimplementation of ClickableWidget#drawScrollableText but with the text shadow removed
	public static void drawScrollableTextWithoutShadow(DrawContext context, TextRenderer textRenderer, Text text, int left, int top, int right, int bottom, int color) {
	    int i = textRenderer.getWidth(text);
	    int var10000 = top + bottom;
	    Objects.requireNonNull(textRenderer);
	    int j = (var10000 - 9) / 2 + 1;
	    int k = right - left;
	    if (i > k) {
	        int l = i - k;
	        double d = (double) Util.getMeasuringTimeMs() / 1000.0;
	        double e = Math.max((double)l * 0.5, 3.0);
	        double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
	        double g = MathHelper.lerp(f, 0.0, l);
	        context.enableScissor(left, top, right, bottom);
	        context.drawText(textRenderer, text, left - (int)g, j, color, false);
	        context.disableScissor();
	    } else {
	        drawCenteredText(context, textRenderer, text, (left + right) / 2, j, color);
	    }
	}

	// Reimplementation of InventoryScreen#drawEntity, intended to allow for applying our own scissor calls, and
	// accepting an origin point instead of X/Y bounds
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
		// divide by entity scale to ensure that we always draw the entity at a consistent size
		float renderSize = size / entity.getScale();
		InventoryScreen.drawEntity(ctx, x, y, renderSize, new Vector3f(), quaternionf, quaternionf2, entity);
		entity.bodyYaw = k;
		entity.setYaw(l);
		entity.setPitch(m);
		entity.prevHeadYaw = n;
		entity.headYaw = o;
	}
}
