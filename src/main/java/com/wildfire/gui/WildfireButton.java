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
import com.wildfire.gui.screen.BaseWildfireScreen;
import com.wildfire.main.WildfireHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WildfireButton extends ButtonWidget {

   public boolean transparent = false;
   private final @Nullable Supplier<Text> textSupplier;
   private boolean isCloseButton = false;
   private boolean shouldScroll = true;

   // TODO remove these public constructors and convert buttons to using a Builder
   public WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress, NarrationSupplier narrationSupplier) {
      super(x, y, w, h, text, onPress, narrationSupplier);
      this.textSupplier = null;
   }
   public WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress) {
      this(x, y, w, h, text, onPress, DEFAULT_NARRATION_SUPPLIER);

   }
   public WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress, Tooltip tooltip) {
      this(x, y, w, h, text, onPress, DEFAULT_NARRATION_SUPPLIER);
      setTooltip(tooltip);
   }

   private WildfireButton(int x, int y, int w, int h, Supplier<Text> textSupplier, PressAction onPress, @Nullable NarrationSupplier narrationSupplier) {
      super(x, y, w, h, textSupplier.get(), onPress, narrationSupplier != null ? narrationSupplier : DEFAULT_NARRATION_SUPPLIER);
      this.textSupplier = textSupplier;
   }

   @Override
   public Text getMessage() {
      if(this.textSupplier != null) {
         return this.textSupplier.get();
      }
      return super.getMessage();
   }

   public boolean isCloseButton() {
      return this.isCloseButton;
   }

   @Override
   protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      TextRenderer font = minecraft.textRenderer;
      int clr = 0x444444 + (84 << 24);
      if(this.isHovered()) clr = 0x666666 + (84 << 24);
      if(!this.active) clr = 0x222222 + (84 << 24);
      if(!transparent) ctx.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), clr);

      int textColor = active ? 0xFFFFFF : 0x666666;
      int i = this.getX() + 2;
      int j = this.getX() + this.getWidth() - 2;

      Text message = getMessage();
      if(!this.active) {
         message = Texts.join(
                 message.copy().withoutStyle().stream()
                         .map(x -> x.copy().formatted(Formatting.RESET))
                         .toList(),
                 Text.empty());
      }

      if(this.shouldScroll) {
         WildfireHelper.drawScrollableTextWithoutShadow(ctx, font, message, i, this.getY(), j, this.getY() + this.getHeight(), textColor);
      } else {
         ctx.drawText(font, message, i, this.getY(), textColor, false);
      }
      RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
   }

   public WildfireButton setTransparent(boolean b) {
      this.transparent = b;
      return this;
   }
   public WildfireButton setActive(boolean b) {
      this.active = b;
      return this;
   }

   public static final class Builder {
      private int x = 0, y = 0;
      private int width, height;
      private @Nullable Text text;
      private @Nullable Supplier<Text> textSupplier;
      private PressAction onClick;
      private @Nullable NarrationSupplier narrationSupplier;
      private @Nullable Tooltip tooltip;
      private boolean isClose = false;
      private boolean shouldScroll = true;

      /**
       * Set a static {@link Text} to be used for this button
       *
       * @apiNote If both this and {@link #text(Supplier)} are used, the supplier takes priority over this.
       */
      public Builder text(Text text) {
         this.text = text;
         return this;
      }

      /**
       * Set the text supplier to be used for the button message
       *
       * @apiNote This supplier is called once per frame, and as such you should avoid any computationally expensive
       *          calls in this supplier. If both this and {@link #text(Text)} are used, this supplier takes priority.
       */
      public Builder text(Supplier<Text> textSupplier) {
         this.textSupplier = textSupplier;
         return this;
      }

      /**
       * Set the callback to be executed when this button is clicked
       */
      public Builder onClick(PressAction onClick) {
         this.onClick = onClick;
         return this;
      }

      /**
       * Marks this button as a close button for the provided screen
       *
       * @apiNote This also implicitly adds a {@link net.minecraft.client.gui.widget.ButtonWidget.PressAction PressAction} for
       *          closing the current screen for you.
       */
      public Builder close(BaseWildfireScreen screen) {
         this.isClose = true;
         return this.onClick(button -> MinecraftClient.getInstance().setScreen(screen.parent));
      }

      /**
       * Sets the X and Y coordinates of the button drawn on screen
       *
       * @apiNote If this button is used in a {@link com.wildfire.gui.screen.DynamicallySizedScreen DynamicallySizedScreen},
       *          this doesn't need to be set, as your button will automatically be repositioned based on its creation
       *          order, and whether its {@link #close a close button} or not.
       */
      public Builder position(int x, int y) {
         this.x = x;
         this.y = y;
         return this;
      }

      /**
       * Sets the width and height for the built button
       */
      public Builder size(int width, int height) {
         this.height = height;
         this.width = width;
         return this;
      }

      /**
       * Set the narration text supplier, intended for use with buttons with text that doesn't properly describe
       * what they do, such as {@link #close a close button}.<br>
       * If this isn't set, the default narration supplier is used instead, which simply reads the button text.
       */
      public Builder narration(NarrationSupplier supplier) {
         this.narrationSupplier = supplier;
         return this;
      }

      /**
       * Add a tooltip to the built button
       *
       * @param tooltip The {@link Tooltip} to render, or {@code null} to remove any previously set tooltip
       */
      public Builder tooltip(@Nullable Tooltip tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      /**
       * Prevents this button's text from scrolling; this is intended for very small buttons where the text
       * slightly spilling out of the button's rendered region doesn't really matter.
       */
      public Builder noScrollingText() {
         this.shouldScroll = false;
         return this;
      }

      public WildfireButton build() {
         WildfireButton button;
         if(this.textSupplier != null) {
            button = new WildfireButton(x, y, width, height, textSupplier, onClick, narrationSupplier);
         } else if(this.text != null) {
            button = new WildfireButton(x, y, width, height, text, onClick, narrationSupplier);
         } else {
            throw new IllegalStateException("neither #text(Supplier<Text>) nor #text(Text) were called!");
         }
         if(tooltip != null) {
            button.setTooltip(tooltip);
         }
         button.isCloseButton = this.isClose;
         button.shouldScroll = this.shouldScroll;
         return button;
      }
   }
}