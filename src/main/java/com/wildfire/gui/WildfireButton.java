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
public class WildfireButton extends ButtonWidget {

   private final @Nullable Supplier<Text> textSupplier;

   private @Getter @Setter boolean transparent = false;
   private @Getter @Setter boolean closeButton = false;
   private @Getter @Setter boolean textScrollable = true;

   private WildfireButton(int x, int y, int w, int h, Text text, ButtonWidget.PressAction onPress, @NotNull NarrationSupplier narrationSupplier) {
      super(x, y, w, h, text, onPress, narrationSupplier);
      this.textSupplier = null;
   }

   private WildfireButton(int x, int y, int w, int h, Supplier<Text> textSupplier, PressAction onPress, @NotNull NarrationSupplier narrationSupplier) {
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
      if(isSelected()) clr = 0x666666 + (84 << 24);
      if(!active) clr = 0x222222 + (84 << 24);
      if(!transparent) ctx.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), clr);

      int textColor = active ? 0xFFFFFF : 0x666666;
      int i = this.getX() + 2;
      int j = this.getX() + this.getWidth() - 2;

      Text message = getMessage();
      if(!this.active) {
         message = GuiUtils.removeTextFormatting(message);
      }

      if(isTextScrollable()) {
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
      private int x = 0, y = 0; // defaults to 0 to explicitly support DynamicallySizedScreen not requiring this to be set
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
       * Optional {@link Text} supplier for the narrator to read instead of the provided {@link #text}.
       * If not set, {@link #DEFAULT_NARRATION_SUPPLIER} is used instead.
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
       * Variation of {@link #onClick}; opens the provided {@link Screen} when clicked.
       */
      private @Nullable Supplier<Screen> opens = null;
      /**
       * Variation of {@link #onClick}; opens the {@link BaseWildfireScreen#parent parent} of the current screen
       * when clicked.<br>
       *
       * If set, the built button will also be marked as {@link WildfireButton#isCloseButton() a close button}, which
       * is used by {@link com.wildfire.gui.screen.DynamicallySizedScreen DynamicallySizedScreen} to place it
       * at the top of the rendered UI.<br>
       *
       * This will also attach a {@link #narrationSupplier} if you don't already have one set.
       *
       * @apiNote There should only ever be one close button on a given screen; {@link com.wildfire.gui.screen.DynamicallySizedScreen DynamicallySizedScreen}
       *          in particular will throw an error if more than one close button is discovered on a screen when repositioning elements.
       */
      private @Nullable BaseWildfireScreen closes = null;

      // Button behavior
      /**
       * If {@code false}, the built button will not have its text scroll; this is designed for single-character labels
       * on small buttons, where it doesn't really matter if the text slightly spills out of bounds.
       */
      private boolean scrollableText = true;
      /**
       * Sets {@link ButtonWidget#active} on the built button
       */
      private boolean active = true;

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
            throw new IllegalStateException("neither #text(Supplier<Text>) nor #text(Text) were called!");
         }

         button.active = active;
         button.setTooltip(tooltip);
         button.setCloseButton(closes != null);
         button.setTextScrollable(scrollableText);
         return button;
      }
   }
}