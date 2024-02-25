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

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class GuiHelper {

    private GuiHelper() {
    }

    //Copy of AbstractWidget#renderScrollingString, but without shadow
    public static void renderScrollingString(GuiGraphics graphics, AbstractWidget widget, Font font, int width, int color) {
        int minX = widget.getX() + width;
        int maxX = widget.getX() + widget.getWidth() - width;
        renderScrollingString(graphics, font, widget.getMessage(), minX, widget.getY(), maxX, widget.getY() + widget.getHeight(), color);
    }

    //Copy of AbstractWidget#renderScrollingString, but without shadow
    public static void renderScrollingString(GuiGraphics graphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
        renderScrollingString(graphics, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    //Copy of AbstractWidget#renderScrollingString, but without shadow
    public static void renderScrollingString(GuiGraphics graphics, Font font, Component text, int middleX, int minX, int minY, int maxX, int maxY, int color) {
        int textWidth = font.width(text);
        int height = (minY + maxY - 9) / 2 + 1;
        int width = maxX - minX;
        if (textWidth > width) {
            int l = textWidth - width;
            double d0 = (double) Util.getMillis() / 1000.0;
            double d1 = Math.max((double)l * 0.5, 3.0);
            double d2 = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d0 / d1)) / 2.0 + 0.5;
            double d3 = Mth.lerp(d2, 0.0, l);
            graphics.enableScissor(minX, minY, maxX, maxY);
            graphics.drawString(font, text, minX - (int) d3, height, color, false);//Don't draw the shadow
            graphics.disableScissor();
        } else {
            int i1 = Mth.clamp(middleX, minX + textWidth / 2, maxX - textWidth / 2);
            //Copy of drawCenteredString but passing false for the shadow
            FormattedCharSequence charSequence = text.getVisualOrderText();
            graphics.drawString(font, charSequence, i1 - font.width(charSequence) / 2, height, color, false);
        }
    }
}