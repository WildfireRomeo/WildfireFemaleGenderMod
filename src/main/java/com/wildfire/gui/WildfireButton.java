/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

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

package com.wildfire.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class WildfireButton extends Button implements WildfireButton2 {
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
   public void renderButton(@Nonnull PoseStack m, int mouseX, int mouseY, float partialTicks) {
      Minecraft minecraft = Minecraft.getInstance();
      Font font = minecraft.font;
      int clr = 0x444444 + (84 << 24);
      if(this.isHoveredOrFocused()) clr = 0x666666 + (84 << 24);
      if(!this.active)  clr = 0x222222 + (84 << 24);
      if(!transparent) fill(m, getX(), getY(), getX() + getWidth(), getY() + getHeight(), clr);

      font.draw(m, this.getMessage(),getX() + (this.width / 2f) - (font.width(this.getMessage()) / 2f) + 1, getY() + (int) Math.ceil((float) height / 2f) - font.lineHeight / 2f, active ? 0xFFFFFF : 0x666666);
      RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
   }

   public WildfireButton setTransparent(boolean b) {
      this.transparent = b;
      return this;
   }
}