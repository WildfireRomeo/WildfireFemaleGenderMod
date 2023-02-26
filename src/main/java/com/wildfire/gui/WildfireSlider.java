/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022 WildfireRomeo

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
import com.wildfire.main.config.FloatConfigKey;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class WildfireSlider extends ClickableWidget {

	private double value;
	private final double minValue;
	private final double maxValue;
	private final FloatConsumer valueUpdate;
	private final Float2ObjectFunction<Text> messageUpdate;
	private final FloatConsumer onSave;

	private float lastValue;
	private boolean changed;

	public WildfireSlider(int xPos, int yPos, int width, int height, FloatConfigKey config, double currentVal, FloatConsumer valueUpdate,
		Float2ObjectFunction<Text> messageUpdate, FloatConsumer onSave) {
		this(xPos, yPos, width, height, config.getMinInclusive(), config.getMaxInclusive(), currentVal, valueUpdate, messageUpdate, onSave);
	}

	public WildfireSlider(int xPos, int yPos, int width, int height, double minVal, double maxVal, double currentVal, FloatConsumer valueUpdate,
		Float2ObjectFunction<Text> messageUpdate, FloatConsumer onSave) {
		super(xPos, yPos, width, height, Text.empty());
		this.minValue = minVal;
		this.maxValue = maxVal;
		this.valueUpdate = valueUpdate;
		this.messageUpdate = messageUpdate;
		this.onSave = onSave;
		setValueInternal(currentVal);
	}

	@Override
	protected int getYImage(boolean hovered) {
		return 0;
	}

	protected void updateMessage() {
		setMessage(messageUpdate.get(lastValue));
	}

	protected void applyValue() {
		float newValue = getFloatValue();
		if (lastValue != newValue) {
			valueUpdate.accept(newValue);
			lastValue = newValue;
			changed = true;
		}
	}

	public void save() {
		if (changed) {
			onSave.accept(lastValue);
			changed = false;
		}
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		save();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean result = super.keyPressed(keyCode, scanCode, modifiers);
		if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) {
			save();
		}
		return result;
	}

	protected MutableText getNarrationMessage() {
		return Text.translatable("gui.narrate.slider", new Object[]{this.getMessage()});
	}


	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			RenderSystem.disableDepthTest();
			this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
			int clr = 84 << 24;

			int xP = getX() +4;
			Screen.fill(matrices, xP-2, getY()+1, getX() + this.width - 1, getY() + this.height-1, 0x222222 + (128 << 24));
			int xPos =  getX() + 4 + (int) (this.value * (float)(this.width - 6));
			Screen.fill(matrices, getX()+3, getY()+2, xPos-1, getY() + this.height - 2, 0x222266 + (180 << 24));

			int xPos2 = this.getX() + 2 + (int) (this.value * (float)(this.width - 4));
			Screen.fill(matrices,xPos2-2, getY() + 1, xPos2, getY() + this.height-1, 0xFFFFFF + (120 << 24));
			RenderSystem.enableDepthTest();
			TextRenderer font = MinecraftClient.getInstance().textRenderer;
			drawCenteredText(matrices, font, getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, this.hovered || changed ? 0xFFFF55 : 0xFFFFFF);
		}
	}

	public float getFloatValue() {
		return (float) getValue();
	}

	public double getValue() {
		return this.value * (maxValue - minValue) + minValue;
	}

	public void setValue(double value) {
		setValueInternal(value);
		applyValue();
	}

	private void setValueInternal(double value) {
		this.value = MathHelper.clamp((value - this.minValue) / (this.maxValue - this.minValue), 0, 1);
		this.lastValue = (float) value;
		updateMessage();
		//Note: Does not call applyValue
	}

	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.setValueFromMouse(mouseX);
		super.onDrag(mouseX, mouseY, deltaX, deltaY);
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {}


	private void setValueFromMouse(double mouseX) {
		this.value = ((mouseX - (double)(this.getX() + 4)) / (double)(this.getWidth() - 8));
		if (this.value < 0.0F) {
			this.value = 0.0F;
		}

		if (this.value > 1.0F) {
			this.value = 1.0F;
		}
		applyValue();
		updateMessage();
	}
}
