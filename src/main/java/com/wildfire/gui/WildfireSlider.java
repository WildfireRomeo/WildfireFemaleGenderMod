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
import com.wildfire.main.config.FloatConfigKey;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class WildfireSlider extends AbstractSliderButton {

	private final double minValue;
	private final double maxValue;
	private final FloatConsumer valueUpdate;
	private final Float2ObjectFunction<Component> messageUpdate;
	private final FloatConsumer onSave;

	private float lastValue;
	private boolean changed;

	public WildfireSlider(int xPos, int yPos, int width, int height, FloatConfigKey config, double currentVal, FloatConsumer valueUpdate,
		Float2ObjectFunction<Component> messageUpdate, FloatConsumer onSave) {
		this(xPos, yPos, width, height, config.getMinInclusive(), config.getMaxInclusive(), currentVal, valueUpdate, messageUpdate, onSave);
	}

	public WildfireSlider(int xPos, int yPos, int width, int height, double minVal, double maxVal, double currentVal, FloatConsumer valueUpdate,
		Float2ObjectFunction<Component> messageUpdate, FloatConsumer onSave) {
		super(xPos, yPos, width, height, TextComponent.EMPTY, 0);
		this.minValue = minVal;
		this.maxValue = maxVal;
		this.valueUpdate = valueUpdate;
		this.messageUpdate = messageUpdate;
		this.onSave = onSave;
		setValueInternal(currentVal);
	}

	@Override
	protected void updateMessage() {
		setMessage(messageUpdate.get(lastValue));
	}

	@Override
	protected void applyValue() {
		float newValue = getFloatValue();
		if (lastValue != newValue) {
			valueUpdate.accept(newValue);
			lastValue = newValue;
			changed = true;
		}
	}

	private void save() {
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

	@Override
	public void renderButton(@Nonnull PoseStack mStack, int mouseX, int mouseY, float partial) {
		RenderSystem.disableDepthTest();
		int clr = 84 << 24;
		fill(mStack, x, y, x + getWidth(), y + height, clr);

		fill(mStack, x + 1, y + 1, x + this.width - 1, y + this.height - 1, 0x222222 + (128 << 24));

		//Inner Blue Filler
		int xPos = this.x + 4 + (int) (value * (this.width - 6));
		fill(mStack, x + 2, y + 2, xPos - 1, y + this.height - 2, 0x222266 + (180 << 24));

		int xPos2 = this.x + 2 + (int) (value * (this.width - 4));
		fill(mStack,xPos2-2, y + 1, xPos2, y + this.height-1, 0xFFFFFF + (120 << 24));
		RenderSystem.enableDepthTest();

		Font font = Minecraft.getInstance().font;
		drawCenteredString(mStack, font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, isHovered || changed ? 0xFFFF55 : 0xFFFFFF);
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
		this.value = Mth.clamp((value - this.minValue) / (this.maxValue - this.minValue), 0, 1);
		this.lastValue = (float) value;
		updateMessage();
		//Note: Does not call applyValue
	}
}
