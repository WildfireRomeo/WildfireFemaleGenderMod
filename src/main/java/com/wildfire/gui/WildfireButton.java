package com.wildfire.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import javax.swing.*;

public class WildfireButton extends ButtonWidget {

   public boolean transparent = false;
   public WildfireButton(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.TooltipSupplier tooltipSupplier) {
      super(x, y, width, height, message, onPress, tooltipSupplier);
   }
   public WildfireButton(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
      super(x, y, width, height, message, onPress);
   }

   @Override
   public void render(MatrixStack m, int mouseX, int mouseY, float partialTicks) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      TextRenderer font = minecraft.textRenderer;
      if(this.visible) {
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

         int clr = 0x444444 + (84 << 24);
         if(this.isHovered()) {
            clr = 0x666666 + (84 << 24);
         }
         if(!this.active)  clr = 0x222222 + (84 << 24);
         if(!transparent) fill(m, x, y, x + getWidth(), y + height, clr);
         font.draw(m, this.getMessage().getString(), x + (this.width / 2) - (font.getWidth(this.getMessage().getString()) / 2) + 1, y + (int) Math.ceil((float) height / 2f) - font.fontHeight / 2, active?0xFFFFFF:0x666666);
      }
      if(this.isHovered())
         this.renderTooltip(m, mouseX, mouseY);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(button)) {
            boolean bl = this.clicked(mouseX, mouseY);
            if (bl) {
               this.playDownSound(MinecraftClient.getInstance().getSoundManager());
               this.onClick(mouseX, mouseY);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public WildfireButton setTransparent(boolean b) {
      this.transparent = b;
      return this;
   }
}