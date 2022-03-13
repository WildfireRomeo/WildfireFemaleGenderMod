package com.wildfire.gui.screen;

import java.util.UUID;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.wildfire.gui.WildfireButton;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.GenderPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

public class WardrobeBrowserScreen extends Screen {
	

	private ResourceLocation BACKGROUND;
	public static float modelRotation = 0.5F;

	private UUID playerUUID;
	private Screen parent;

	public WardrobeBrowserScreen(Screen parent, UUID uuid) {
		super(new TranslatableComponent("wildfire_gender.settings.title"));
		this.playerUUID = uuid;
		this.parent = parent;
	}
  
 
	public boolean isPauseScreen() { return false; }

  
  	public void init() {
	  	Minecraft m = Minecraft.getInstance();
	    int j = this.height / 2;

	    
	    GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());

	    TextComponent genderString = new TextComponent(new TranslatableComponent("wildfire_gender.label.gender").getString() + " - ");

	    if(plr.gender == 0) {
	    	genderString.append(ChatFormatting.LIGHT_PURPLE + new TranslatableComponent("wildfire_gender.label.female").getString());
		} else if(plr.gender == 1) {
			genderString.append(ChatFormatting.BLUE + new TranslatableComponent("wildfire_gender.label.male").getString());
		} else if(plr.gender == 2) {
			genderString.append(ChatFormatting.GREEN + new TranslatableComponent("wildfire_gender.label.other").getString());
		}
		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 52, 158, 20, genderString, button -> {
			if(plr.gender == 0) {
				plr.gender = 2;
			} else if(plr.gender == 1) {
				plr.gender = 0;
			} else {
				plr.gender = 1;
			}

			TextComponent btnString = new TextComponent(new TranslatableComponent("wildfire_gender.label.gender").getString() + " - ");

			if(plr.gender == 0) {
				btnString.append(ChatFormatting.LIGHT_PURPLE +  new TranslatableComponent("wildfire_gender.label.female").getString());
			} else if(plr.gender == 1) {
				btnString.append(ChatFormatting.BLUE + new TranslatableComponent("wildfire_gender.label.male").getString());
			} else if(plr.gender == 2) {
				btnString.append(ChatFormatting.GREEN + new TranslatableComponent("wildfire_gender.label.other").getString());
			}

			button.setMessage(btnString);
			GenderPlayer.saveGenderInfo(plr);
		}));

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 32, 158, 20, new TextComponent("Appearance Settings..."), button -> {
			Minecraft.getInstance().setScreen(new WildfireBreastCustomizationScreen(WardrobeBrowserScreen.this, this.playerUUID));
		}));

		this.addRenderableWidget(new WildfireButton(this.width / 2 - 42, j - 12, 158, 20, new TextComponent("Character Settings..."), button -> {
			Minecraft.getInstance().setScreen(new WildfireCharacterSettingsScreen(WardrobeBrowserScreen.this, this.playerUUID));
		}));

		this.addRenderableWidget(new WildfireButton(this.width / 2 + 111, j - 63, 9, 9, new TextComponent("X"), button -> {
			Minecraft.getInstance().setScreen(parent);
		}));
	    
	    modelRotation = 0.6F;

	    this.BACKGROUND = new ResourceLocation("wildfire_gender", "textures/gui/wardrobe_bg.png");
    
	    super.init();
  	}

  	public void render(PoseStack m, int f1, int f2, float f3) {
		Minecraft minecraft = Minecraft.getInstance();
	    GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());
	    super.renderBackground(m);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, this.BACKGROUND);

	    int i = (this.width - 248) / 2;
	    int j = (this.height - 134) / 2;
		blit(m, i, j, 0, 0, 248, 156);

	    if(plr == null) return;


		int x = this.width / 2;
	    int y = this.height / 2;
	    
	    this.font.draw(m, new TranslatableComponent("wildfire_gender.wardrobe.title"), x - 42, y - 62, 4473924);

	    modelRotation = 0.6f;

		try {
			RenderSystem.setShaderColor(1f, 1.0F, 1.0F, 1.0F);
		    int xP = this.width / 2 - 82;
		    int yP = this.height / 2 + 32;
		    Player ent = Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID);
		    if(ent != null) {
		    	drawEntityOnScreen(xP, yP, 45, (xP - f1), (yP - 76 - f2), Minecraft.getInstance().level.getPlayerByUUID(this.playerUUID));
		    } else {
				//player left, fallback
				minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
		    }
		} catch(Exception e) {
			//error, fallback
			minecraft.setScreen(new WildfirePlayerListScreen(minecraft));
		}
	    super.render(m, f1, f2, f3);
	}
  	
  	public boolean mouseReleased(double mouseX, double mouseY, int state) {
	    GenderPlayer plr = WildfireGender.getPlayerByName(this.playerUUID.toString());
	  
	    return super.mouseReleased(mouseX, mouseY, state);
  	}

	public static void drawEntityOnScreen(int p_98851_, int p_98852_, int p_98853_, float p_98854_, float p_98855_, LivingEntity p_98856_) {
		float var6 = (float)Math.atan((double)(p_98854_ / 40.0F));
		float var7 = (float)Math.atan((double)(p_98855_ / 40.0F));
		PoseStack var8 = RenderSystem.getModelViewStack();
		var8.pushPose();
		var8.translate((double)p_98851_, (double)p_98852_, 1050.0D);
		var8.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack var9 = new PoseStack();
		var9.translate(0.0D, 0.0D, 1000.0D);
		var9.scale((float)p_98853_, (float)p_98853_, (float)p_98853_);
		Quaternion var10 = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion var11 = Vector3f.XP.rotationDegrees(var7 * 20.0F);
		var10.mul(var11);
		var9.mulPose(var10);
		float var12 = p_98856_.yBodyRot;
		float var13 = p_98856_.getYRot();
		float var14 = p_98856_.getXRot();
		float var15 = p_98856_.yHeadRotO;
		float var16 = p_98856_.yHeadRot;
		p_98856_.yBodyRot = 180.0F + var6 * 20.0F;
		p_98856_.setYRot(180.0F + var6 * 40.0F);
		p_98856_.setXRot(-var7 * 20.0F);
		p_98856_.yHeadRot = p_98856_.getYRot();
		p_98856_.yHeadRotO = p_98856_.getYRot();
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher var17 = Minecraft.getInstance().getEntityRenderDispatcher();
		var11.conj();
		var17.overrideCameraOrientation(var11);
		var17.setRenderShadow(false);
		MultiBufferSource.BufferSource var18 = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> {
			var17.render(p_98856_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, var9, var18, 15728880);
		});
		var18.endBatch();
		var17.setRenderShadow(true);
		p_98856_.yBodyRot = var12;
		p_98856_.setYRot(var13);
		p_98856_.setXRot(var14);
		p_98856_.yHeadRotO = var15;
		p_98856_.yHeadRot = var16;
		var8.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}
}