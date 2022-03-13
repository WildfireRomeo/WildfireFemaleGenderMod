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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.Slider;

public class WildfireSlider extends Slider {

	public WildfireSlider(int xPos, int yPos, int width, int height, Component prefix, Component suf,
					   double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, OnPress handler, ISlider iSlider) {
		super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler);

	}
	@Override
	protected void renderBg(PoseStack mStack, Minecraft par1Minecraft, int par2, int par3) {
		if (this.visible) {
			if (this.dragging){
				this.sliderValue = (par2 - (this.x + 4)) / (float)(this.width - 8);
				updateSlider();
			}

		}
	}

	@Override
	public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partial)
	{
		this.renderBg(mStack, Minecraft.getInstance(), mouseX, mouseY);
		RenderSystem.disableDepthTest();
		if (this.visible) {
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			if(this.visible) {
				int clr = 0x000000 + (84 << 24);
				fill(mStack, x, y, x + getWidth(), y + height, clr);

				Screen.fill(mStack, x+1, y+1, x + this.width - 1, y + this.height-1, 0x222222 + (128 << 24));

				//Inner Blue Filler
				int xPos = this.x + 4 + (int) (((this.getValue() - this.minValue) * (float)(this.width - 6)) / (this.maxValue - this.minValue));
				Screen.fill(mStack, x+2, y+2, xPos-1, y + this.height - 2, 0x222266 + (180 << 24));

				int xPos2 = this.x + 2 + (int) (((this.getValue() - this.minValue) * (float)(this.width - 4)) / (this.maxValue - this.minValue));
				Screen.fill(mStack,xPos2-2, y + 1, xPos2, y + this.height-1, 0xFFFFFF + (120 << 24));
			}
		}
		RenderSystem.enableDepthTest();
	}


}
