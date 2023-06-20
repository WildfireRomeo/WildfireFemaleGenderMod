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

import com.wildfire.main.GenderPlayer.Gender;
import com.wildfire.main.WildfireGender;
import java.util.UUID;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.GenderPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.NotNull;

public class WardrobeBrowserScreen extends BaseWildfireScreen {

	private ResourceLocation BACKGROUND;
	public static float modelRotation = 0.5F;

	public WardrobeBrowserScreen(Screen parent, UUID uuid) {
		super(Component.translatable("wildfire_gender.wardrobe.title"), parent, uuid);
	}

	@Override
  	public void init() {
	    int j = this.height / 2;

		GenderPlayer plr = getPlayer();

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 52, 158, 20, getGenderLabel(plr.getGender()), button -> {
			Gender gender = switch (plr.getGender()) {
				case MALE -> Gender.FEMALE;
				case FEMALE -> Gender.OTHER;
				case OTHER -> Gender.MALE;
			};
			if (plr.updateGender(gender)) {
				button.setMessage(getGenderLabel(gender));
				GenderPlayer.saveGenderInfo(plr);
			}
		}));

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 32, 158, 20, Component.translatable("wildfire_gender.appearance_settings.title").append("..."),
			button -> Minecraft.getInstance().setScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, this.playerUUID))));

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 12, 158, 20, Component.translatable("wildfire_gender.char_settings.title").append("..."),
			button -> Minecraft.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID))));

		this.addRenderableWidget(new WildfireButton(this.width / 2 + 111, j - 63, 9, 9, Component.translatable("wildfire_gender.label.exit"),
			button -> Minecraft.getInstance().setScreen(parent)));
	    
	    modelRotation = 0.6F;

	    this.BACKGROUND = new ResourceLocation(WildfireGender.MODID, "textures/gui/wardrobe_bg.png");
    
	    super.init();
  	}

	private Component getGenderLabel(Gender gender) {
		return Component.translatable("wildfire_gender.label.gender").append(" - ").append(gender.getDisplayName());
	}

  	@Override
	public void render(@NotNull GuiGraphics graphics, int f1, int f2, float f3) {
		Minecraft minecraft = Minecraft.getInstance();
		GenderPlayer plr = getPlayer();
	    super.renderBackground(graphics);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

	    int i = (this.width - 248) / 2;
	    int j = (this.height - 134) / 2;
		graphics.blit(BACKGROUND, i, j, 0, 0, 248, 156);

	    if(plr == null) return;


		int x = this.width / 2;
	    int y = this.height / 2;

		graphics.drawString(this.font, title, x - 42, y - 62, 4473924, false);

	    modelRotation = 0.6f;


		try {
			RenderSystem.setShaderColor(1f, 1.0F, 1.0F, 1.0F);
		    int xP = this.width / 2 - 82;
		    int yP = this.height / 2 + 32;
		    Player ent = minecraft.level == null ? null : minecraft.level.getPlayerByUUID(this.playerUUID);
		    if (ent != null) {
				InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, xP, yP, 45, xP - f1, j + 75 - 40 - f2, ent);
		    } else {
				//player left, fallback
				minecraft.setScreen(new WildfirePlayerListScreen());
		    }
		} catch(Exception e) {
			//error, fallback
			minecraft.setScreen(new WildfirePlayerListScreen());
		}

	    super.render(graphics, f1, f2, f3);
	}
}