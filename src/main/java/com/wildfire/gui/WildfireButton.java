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
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class WildfireButton extends Button {
   public boolean transparent = false;

   public WildfireButton(int x, int y, int width, int height, Component text, Button.OnPress onPress, CreateNarration narrationSupplier) {
      super(x, y, width, height, text, onPress, narrationSupplier);
   }
   public WildfireButton(int x, int y, int w, int h, Component text, Button.OnPress onPress) {
      this(x, y, w, h, text, onPress, DEFAULT_NARRATION);
   }
   public WildfireButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, Tooltip tooltip) {
      this(x, y, width, height, message, onPress, DEFAULT_NARRATION);
      setTooltip(tooltip);
   }

   @Override
   public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      Minecraft minecraft = Minecraft.getInstance();
      int clr = 0x444444 + (84 << 24);
      if(this.isHoveredOrFocused()) clr = 0x666666 + (84 << 24);
      if(!this.active)  clr = 0x222222 + (84 << 24);
      if(!transparent) graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), clr);
      GuiHelper.renderScrollingString(graphics, this, minecraft.font, 2, active ? 0xFFFFFF : 0x666666);
      RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
   }

   public WildfireButton setTransparent(boolean b) {
      this.transparent = b;
      return this;
   }
}