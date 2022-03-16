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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class WildfireSlider extends ClickableWidget {
	protected double value;
	protected double minVal, maxVal;

	public boolean dragging = false;

	public WildfireSlider(int x, int y, int width, int height, Text text, double minVal, double maxVal, double value) {
		super(x, y, width, height, text);
		this.value = (value - minVal) / (maxVal - minVal);
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	protected int getYImage(boolean hovered) {
		return 0;
	}

	protected MutableText getNarrationMessage() {
		return new TranslatableText("gui.narrate.slider", new Object[]{this.getMessage()});
	}


	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			if(this.visible) {
				int clr = 84 << 24;

				int xP = x +4;
				Screen.fill(matrices, xP-2, y+1, x + this.width - 1, y + this.height-1, 0x222222 + (128 << 24));
				int xPos =  x + 4 + (int) (this.value * (float)(this.width - 6));
				Screen.fill(matrices, x+3, y+2, xPos-1, y + this.height - 2, 0x222266 + (180 << 24));

				int xPos2 = this.x + 2 + (int) (this.value * (float)(this.width - 4));
				Screen.fill(matrices,xPos2-2, y + 1, xPos2, y + this.height-1, 0xFFFFFF + (120 << 24));
			}
		}
	}
	protected void renderBg(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
	}

	public void onClick(double mouseX, double mouseY) {
		this.setValueFromMouse(mouseX);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean bl = keyCode == 263;
		if (bl || keyCode == 262) {
			float f = bl ? -1.0F : 1.0F;
			this.setValue(this.value + (double)(f / (float)(this.width - 8)));
		}

		return false;
	}

	private void setValueFromMouse(double mouseX) {
		this.value = ((mouseX - (double)(this.x + 4)) / (double)(this.width - 8));
		if (this.value < 0.0F) {
			this.value = 0.0F;
		}

		if (this.value > 1.0F) {
			this.value = 1.0F;
		}
	}
	public int getValueInt()
	{
		return (int)Math.round(value * (maxVal - minVal) + minVal);
	}

	public double getValue()
	{
		return value * (maxVal - minVal) + minVal;
	}

	public void setValue(double d) {
		this.value = (d - minVal) / (maxVal - minVal);
		if (this.value < 0.0F) {
			this.value = 0.0F;
		}

		if (this.value > 1.0F) {
			this.value = 1.0F;
		}
	}


	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.setValueFromMouse(mouseX);
		this.dragging = true;
		super.onDrag(mouseX, mouseY, deltaX, deltaY);
	}

	public void playDownSound(SoundManager soundManager) {
	}

	public void onRelease(double mouseX, double mouseY) {
		this.dragging = false;
		super.playDownSound(MinecraftClient.getInstance().getSoundManager());
	}

	protected abstract void updateMessage();

	protected abstract void applyValue();
}
