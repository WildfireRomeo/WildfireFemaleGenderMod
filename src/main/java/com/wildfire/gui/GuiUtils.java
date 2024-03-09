package com.wildfire.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public final class GuiUtils {
	private GuiUtils() {
		throw new AssertionError("GuiUtils should only be used in a static context");
	}

	@Environment(EnvType.CLIENT)
	public static void drawCenteredText(DrawContext ctx, TextRenderer textRenderer, Text text, int x, int y, int color) {
		int centeredX = x - textRenderer.getWidth(text) / 2;
		ctx.drawText(textRenderer, text, centeredX, y, color, false);
	}

	@Environment(EnvType.CLIENT)
	public static void drawScrollableTextWithoutShadow(DrawContext context, TextRenderer textRenderer, Text text, int left, int top, int right, int bottom, int color) {
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

	public static Text removeTextFormatting(Text original) {
		return Texts.join(
				original.copy().withoutStyle().stream()
						.map(x -> x.copy().formatted(Formatting.RESET))
						.toList(),
				Text.empty());
	}
}
