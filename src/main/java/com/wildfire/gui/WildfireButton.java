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

import com.wildfire.gui.screen.BaseWildfireScreen;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@Accessors(chain = true)
public class WildfireButton extends ButtonWidget {

   private final @Nullable Supplier<Text> textSupplier;

   /**
    * If {@code true}, this button's text will be rendered slightly darkened (provided that it isn't
    * also {@link #active inactive}).
    */
   private @Setter boolean deselected = false;
   /**
    * If {@code true}, this button's rendered background will be rendered as if the button was being
    * {@link #isSelected() hovered over}.
    */
   private @Setter boolean highlightBackground = false;
   /**
    * If {@code true}, this button won't render its typical background.
    */
   private @Setter boolean transparent = false;
   /**
    * If {@code true}, this button will be considered as a close button; this is primarily used by
    * {@link com.wildfire.gui.screen.DynamicallySizedScreen DynamicallySizedScreen} to position such
    * buttons at the top of the rendered UI.
    */
   private @Getter @Setter boolean closeButton = false;
   /**
    * If {@code false}, this button's text won't scroll to fit within its bounds, and will instead stay in a fixed
    * position. This is intended for small buttons that only have icon-like text, which don't have any reason to care
    * about if its text spills slightly out of bounds.
    */
   private @Setter boolean textScrollable = true;

   protected WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress, @NotNull NarrationSupplier narrationSupplier) {
      super(x, y, w, h, text, onPress, narrationSupplier);
      this.textSupplier = null;
   }

   protected WildfireButton(int x, int y, int w, int h, Supplier<Text> textSupplier, PressAction onPress, @NotNull NarrationSupplier narrationSupplier) {
      super(x, y, w, h, textSupplier.get(), onPress, narrationSupplier);
      this.textSupplier = textSupplier;
   }

   @Override
   public Text getMessage() {
      if(this.textSupplier != null) {
         return this.textSupplier.get();
      }
      return super.getMessage();
   }

   @Override
   protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      TextRenderer font = minecraft.textRenderer;
      int clr = 0x444444 + (84 << 24);
      if(isSelected() || highlightBackground) clr = 0x666666 + (84 << 24);
      if(!active) clr = 0x222222 + (84 << 24);
      if(!transparent) ctx.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), clr);

      int textColor = active ? (deselected ? 0x888888 : 0xFFFFFF) : 0x666666;
      int i = this.getX() + 2;
      int j = this.getX() + this.getWidth() - 2;

      Text message = getMessage();
      if(!this.active) {
         message = GuiUtils.removeTextFormatting(message);
      }

      if(textScrollable) {
         GuiUtils.drawScrollableTextWithoutShadow(ctx, font, message, i, this.getY(), j, this.getY() + this.getHeight(), textColor);
      } else {
         ctx.drawText(font, message, i, this.getY(), textColor, false);
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   @Setter
   @Accessors(fluent = true)
   public static final class Builder implements IWildfireWidgetBuilder<WildfireButton, Builder> {
      private Builder() {}

      // Button placement
      private int x = 0, y = 0;
      private int width, height;

      // Appearance
      /**
       * The {@link Text} to render on this button; this has no effect if {@link #textSupplier} is set.
       */
      private @Nullable Text text;
      /**
       * Optional supplier to use instead of {@link #text}; this takes priority over the aforementioned value if
       * both are set.
       *
       * @apiNote This supplier may be called multiple times per frame, and as such you should avoid performing any
       *          computationally expensive operations in this supplier.
       */
      private @Nullable Supplier<Text> textSupplier;
      /**
       * Optional {@link Text} supplier for the narrator to read instead of the provided {@link #text}
       * (or {@link #textSupplier}). If not set, {@link #DEFAULT_NARRATION_SUPPLIER} is used instead.
       */
      private @Nullable NarrationSupplier narrationSupplier;
      /**
       * Optional {@link Tooltip} to render whenever the player hovers over this button.
       */
      private @Nullable Tooltip tooltip;

      // User interaction
      /**
       * Called when the player interacts with the button
       */
      private PressAction onClick;
      /**
       * Variation of {@link #onClick(PressAction)}; opens the provided {@link Screen} when clicked.
       */
      private @Nullable Supplier<Screen> opens = null;
      /**
       * <p>Further variation of {@link #opens(Supplier)}; instead opens the {@link BaseWildfireScreen#parent parent} of
       * the provided {@link BaseWildfireScreen} when clicked.</p>
       *
       * <p>This will also attach a {@link #narrationSupplier} if you don't already have one set, which simply reads
       * (in English) Done button.</p>
       *
       * @apiNote There should only ever be one close button on a given screen;
       *          {@link com.wildfire.gui.screen.DynamicallySizedScreen DynamicallySizedScreen} in particular will
       *          throw an error if more than one close button is discovered on a screen when repositioning elements.
       *
       * @see WildfireButton#setCloseButton(boolean)
       */
      private @Nullable BaseWildfireScreen closes = null;

      // Button behavior
      /**
       * @see WildfireButton#setTextScrollable(boolean)
       */
      private boolean scrollableText = true;
      /**
       * @see ButtonWidget#active
       */
      private boolean active = true;
      /**
       * @see WildfireButton#setDeselected(boolean)
       */
      private boolean deselected = false;
      /**
       * @see WildfireButton#setHighlightBackground(boolean)
       */
      private boolean highlightBackground = false;

      public WildfireButton build() {
         final WildfireButton button;
         final PressAction onClick;
         NarrationSupplier narrationSupplier = this.narrationSupplier;
         if(narrationSupplier == null) {
            narrationSupplier = closes == null
                    ? DEFAULT_NARRATION_SUPPLIER
                    : (narration -> Text.translatable("gui.narrate.button", Text.translatable("gui.done")));
         }

         if(this.closes != null) {
            onClick = (ignored) -> MinecraftClient.getInstance().setScreen(closes.parent);
         } else if(this.opens != null) {
            onClick = (ignored) -> MinecraftClient.getInstance().setScreen(opens.get());
         } else {
            onClick = Objects.requireNonNull(this.onClick);
         }

         if(this.textSupplier != null) {
            button = new WildfireButton(x, y, width, height, textSupplier, onClick, narrationSupplier);
         } else if(this.text != null) {
            button = new WildfireButton(x, y, width, height, text, onClick, narrationSupplier);
         } else {
            throw new IllegalStateException("Required one of either textSupplier or text, but neither were set");
         }

         button.active = active;
         button.setTooltip(tooltip);

         return button.setCloseButton(closes != null)
                 .setTextScrollable(scrollableText)
                 .setDeselected(deselected)
                 .setHighlightBackground(highlightBackground);
      }
   }
}