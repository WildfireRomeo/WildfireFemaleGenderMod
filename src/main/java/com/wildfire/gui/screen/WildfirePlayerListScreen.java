package com.wildfire.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.WildfireButton;
import com.wildfire.gui.WildfirePlayerList;
import com.wildfire.main.GenderPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.minecraft.client.gui.screen.Screen;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;


public class WildfirePlayerListScreen extends Screen {
	

	private Identifier TXTR_BACKGROUND;
	private static final Identifier TXTR_RIBBON = new Identifier("wildfire_gender", "textures/bc_ribbon.png");

	private String tooltip = "";

 	public static GenderPlayer HOVER_PLAYER;

	WildfirePlayerList PLAYER_LIST;
	private MinecraftClient client;
	public WildfirePlayerListScreen(MinecraftClient mc) {
		super(new TranslatableText("wildfire_gender.player_list.title"));
		this.client = mc;
		MinecraftClient.getInstance().keyboard.setRepeatEvents(true);
	}
  
	public void removed() {
		MinecraftClient.getInstance().keyboard.setRepeatEvents(false);
		super.removed();
	}
	
	public boolean shouldPause() { return false; }

  
  	public void init() {
	  	MinecraftClient mc = MinecraftClient.getInstance();

	    int x = this.width / 2;
	    int y = this.height / 2 - 20;

		this.addDrawableChild(new WildfireButton(this.width / 2 + 53, y - 74, 9, 9, new LiteralText("X"), button -> {
			MinecraftClient.getInstance().setScreen(null);
		}));

	    PLAYER_LIST = new WildfirePlayerList(this, 118, (y - 61), (y + 71));
		PLAYER_LIST.setRenderBackground(false);
		PLAYER_LIST.setRenderHorizontalShadows(false);
	    this.addSelectableChild(this.PLAYER_LIST);
		
	    this.TXTR_BACKGROUND = new Identifier("wildfire_gender", "textures/gui/player_list.png");
    
	    super.init();
  	}

	public void render(MatrixStack m, int f1, int f2, float f3) {
		HOVER_PLAYER = null;
		this.setTooltip("");
		PLAYER_LIST.refreshList();


	    super.renderBackground(m);
		MinecraftClient mc = MinecraftClient.getInstance();
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    if(this.TXTR_BACKGROUND != null) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, this.TXTR_BACKGROUND);
		}
	    int i = (this.width - 132) / 2;
	    int j = (this.height - 156) / 2 - 20;
	    drawTexture(m, i, j, 0, 0, 192, 174);
    
	    int x = (this.width / 2);
	    int y = (this.height / 2) - 20;
	    
	    super.render(m, f1, f2, f3);

        double scale = mc.getWindow().getScaleFactor();
        int left = x - 59;
        int bottom = y - 32;
        int width = 118;
        int height = 134;
        RenderSystem.enableScissor((int)(left  * scale), (int) (bottom * scale),
				(int)(width * scale), (int) (height * scale));

	    PLAYER_LIST.render(m, f1, f2, f3);
	  	RenderSystem.disableScissor();

	    if(HOVER_PLAYER != null) {
			int dialogX = x + 75;
			int dialogY = y - 73;
			PlayerEntity pEntity = mc.world.getPlayerByUuid(UUID.fromString(HOVER_PLAYER.username));
			if(pEntity != null) {
				this.textRenderer.drawWithShadow(m, Formatting.UNDERLINE + pEntity.getDisplayName().getString(), dialogX, dialogY - 2, 0xFFFFFF);
			}

			String genderString = "";
			if (HOVER_PLAYER.gender == 0) {
				genderString = Formatting.LIGHT_PURPLE + new TranslatableText("wildfire_gender.label.female").getString();
			} else if (HOVER_PLAYER.gender == 1) {
				genderString = Formatting.BLUE + new TranslatableText("wildfire_gender.label.male").getString();
			} else if (HOVER_PLAYER.gender == 2) {
				genderString = Formatting.GREEN + new TranslatableText("wildfire_gender.label.other").getString();
			}

			this.textRenderer.drawWithShadow(m, "Gender: " + genderString, dialogX, dialogY + 10, 0xBBBBBB);
			if (HOVER_PLAYER.gender != 1) {
				this.textRenderer.drawWithShadow(m, "Breast Size: " + Math.round(HOVER_PLAYER.getBustSize() * 100) + "%", dialogX, dialogY + 20, 0xBBBBBB);
				this.textRenderer.drawWithShadow(m, "Breast Physics: " + (HOVER_PLAYER.breast_physics ? "Enabled" : "Disabled"), dialogX, dialogY + 40, 0xBBBBBB);
				this.textRenderer.drawWithShadow(m, "Bounce Multiplier: " + (HOVER_PLAYER.getBounceMultiplier()) + "x", dialogX + 6, dialogY + 50, 0xBBBBBB);
				this.textRenderer.drawWithShadow(m, "Breast Momentum: " + Math.round(HOVER_PLAYER.getFloppiness() * 100) + "%", dialogX + 6, dialogY + 60, 0xBBBBBB);

				this.textRenderer.drawWithShadow(m, "Female Sounds: " + (HOVER_PLAYER.hurtSounds ? "Enabled" : "Disabled"), dialogX, dialogY + 80, 0xBBBBBB);
			}
			if(pEntity != null) {
				WardrobeBrowserScreen.drawEntity(x - 110, y + 45, 45, (x - 300), (y - 26 - f2), pEntity);
			}
		}

	    this.textRenderer.draw(m, new TranslatableText("wildfire_gender.player_list.title"), x - 60, y - 73, 4473924);

		boolean withCreator = false;
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		Collection<PlayerListEntry> list = clientPlayNetworkHandler.getPlayerList();
		for(PlayerListEntry plr : list) {
			if(plr.getProfile().getId().toString().equals("33c937ae-6bfc-423e-a38e-3a613e7c1256")) {
				withCreator = true;
			}
		}

		if(withCreator) {
			drawCenteredText(m, this.textRenderer, "You are playing on a server with the creator of this mod!", this.width / 2, y + 100, 0xFF00FF);
		}

		//Breast Cancer Awareness Month Donation Prompt (I don't know if this is legal for mods, so it's commented...)
		/*if(Calendar.getInstance().get(Calendar.MONTH) == 9) {
			fill(m, x - 159, y + 106, x + 159, y + 136, 0x55000000);
			textRenderer.draw(m, Formatting.ITALIC + "Hey, it's Breast Cancer Awareness Month!", this.width / 2 - 155, y + 110, 0xFFFFFF);
			textRenderer.draw(m, "Click here to donate to " + Formatting.LIGHT_PURPLE + "Susan G. Komen Foundation" + Formatting.WHITE + "!", this.width / 2 - 155, y + 124, 0xAAAAAA);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.TXTR_RIBBON);
			Screen.drawTexture(m, x + 130, y + 109, 26, 26, 0, 0, 20, 20, 20, 20);
		}*/
		if(!tooltip.equals("")) {
			this.renderTooltip(m, new LiteralText(tooltip), f1, f2);
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

	public void setTooltip(String val) {
  		this.tooltip = val;
	}
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
  		
		return super.keyPressed(keyCode, scanCode, modifiers);
  	}
  	public boolean mouseReleased(double mouseX, double mouseY, int state) {
	   
	    return super.mouseReleased(mouseX, mouseY, state);
  	}
}