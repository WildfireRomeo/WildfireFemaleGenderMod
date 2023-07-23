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

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.main.WildfireHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class WildfireButton extends ButtonWidget {

   public boolean transparent = false;

   public WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress, NarrationSupplier narrationSupplier) {
      super(x, y, w, h, text, onPress, narrationSupplier);
   }
   public WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress) {
      this(x, y, w, h, text, onPress, DEFAULT_NARRATION_SUPPLIER);
   }
   public WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress, Tooltip tooltip) {
      this(x, y, w, h, text, onPress, DEFAULT_NARRATION_SUPPLIER);
      setTooltip(tooltip);
   }

   @Override
   public void renderButton(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      TextRenderer font = minecraft.textRenderer;
      int clr = 0x444444 + (84 << 24);
      if(this.isHovered()) clr = 0x666666 + (84 << 24);
      if(!this.active) clr = 0x222222 + (84 << 24);
      if(!transparent) ctx.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), clr);

      int textColor = active ? 0xFFFFFF : 0x666666;
      int i = this.getX() + 2;
      int j = this.getX() + this.getWidth() - 2;
      WildfireHelper.drawScrollableText(ctx, font, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), textColor);
      RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
   }

   public WildfireButton setTransparent(boolean b) {
      this.transparent = b;
      return this;
   }
}