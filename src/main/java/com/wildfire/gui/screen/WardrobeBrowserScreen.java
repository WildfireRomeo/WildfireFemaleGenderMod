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

package com.wildfire.gui.screen;

import com.wildfire.main.Gender;
import com.wildfire.main.WildfireGender;
import java.util.Calendar;
import java.util.UUID;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.entitydata.PlayerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.NotNull;

public class WardrobeBrowserScreen extends BaseWildfireScreen {

	private static final ResourceLocation BACKGROUND_FEMALE = WildfireGender.rl("textures/gui/wardrobe_bg2.png");
	private static final ResourceLocation BACKGROUND = WildfireGender.rl("textures/gui/wardrobe_bg3.png");
	private static final ResourceLocation TXTR_RIBBON = WildfireGender.rl("textures/bc_ribbon.png");

	private static final UUID CREATOR_UUID = UUID.fromString("33c937ae-6bfc-423e-a38e-3a613e7c1256");

	private final boolean isBreastCancerAwarenessMonth = Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER;

	public WardrobeBrowserScreen(UUID uuid) {
		super(Component.translatable("wildfire_gender.wardrobe.title"), null, uuid);
	}

	@Override
  	public void init() {
	    int y = this.height / 2;

		PlayerConfig plr = getPlayer();

		int buttonX = this.width / 2 - 42;
		this.addRenderableWidget(new WildfireButton(buttonX, y - 52, 158, 20, getGenderLabel(plr.getGender()), button -> {
			Gender gender = switch (plr.getGender()) {
				case MALE -> Gender.FEMALE;
				case FEMALE -> Gender.OTHER;
				case OTHER -> Gender.MALE;
			};
			if (plr.updateGender(gender)) {
				button.setMessage(getGenderLabel(gender));
				PlayerConfig.saveGenderInfo(plr);
				rebuildWidgets();
			}
		}));

		int yOffset = 32;
		if (plr.getGender().canHaveBreasts()) {
			this.addRenderableWidget(new WildfireButton(buttonX, y - yOffset, 158, 20, Component.translatable("wildfire_gender.appearance_settings.title").append("..."),
				button -> Minecraft.getInstance().setScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, this.playerUUID))));
			yOffset -= 20;
		}
		this.addRenderableWidget(new WildfireButton(buttonX, y - yOffset, 158, 20, Component.translatable("wildfire_gender.char_settings.title").append("..."),
			button -> Minecraft.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID))));

		this.addRenderableWidget(new WildfireButton(this.width / 2 + 111, y - 63, 9, 9, Component.literal("X"),
			button -> Minecraft.getInstance().setScreen(parent)));
    
	    super.init();
  	}

	private Component getGenderLabel(Gender gender) {
		return Component.translatable("wildfire_gender.label.gender").append(" - ").append(gender.getDisplayName());
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(graphics, mouseX, mouseY, partialTick);
		ResourceLocation backgroundTexture = getPlayer().getGender().canHaveBreasts() ? BACKGROUND_FEMALE : BACKGROUND;
		graphics.blit(backgroundTexture, (this.width - 248) / 2, (this.height - 134) / 2, 0, 0, 248, 156);
	}

  	@Override
	public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);

		int x = this.width / 2;
	    int y = this.height / 2;

		graphics.drawString(this.font, title, x - 118, y - 62, 4473924, false);
		RenderSystem.setShaderColor(1f, 1.0F, 1.0F, 1.0F);
		if (minecraft != null && minecraft.level != null) {
			Player ent = minecraft.level.getPlayerByUUID(this.playerUUID);
			if (ent != null) {
				InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x - 164, y - 80, x, y + 80, 45, 0, mouseX, mouseY, ent);
			}
		}

		y = y - 45;
		if (isBreastCancerAwarenessMonth) {
			graphics.fill(x - 159, y + 106, x + 159, y + 136, 0x55000000);
			graphics.drawString(font, Component.translatable("wildfire_gender.cancer_awareness.title").withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC), this.width / 2 - 148, y + 117, 0xFFFFFF, false);
			graphics.blit(TXTR_RIBBON, x + 130, y + 109, 26, 26, 0, 0, 20, 20, 20, 20);
			y += 55;
		}

		if (minecraft != null && minecraft.player != null && minecraft.player.connection.getListedOnlinePlayers().stream()
			.anyMatch(player -> player.getProfile().getId().equals(CREATOR_UUID))) {
			graphics.drawCenteredString(font, Component.translatable("wildfire_gender.label.with_creator"), this.width / 2, y + 89, 0xFF00FF);
		}
	}
}