package com.wildfire.gui;
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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;


public class WildfireButton extends AbstractButton {
   public static final WildfireButton.ITooltip NO_TOOLTIP = (button, matrixStack, mouseX, mouseY) -> {};

   public boolean transparent = false;
   protected final WildfireButton.IPressable onPress;
   protected final WildfireButton.ITooltip onTooltip;

   public WildfireButton(int x, int y, int w, int h, Component text, WildfireButton.IPressable onPress, WildfireButton.ITooltip onTooltip) {
      super(x, y, w, h, text);
      this.onPress = onPress;
      this.onTooltip = onTooltip;
   }
   public WildfireButton(int x, int y, int w, int h, Component text, WildfireButton.IPressable onPress) {
      this(x, y, w, h, text, onPress, NO_TOOLTIP);
   }

   @Override
   public void render(PoseStack m, int mouseX, int mouseY, float partialTicks) {
      Minecraft minecraft = Minecraft.getInstance();
      Font font = minecraft.font;
      if(this.visible) {
         this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         if (this.isHoveredOrFocused()) {
            this.renderToolTip(m, mouseX, mouseY);
         }
         int clr = 0x444444 + (84 << 24);
         if(this.isHoveredOrFocused()) clr = 0x666666 + (84 << 24);
         if(!this.active)  clr = 0x222222 + (84 << 24);
         if(!transparent) fill(m, x, y, x + getWidth(), y + height, clr);

         font.draw(m, this.getMessage().getString(), x + (this.width / 2) - (font.width(this.getMessage().getString()) / 2) + 1, y + (int) Math.ceil((float) height / 2f) - font.lineHeight / 2, active?0xFFFFFF:0x666666);
      }
      RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
   }

   public void onPress() {
      if(this.onPress != null) this.onPress.onPress(this);
   }


   public WildfireButton setTransparent(boolean b) {
      this.transparent = b;
      return this;
   }
   public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
      this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
   }

   @Override
   public void updateNarration(NarrationElementOutput narrationElementOutput) {

   }

   @OnlyIn(Dist.CLIENT)
   public interface IPressable {
      void onPress(WildfireButton p_onPress_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public interface ITooltip {
      void onTooltip(WildfireButton p_onTooltip_1_, PoseStack p_onTooltip_2_, int p_onTooltip_3_, int p_onTooltip_4_);
   }
}