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

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfirePlayerList;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.GenderPlayer.Gender;
import com.wildfire.main.WildfireGender;

import com.wildfire.main.WildfireHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.UUID;

public class WildfirePlayerListScreen extends Screen {
	private static final UUID CREATOR_UUID = UUID.fromString("33c937ae-6bfc-423e-a38e-3a613e7c1256");
	private static final Identifier TXTR_BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/player_list.png");
	private static final Identifier TXTR_RIBBON = new Identifier(WildfireGender.MODID, "textures/bc_ribbon.png");

	private @Nullable Text tooltip;
	public static GenderPlayer HOVER_PLAYER;
	private WildfirePlayerList PLAYER_LIST;
	private final MinecraftClient client;

	public WildfirePlayerListScreen(MinecraftClient mc) {
		super(Text.translatable("wildfire_gender.player_list.title"));
		this.client = mc;
	}

	@Override
	public void removed() {
		super.removed();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public void init() {
		int y = this.height / 2 - 20;

		this.addDrawableChild(new WildfireButton(this.width / 2 + 53, y - 74, 9, 9, Text.translatable("wildfire_gender.label.exit"), button -> MinecraftClient.getInstance().setScreen(null)));

		PLAYER_LIST = new WildfirePlayerList(this, 118, (y - 61), (y + 71));
		PLAYER_LIST.setRenderBackground(false);
		PLAYER_LIST.setRenderHorizontalShadows(false);
		this.addSelectableChild(this.PLAYER_LIST);

	    super.init();
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		TextRenderer font = client.textRenderer;
		HOVER_PLAYER = null;
		PLAYER_LIST.refreshList();

		super.renderBackground(ctx);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = (this.width - 132) / 2;
		int j = (this.height - 156) / 2 - 20;
		ctx.drawTexture(TXTR_BACKGROUND, i, j, 0, 0, 192, 174);

		int x = (this.width / 2);
		int y = (this.height / 2) - 20;

		super.render(ctx, mouseX, mouseY, delta);

		double scale = client.getWindow().getScaleFactor();
		int left = x - 59;
		int bottom = y - 32;
		int width = 118;
		int height = 134;
		RenderSystem.enableScissor((int)(left  * scale), (int) (bottom * scale),
				(int)(width * scale), (int) (height * scale));

		PLAYER_LIST.render(ctx, mouseX, mouseY, delta);
		RenderSystem.disableScissor();

		if(HOVER_PLAYER != null) {
			int dialogX = x + 75;
			int dialogY = y - 73;
			PlayerEntity pEntity = client.world.getPlayerByUuid(HOVER_PLAYER.uuid);
			if(pEntity != null) {
				ctx.drawTextWithShadow(font, pEntity.getDisplayName().copy().formatted(Formatting.UNDERLINE), dialogX, dialogY - 2, 0xFFFFFF);
			}

			Gender gender = HOVER_PLAYER.getGender();
			ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.label.gender").append(" ").append(gender.getDisplayName()), dialogX, dialogY + 10, 0xBBBBBB);
			if (gender.canHaveBreasts()) {
				ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.wardrobe.slider.breast_size", Math.round(HOVER_PLAYER.getBustSize() * 100)), dialogX, dialogY + 20, 0xBBBBBB);
				ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.char_settings.physics", Text.translatable(HOVER_PLAYER.hasBreastPhysics() ? "wildfire_gender.label.enabled" : "wildfire_gender.label.disabled")), dialogX, dialogY + 40, 0xBBBBBB);
				ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.player_list.bounce_multiplier", HOVER_PLAYER.getBounceMultiplier()), dialogX + 6, dialogY + 50, 0xBBBBBB);
				ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.player_list.breast_momentum", Math.round(HOVER_PLAYER.getFloppiness() * 100)), dialogX + 6, dialogY + 60, 0xBBBBBB);

				ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.player_list.female_sounds", Text.translatable(HOVER_PLAYER.hasHurtSounds() ? "wildfire_gender.label.enabled" : "wildfire_gender.label.disabled")), dialogX, dialogY + 80, 0xBBBBBB);
			}
			if(pEntity != null) {
				WardrobeBrowserScreen.drawEntityOnScreen(x - 110, y + 45, 45, (x - mouseX - 110), (y - 26 - mouseY), pEntity);
			}
		}

		ctx.drawText(font, Text.translatable("wildfire_gender.player_list.title"), x - 60, y - 73, 4473924, false);

		ClientPlayNetworkHandler clientPlayNetworkHandler = client.player.networkHandler;
		boolean withCreator = clientPlayNetworkHandler.getPlayerList().stream()
				.anyMatch((player) -> player.getProfile().getId().equals(CREATOR_UUID));
		if(withCreator) {
			WildfireHelper.drawCenteredText(ctx, this.textRenderer, Text.translatable("wildfire_gender.label.with_creator"), this.width / 2, y + 89, 0xFF00FF);
		}

		//Breast Cancer Awareness Month Notification
		if(Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER) {
			ctx.fill(x - 159, y + 106, x + 159, y + 136, 0x55000000);
			ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.cancer_awareness.title").formatted(Formatting.BOLD, Formatting.ITALIC), this.width / 2 - 148, y + 117, 0xFFFFFF);
			RenderSystem.setShader(GameRenderer::getPositionTexProgram);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			ctx.drawTexture(TXTR_RIBBON, x + 130, y + 109, 26, 26, 0, 0, 20, 20, 20, 20);
		}
		if(tooltip != null) {
			ctx.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
		}
	}

	public void setTooltip(@Nullable Text tooltip) {
		this.tooltip = tooltip;
	}
}
