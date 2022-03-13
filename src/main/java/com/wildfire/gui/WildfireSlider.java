package com.wildfire.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
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
