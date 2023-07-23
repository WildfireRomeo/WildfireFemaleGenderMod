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

package com.wildfire.main;

import com.wildfire.api.IGenderArmor;
import com.wildfire.api.WildfireAPI;
import com.wildfire.render.armor.SimpleGenderArmor;
import com.wildfire.render.armor.EmptyGenderArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class WildfireHelper {
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    public static float randFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, (double) max + 1);
    }

    public static IGenderArmor getArmorConfig(ItemStack stack) {
        if (stack.isEmpty()) {
            return EmptyGenderArmor.INSTANCE;
        }

        if (WildfireAPI.getGenderArmors().get(stack.getItem()) != null) {
            return WildfireAPI.getGenderArmors().get(stack.getItem());
        } else {
            //TODO: Fabric Alternative to Capabilities? Maybe someone can help with this?
            if (stack.getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() == EquipmentSlot.CHEST) {
                //Start by checking if it is a vanilla chestplate as we have custom configurations for those we check against
                // the armor material instead of the item instance in case any mods define custom armor items using vanilla
                // materials as then we can make a better guess at what we want the default implementation to be
                ArmorMaterial material = armorItem.getMaterial();
                if (material == ArmorMaterials.LEATHER) {
                    return SimpleGenderArmor.LEATHER;
                } else if (material == ArmorMaterials.CHAIN) {
                    return SimpleGenderArmor.CHAIN_MAIL;
                } else if (material == ArmorMaterials.GOLD) {
                    return SimpleGenderArmor.GOLD;
                } else if (material == ArmorMaterials.IRON) {
                    return SimpleGenderArmor.IRON;
                } else if (material == ArmorMaterials.DIAMOND) {
                    return SimpleGenderArmor.DIAMOND;
                } else if (material == ArmorMaterials.NETHERITE) {
                    return SimpleGenderArmor.NETHERITE;
                }
                //Otherwise just fallback to our default armor implementation
                return SimpleGenderArmor.FALLBACK;
            }
            //If it is not an armor item default as if "nothing is being worn that covers the breast area"
            // this might not be fully accurate and may need some tweaks but in general is likely relatively
            // close to the truth of if it should render or not. This covers cases such as the elytra and
            // other wearables
            return EmptyGenderArmor.INSTANCE;
        }
    }

    @Environment(EnvType.CLIENT)
    public static void drawCenteredText(DrawContext ctx, TextRenderer textRenderer, Text text, int x, int y, int color) {
        int centeredX = (int) x - textRenderer.getWidth(text) / 2;
        ctx.drawText(textRenderer, text, centeredX, y, color, false);
    }

    public static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int left, int top, int right, int bottom, int color) {
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
            double g = MathHelper.lerp(f, 0.0, (double)l);
            context.enableScissor(left, top, right, bottom);
            context.drawText(textRenderer, text, left - (int)g, j, color, false);
            context.disableScissor();
        } else {
            drawCenteredText(context, textRenderer, text, (left + right) / 2, j, color);
        }

    }
}