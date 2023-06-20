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

package com.wildfire.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfirePlayerList;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.GenderPlayer.Gender;
import com.wildfire.main.WildfireGender;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Calendar;
import java.util.UUID;


public class WildfirePlayerListScreen extends Screen {

	private static final UUID CREATOR_UUID = UUID.fromString("33c937ae-6bfc-423e-a38e-3a613e7c1256");
	private ResourceLocation TXTR_BACKGROUND;
	private static final ResourceLocation TXTR_RIBBON = new ResourceLocation(WildfireGender.MODID, "textures/bc_ribbon.png");

	@Nullable
	private Component tooltip = null;

 	public static GenderPlayer HOVER_PLAYER;

	WildfirePlayerList PLAYER_LIST;

	public WildfirePlayerListScreen() {
		super(Component.translatable("wildfire_gender.player_list.title"));
	}

	@Override
	public boolean isPauseScreen() { return false; }

	@Override
  	public void init() {

	    int x = this.width / 2;
	    int y = this.height / 2 - 20;

		this.addWidget(new WildfireButton(this.width / 2 + 53, y - 74, 9, 9, Component.translatable("wildfire_gender.label.exit"), button -> Minecraft.getInstance().setScreen(null)));

	    PLAYER_LIST = new WildfirePlayerList(this, 118, (y - 61), (y + 71));
		PLAYER_LIST.setRenderBackground(false);
		PLAYER_LIST.setRenderTopAndBottom(false);
	    this.addRenderableWidget(this.PLAYER_LIST);

	    this.TXTR_BACKGROUND = new ResourceLocation(WildfireGender.MODID, "textures/gui/player_list.png");

	    super.init();
  	}

	@Override
	public void render(@Nonnull GuiGraphics graphics, int f1, int f2, float f3) {
		HOVER_PLAYER = null;
		this.setTooltip(null);
		PLAYER_LIST.refreshList();


	    super.renderBackground(graphics);
		Minecraft mc = Minecraft.getInstance();
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    if(this.TXTR_BACKGROUND != null) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int i = (this.width - 132) / 2;
			int j = (this.height - 156) / 2 - 20;
			graphics.blit(TXTR_BACKGROUND, i, j, 0, 0, 192, 174);
		}

	    int x = (this.width / 2);
	    int y = (this.height / 2) - 20;

	    super.render(graphics, f1, f2, f3);

        double scale = mc.getWindow().getGuiScale();
        int left = x - 59;
        int bottom = y - 32;
        int width = 118;
        int height = 134;
        RenderSystem.enableScissor((int)(left  * scale), (int) (bottom * scale),
				(int)(width * scale), (int) (height * scale));

	    PLAYER_LIST.render(graphics, f1, f2, f3);
	  	RenderSystem.disableScissor();

	    if(HOVER_PLAYER != null) {
			int dialogX = x + 75;
			int dialogY = y - 73;
			Player pEntity = mc.level == null ? null : mc.level.getPlayerByUUID(HOVER_PLAYER.uuid);
			if (pEntity != null) {
				graphics.drawString(this.font, pEntity.getDisplayName().copy().withStyle(ChatFormatting.UNDERLINE), dialogX, dialogY - 2, 0xFFFFFF);
			}

			Gender gender = HOVER_PLAYER.getGender();
			graphics.drawString(this.font, Component.translatable("wildfire_gender.label.gender").append(" ").append(gender.getDisplayName()), dialogX, dialogY + 10, 0xBBBBBB);
			if (gender.canHaveBreasts()) {
				graphics.drawString(this.font, Component.translatable("wildfire_gender.wardrobe.slider.breast_size", Math.round(HOVER_PLAYER.getBustSize() * 100)), dialogX, dialogY + 20, 0xBBBBBB);
				graphics.drawString(this.font, Component.translatable("wildfire_gender.char_settings.physics", Component.translatable(HOVER_PLAYER.hasBreastPhysics() ? "wildfire_gender.label.enabled" : "wildfire_gender.label.disabled")), dialogX, dialogY + 40, 0xBBBBBB);
				graphics.drawString(this.font, Component.translatable("wildfire_gender.player_list.bounce_multiplier", HOVER_PLAYER.getBounceMultiplier()), dialogX + 6, dialogY + 50, 0xBBBBBB);
				graphics.drawString(this.font, Component.translatable("wildfire_gender.player_list.breast_momentum", Math.round(HOVER_PLAYER.getFloppiness() * 100)), dialogX + 6, dialogY + 60, 0xBBBBBB);

				graphics.drawString(this.font, Component.translatable("wildfire_gender.player_list.female_sounds", Component.translatable(HOVER_PLAYER.hasHurtSounds() ? "wildfire_gender.label.enabled" : "wildfire_gender.label.disabled")), dialogX, dialogY + 80, 0xBBBBBB);
			}
			if(pEntity != null) {
				InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x - 110, y + 45, 45, (x - 300), (y - 26 - f2), pEntity);
			}
		}

		graphics.drawString(this.font, Component.translatable("wildfire_gender.player_list.title"), x - 60, y - 73, 4473924, false);

		boolean withCreator = false;
		ClientPacketListener connection = mc.getConnection();
		if (connection != null) {
			withCreator = connection.getOnlinePlayers().stream().anyMatch(loadedPlayer -> loadedPlayer.getProfile().getId().equals(CREATOR_UUID));
		}

		if (withCreator) {
			graphics.drawCenteredString(this.font, Component.translatable("wildfire_gender.label.with_creator"), this.width / 2, y + 89, 0xFF00FF);
		}

		//Breast Cancer Awareness Month Notification
		if(Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER) {
			graphics.fill(x - 159, y + 106, x + 159, y + 136, 0x55000000);
			graphics.drawString(this.font, Component.translatable("wildfire_gender.cancer_awareness.title").withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC), this.width / 2 - 148, y + 117, 0xFFFFFF, false);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			graphics.blit(TXTR_RIBBON, x + 130, y + 109, 26, 26, 0, 0, 20, 20, 20, 20);
		}
		if (tooltip != null) {
			graphics.renderTooltip(this.font, tooltip, f1, f2);
		}
  	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int x = (this.width / 2);
		int y = (this.height / 2) - 20;

		/*if(mouseX > x - 159 && mouseY > y + 106 && mouseX < x + 159 && mouseY < y + 136) {
			this.client.openScreen(new ConfirmChatLinkScreen((bool) -> {
				if (bool) {
					Util.getOperatingSystem().open("https://www.komen.org/how-to-help/donate/");
				}
				this.client.openScreen(this);
			}, "https://www.komen.org/how-to-help/donate/", true));
		}*/
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public void setTooltip(@Nullable Component tooltip) {
  		this.tooltip = tooltip;
	}

    @Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		return super.keyPressed(keyCode, scanCode, modifiers);
  	}

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {

	    return super.mouseReleased(mouseX, mouseY, state);
  	}
}