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

package com.wildfire.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import com.wildfire.main.Breasts;
import com.wildfire.physics.BreastPhysics;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import com.wildfire.render.WildfireModelRenderer.OverlayModelBox;
import com.wildfire.render.WildfireModelRenderer.PositionTextureVertex;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.client.ForgeHooksClient;

public class GenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	private BreastModelBox lBreast, rBreast;
	private OverlayModelBox lBreastWear, rBreastWear;
	private BreastModelBox lBoobArmor, rBoobArmor;

	private float preBreastSize = 0f;

	public GenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> player) {
		super(player);

		lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		lBreastWear = new OverlayModelBox(true,64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBreastWear = new OverlayModelBox(false,64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);

		lBoobArmor = new BreastModelBox(64, 32, 16, 19, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new BreastModelBox(64, 32, 20, 19, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
	}

	private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = new HashMap<>();
	//Copy of Forge's patched in HumanoidArmorLayer#getArmorResource
	public ResourceLocation getArmorResource(AbstractClientPlayer entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
		ArmorItem item = (ArmorItem) stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture,
			(slot == EquipmentSlot.LEGS ? 2 : 1), type == null ? "" : String.format(Locale.ROOT, "_%s", type));

		s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
		}

		return resourcelocation;
	}

	@Override
	public void render(@Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource bufferSource, int packedLightIn, @Nonnull AbstractClientPlayer ent, float limbAngle,
		float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		if (ent.isInvisibleTo(Minecraft.getInstance().player)) {
			//Exit early if the entity shouldn't actually be seen
			return;
		}
		//Surround with a try/catch to fix for essential mod.
		try {
			//0.5 or 0
			UUID playerUUID = ent.getUUID();
			//System.out.println(playerUUID);
			GenderPlayer plr = WildfireGender.getPlayerById(playerUUID);
			if(plr == null) return;

			PlayerRenderer rend = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ent);
			PlayerModel<AbstractClientPlayer> model = rend.getModel();

			Breasts breasts = plr.getBreasts();
			float breastOffsetX = Math.round((Math.round(breasts.xOffset * 100f) / 100f) * 10) / 10f;
			float breastOffsetY = -Math.round((Math.round(breasts.yOffset * 100f) / 100f) * 10) / 10f;
			float breastOffsetZ = -Math.round((Math.round(breasts.zOffset * 100f) / 100f) * 10) / 10f;

			BreastPhysics leftBreastPhysics = plr.getLeftBreastPhysics();
			final float bSize = leftBreastPhysics.getBreastSize(partialTicks);
			float outwardAngle = (Math.round(breasts.cleavage * 100f) / 100f) * 100f;
			outwardAngle = Math.min(outwardAngle, 10);
			boolean uniboob = breasts.isUniboob;


			float reducer = 0;
			if (bSize < 0.84f) reducer++;
			if (bSize < 0.72f) reducer++;

			if (preBreastSize != bSize) {
				lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
				rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
				lBoobArmor = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
				rBoobArmor = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
				preBreastSize = bSize;
			}

			//DEPENDENCIES
			float overlayRed = 1;
			float overlayGreen = 1;
			float overlayBlue = 1;
			//Note: We only render if the entity is not visible to the player, so we can assume it is visible to the player
			float overlayAlpha = ent.isInvisible() ? 0.15F : 1;

			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

			float lTotal = Mth.lerp(partialTicks, leftBreastPhysics.getPreBounceY(), leftBreastPhysics.getBounceY());
			float lTotalX = Mth.lerp(partialTicks, leftBreastPhysics.getPreBounceX(), leftBreastPhysics.getBounceX());
			float leftBounceRotation = Mth.lerp(partialTicks, leftBreastPhysics.getPreBounceRotation(), leftBreastPhysics.getBounceRotation());
			float rTotal;
			float rTotalX;
			float rightBounceRotation;
			if (uniboob) {
				rTotal = lTotal;
				rTotalX = lTotalX;
				rightBounceRotation = leftBounceRotation;
			} else {
				BreastPhysics rightBreastPhysics = plr.getRightBreastPhysics();
				rTotal = Mth.lerp(partialTicks, rightBreastPhysics.getPreBounceY(), rightBreastPhysics.getBounceY());
				rTotalX = Mth.lerp(partialTicks, rightBreastPhysics.getPreBounceX(), rightBreastPhysics.getBounceX());
				rightBounceRotation = Mth.lerp(partialTicks, rightBreastPhysics.getPreBounceRotation(), rightBreastPhysics.getBounceRotation());
			}
			float breastSize = bSize * 1.5f;
			if (breastSize > 0.7f) breastSize = 0.7f;
			if (bSize > 0.7f) {
				breastSize = bSize;
			}

			ItemStack armorStack = ent.getItemBySlot(EquipmentSlot.CHEST);

			boolean isChestplateOccupied = !armorStack.isEmpty() && !(armorStack.getItem() instanceof ElytraItem);

			if (breastSize < 0.02f || (!plr.showBreastsInArmor && isChestplateOccupied)) return;

			float zOff = 0.0625f - (bSize * 0.0625f);
			breastSize = bSize + 0.5f * Math.abs(bSize - 0.7f) * 2f;

			//matrixStack.translate(0, 0, zOff);
			//System.out.println(bounceRotation);

			boolean breathingAnimation = !ent.isUnderWater() ||
					((ent.hasEffect(MobEffects.WATER_BREATHING) && ent.isUnderWater()) ||
					ent.hasEffect(MobEffects.CONDUIT_POWER) && ent.isUnderWater());

			boolean bounceEnabled = plr.hasBreastPhysics && (!isChestplateOccupied || plr.hasArmorBreastPhysics); //oh, you found this?

			int combineTex = LivingEntityRenderer.getOverlayCoords(ent, 0);
			RenderType type = RenderType.entityTranslucent(rend.getTextureLocation(ent));
			VertexConsumer vertexConsumer = bufferSource.getBuffer(type);
			renderBreastWithTransforms(ent, model.body, armorStack, matrixStack, bufferSource, vertexConsumer, packedLightIn, combineTex, overlayRed, overlayGreen,
				overlayBlue, overlayAlpha, bounceEnabled, lTotalX, lTotal, leftBounceRotation, breastSize, breastOffsetX, breastOffsetY, breastOffsetZ, zOff,
				outwardAngle, uniboob, isChestplateOccupied, breathingAnimation, true);
			renderBreastWithTransforms(ent, model.body, armorStack, matrixStack, bufferSource, vertexConsumer, packedLightIn, combineTex, overlayRed, overlayGreen,
				overlayBlue, overlayAlpha, bounceEnabled, rTotalX, rTotal, rightBounceRotation, breastSize, -breastOffsetX, breastOffsetY, breastOffsetZ, zOff,
				-outwardAngle, uniboob, isChestplateOccupied, breathingAnimation, false);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private float calculateRotation(float breastSize, float rotationMultiplier, boolean bounceEnabled) {
		float totalRotation = breastSize + rotationMultiplier;
		if (!bounceEnabled) {
			totalRotation = breastSize;
		}
		if (totalRotation > breastSize + 0.2F) {
			totalRotation = breastSize + 0.2F;
		}
		if (totalRotation > 1) {
			return 1; //hard limit for MAX
		}
		return totalRotation;
	}

	private static int pushMatrix(PoseStack m, ModelPart mdl) {
		m.pushPose();
		m.translate(mdl.x * 0.0625f, mdl.y * 0.0625f, mdl.z * 0.0625f);
		if (mdl.zRot != 0.0F) {
			m.mulPose(new Quaternion(0f, 0f, mdl.zRot, false));
		}
		if (mdl.yRot != 0.0F) {
			m.mulPose(new Quaternion(0f, mdl.yRot, 0f, false));
		}
		if (mdl.xRot != 0.0F) {
			m.mulPose(new Quaternion(mdl.xRot, 0f, 0f, false));
		}
		return 1;
	}

	private void renderBreastWithTransforms(AbstractClientPlayer entity, ModelPart body, ItemStack armorStack, PoseStack matrixStack, MultiBufferSource bufferSource,
		VertexConsumer vertexConsumer, int packedLightIn, int combineTex, float red, float green, float blue, float alpha, boolean bounceEnabled, float totalX, float total,
		float bounceRotation, float breastSize, float breastOffsetX, float breastOffsetY, float breastOffsetZ, float zOff, float outwardAngle, boolean uniboob,
		boolean isChestplateOccupied, boolean breathingAnimation, boolean left) {
		matrixStack.pushPose();
		//Surround with a try/catch to fix for essential mod.
		try {
			matrixStack.translate(body.x * 0.0625f, body.y * 0.0625f, body.z * 0.0625f);
			if (body.zRot != 0.0F) {
				matrixStack.mulPose(new Quaternion(0f, 0f, body.zRot, false));
			}
			if (body.yRot != 0.0F) {
				matrixStack.mulPose(new Quaternion(0f, body.yRot, 0f, false));
			}
			if (body.xRot != 0.0F) {
				matrixStack.mulPose(new Quaternion(body.xRot, 0f, 0f, false));
			}

			//right breast
			if (bounceEnabled) {
				matrixStack.translate(totalX / 32f, 0, 0);
				matrixStack.translate(0, total / 32f, 0);
			}

			matrixStack.translate(breastOffsetX * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f), zOff - 0.0625f * 2f + (breastOffsetZ * 0.0625f)); //shift down to correct position

			if (!uniboob) {
				matrixStack.translate(-0.0625f * 2 * (left ? 1 : -1), 0, 0);
			}
			if (bounceEnabled) {
				matrixStack.mulPose(new Quaternion(0, bounceRotation, 0, true));
			}
			if (!uniboob) {
				matrixStack.translate(0.0625f * 2 * (left ? 1 : -1), 0, 0);
			}

			float rotationMultiplier = 0;
			if (bounceEnabled) {
				matrixStack.translate(0, -0.035f * breastSize, 0); //shift down to correct position
				rotationMultiplier = -total / 12f;
			}
			float totalRotation = calculateRotation(breastSize, rotationMultiplier, bounceEnabled);

			if (isChestplateOccupied) {
				matrixStack.translate(0, 0, 0.01f);
			}

			matrixStack.mulPose(new Quaternion(0, outwardAngle, 0, true));
			matrixStack.mulPose(new Quaternion(-35f * totalRotation, 0, 0, true));

			if (!isChestplateOccupied && breathingAnimation) {
				float f5 = -Mth.cos(entity.tickCount * 0.09F) * 0.45F + 0.45F;
				matrixStack.mulPose(new Quaternion(f5, 0, 0, true));
			}

			matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX

			renderBreast(entity, armorStack, matrixStack, bufferSource, vertexConsumer, packedLightIn, combineTex, red, green, blue, alpha, left);
		} catch(Exception e) {
			e.printStackTrace();
		}
		matrixStack.popPose();
	}

	private void renderBreast(AbstractClientPlayer entity, ItemStack armorStack, PoseStack matrixStack, MultiBufferSource bufferSource, VertexConsumer vertexConsumer,
		int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, boolean left) {
		renderBox(left ? lBreast : rBreast, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (entity.isModelPartShown(PlayerModelPart.JACKET)) {
			matrixStack.translate(0, 0, -0.015f);
			matrixStack.scale(1.05f, 1.05f, 1.05f);
			renderBox(left ? lBreastWear : rBreastWear, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		//Render Breast Armor
		if (!armorStack.isEmpty() && !(armorStack.getItem() instanceof ElytraItem) && armorStack.getItem() instanceof ArmorItem armorItem) {
			ResourceLocation armorTexture = getArmorResource(entity, armorStack, EquipmentSlot.CHEST, null);
			ResourceLocation overlayTexture = null;
			float armorR = 1f;
			float armorG = 1f;
			float armorB = 1f;
			if (armorItem instanceof DyeableLeatherItem dyeableItem) {
				overlayTexture = getArmorResource(entity, armorStack, EquipmentSlot.CHEST, "overlay");
				int color = dyeableItem.getColor(armorStack);
				armorR = (float) (color >> 16 & 255) / 255.0F;
				armorG = (float) (color >> 8 & 255) / 255.0F;
				armorB = (float) (color & 255) / 255.0F;
			}
			matrixStack.pushPose();
			matrixStack.translate(left ? 0.001f : -0.001f, 0.015f, -0.015f);
			matrixStack.scale(1.05f, 1, 1);
			WildfireModelRenderer.BreastModelBox armor = left ? lBoobArmor : rBoobArmor;
			RenderType armorType = RenderType.armorCutoutNoCull(armorTexture);
			VertexConsumer armorVertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, armorType, false, armorStack.hasFoil());
			renderBox(armor, matrixStack, armorVertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, armorR, armorG, armorB, 1);
			if (overlayTexture != null) {
				RenderType overlayType = RenderType.armorCutoutNoCull(overlayTexture);
				VertexConsumer overlayVertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, overlayType, false, armorStack.hasFoil());
				renderBox(armor, matrixStack, overlayVertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
			}
			matrixStack.popPose();
		}
	}

	private static void renderBox(WildfireModelRenderer.ModelBox model, PoseStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
		float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStack.last().pose();
		Matrix3f matrix3f =	matrixStack.last().normal();
		for (WildfireModelRenderer.TexturedQuad quad : model.quads) {
			Vector3f vector3f = new Vector3f(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());
			vector3f.transform(matrix3f);
			float normalX = vector3f.x();
			float normalY = vector3f.y();
			float normalZ = vector3f.z();
			for (PositionTextureVertex vertex : quad.vertexPositions) {
				bufferIn.vertex(matrix4f, vertex.x() / 16.0F, vertex.y() / 16.0F, vertex.z() / 16.0F)
					.color(red, green, blue, alpha)
					.uv(vertex.texturePositionX(), vertex.texturePositionY())
					.overlayCoords(packedOverlayIn)
					.uv2(packedLightIn)
					.normal(normalX, normalY, normalZ)
					.endVertex();
			}
		}
	}
}
