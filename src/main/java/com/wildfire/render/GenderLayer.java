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
import com.wildfire.api.IGenderArmor;
import com.wildfire.main.Breasts;
import com.wildfire.main.WildfireHelper;
import com.wildfire.physics.BreastPhysics;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import com.wildfire.render.WildfireModelRenderer.OverlayModelBox;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.util.math.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GenderLayer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

	private BreastModelBox lBreast, rBreast;
	private OverlayModelBox lBreastWear, rBreastWear;
	private BreastModelBox lBoobArmor, rBoobArmor;

	private float preBreastSize = 0f;

	public GenderLayer(FeatureRendererContext render) {
		super(render);


		lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		lBreastWear = new OverlayModelBox(true, 64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBreastWear = new OverlayModelBox(false, 64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);

		lBoobArmor = new BreastModelBox(64, 32, 16, 19, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new BreastModelBox(64, 32, 20, 19, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
	}

	private static final Map<String, Identifier> ARMOR_LOCATION_CACHE = new HashMap<>();

	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = new HashMap<String, Identifier>();
	public Identifier getArmorResource(ArmorItem item, boolean legs, @Nullable String overlay) {

		String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
		return (Identifier) ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
	}

	private void renderBreastWithTransforms(AbstractClientPlayerEntity entity, ModelPart body, ItemStack armorStack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
											VertexConsumer vertexConsumer, int packedLightIn, int combineTex, float red, float green, float blue, float alpha, boolean bounceEnabled, float totalX, float total,
											float bounceRotation, float breastSize, float breastOffsetX, float breastOffsetY, float breastOffsetZ, float zOff, float outwardAngle, boolean uniboob,
											boolean isChestplateOccupied, boolean breathingAnimation, boolean left) {
		matrixStack.push();
		//Surround with a try/catch to fix for essential mod.
		try {

			matrixStack.translate(body.pivotX * 0.0625f, body.pivotY * 0.0625f, body.pivotZ * 0.0625f);
			if (body.roll != 0.0F) {
				matrixStack.multiply(new Quaternion(0f, 0f, body.roll, false));
			}
			if (body.yaw != 0.0F) {
				matrixStack.multiply(new Quaternion(0f, body.yaw, 0f, false));
			}
			if (body.pitch != 0.0F) {
				matrixStack.multiply(new Quaternion(body.pitch, 0f, 0f, false));
			}

			if (bounceEnabled) {
				matrixStack.translate(totalX / 32f, 0, 0);
				matrixStack.translate(0, total / 32f, 0);
			}

			matrixStack.translate(breastOffsetX * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f), zOff - 0.0625f * 2f + (breastOffsetZ * 0.0625f)); //shift down to correct position

			if (!uniboob) {
				matrixStack.translate(-0.0625f * 2 * (left ? 1 : -1), 0, 0);
			}
			if (bounceEnabled) {
				matrixStack.multiply(new Quaternion(0, bounceRotation, 0, true));
			}
			if (!uniboob) {
				matrixStack.translate(0.0625f * 2 * (left ? 1 : -1), 0, 0);
			}

			float rotationMultiplier = 0;
			if (bounceEnabled) {
				matrixStack.translate(0, -0.035f * breastSize, 0); //shift down to correct position
				rotationMultiplier = -total / 12f;
			}
			float totalRotation = breastSize + rotationMultiplier;
			if (!bounceEnabled) {
				totalRotation = breastSize;
			}
			if (totalRotation > breastSize + 0.2F) {
				totalRotation = breastSize + 0.2F;
			}
			totalRotation = Math.min(totalRotation, 1); //hard limit for MAX

			if (isChestplateOccupied) {
				matrixStack.translate(0, 0, 0.01f);
			}

			matrixStack.multiply(new Quaternion(0, outwardAngle, 0, true));
			matrixStack.multiply(new Quaternion(-35f * totalRotation, 0, 0, true));

			if (breathingAnimation) {
				float f5 = -MathHelper.cos(entity.age * 0.09F) * 0.45F + 0.45F;
				matrixStack.multiply(new Quaternion(f5, 0, 0, true));
			}

			matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX

			renderBreast(entity, armorStack, matrixStack, vertexConsumerProvider, vertexConsumer, packedLightIn, combineTex, red, green, blue, alpha, left);
		} catch (Exception e) {
			e.printStackTrace();
		}
		matrixStack.pop();
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int packedLightIn, AbstractClientPlayerEntity ent, float limbAngle, float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		if (ent.isInvisibleTo(MinecraftClient.getInstance().player)) {
			//Exit early if the entity shouldn't actually be seen
			return;
		}
		//Surround with a try/catch to fix for essential mod.
		try {
			//0.5 or 0
			UUID playerUUID = ent.getUuid();
			//System.out.println(playerUUID);
			GenderPlayer plr = WildfireGender.getPlayerById(playerUUID);
			if (plr == null) return;

			ItemStack armorStack = ent.getInventory().getArmorStack(2);
			//Note: When the stack is empty the helper will fall back to an implementation that returns the proper data
			IGenderArmor genderArmor = WildfireHelper.getArmorConfig(armorStack);
			boolean isChestplateOccupied = genderArmor.coversBreasts();
			if (genderArmor.alwaysHidesBreasts() || !plr.showBreastsInArmor && isChestplateOccupied) {
				//If the armor always hides breasts or there is armor and the player configured breasts
				// to be hidden when wearing armor, we can just exit early rather than doing any calculations
				return;
			}

			PlayerEntityRenderer rend = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(ent);
			//PlayerModel<AbstractClientPlayer> model = rend.getModel();

			Breasts breasts = plr.getBreasts();
			float breastOffsetX = Math.round((Math.round(breasts.xOffset * 100f) / 100f) * 10) / 10f;
			float breastOffsetY = -Math.round((Math.round(breasts.yOffset * 100f) / 100f) * 10) / 10f;
			float breastOffsetZ = -Math.round((Math.round(breasts.zOffset * 100f) / 100f) * 10) / 10f;

			BreastPhysics leftBreastPhysics = plr.getLeftBreastPhysics();
			final float bSize = leftBreastPhysics.getBreastSize(partialTicks);
			float outwardAngle = (Math.round(breasts.cleavage * 100f) / 100f) * 100f;
			outwardAngle = Math.min(outwardAngle, 10);


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

			float lTotal = MathHelper.lerp(partialTicks, leftBreastPhysics.getPreBounceY(), leftBreastPhysics.getBounceY());
			float lTotalX = MathHelper.lerp(partialTicks, leftBreastPhysics.getPreBounceX(), leftBreastPhysics.getBounceX());
			float leftBounceRotation = MathHelper.lerp(partialTicks, leftBreastPhysics.getPreBounceRotation(), leftBreastPhysics.getBounceRotation());
			float rTotal;
			float rTotalX;
			float rightBounceRotation;
			if (breasts.isUniboob) {
				rTotal = lTotal;
				rTotalX = lTotalX;
				rightBounceRotation = leftBounceRotation;
			} else {
				BreastPhysics rightBreastPhysics = plr.getRightBreastPhysics();
				rTotal = MathHelper.lerp(partialTicks, rightBreastPhysics.getPreBounceY(), rightBreastPhysics.getBounceY());
				rTotalX = MathHelper.lerp(partialTicks, rightBreastPhysics.getPreBounceX(), rightBreastPhysics.getBounceX());
				rightBounceRotation = MathHelper.lerp(partialTicks, rightBreastPhysics.getPreBounceRotation(), rightBreastPhysics.getBounceRotation());
			}
			float breastSize = bSize * 1.5f;
			if (breastSize > 0.7f) breastSize = 0.7f;
			if (bSize > 0.7f) {
				breastSize = bSize;
			}

			if (breastSize < 0.02f) return;

			float zOff = 0.0625f - (bSize * 0.0625f);
			breastSize = bSize + 0.5f * Math.abs(bSize - 0.7f) * 2f;


			float resistance = MathHelper.clamp(genderArmor.physicsResistance(), 0, 1);
			boolean breathingAnimation = resistance <= 0.5F &&
					(!ent.isSubmergedInWater() || StatusEffectUtil.hasWaterBreathing(ent) ||
							ent.world.getBlockState(new BlockPos(ent.getX(), ent.getEyeY(), ent.getZ())).	isOf(Blocks.BUBBLE_COLUMN));
			boolean bounceEnabled = plr.hasBreastPhysics && (!isChestplateOccupied || plr.hasArmorBreastPhysics && resistance < 1); //oh, you found this?

			int combineTex = LivingEntityRenderer.getOverlay(ent, 0);
			RenderLayer type = RenderLayer.getEntityTranslucent(rend.getTexture(ent));
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(type);
			renderBreastWithTransforms(ent, rend.getModel().body, armorStack, matrixStack, vertexConsumers, vertexConsumer, packedLightIn, combineTex, overlayRed, overlayGreen,
					overlayBlue, overlayAlpha, bounceEnabled, lTotalX, lTotal, leftBounceRotation, breastSize, breastOffsetX, breastOffsetY, breastOffsetZ, zOff,
					outwardAngle, breasts.isUniboob, isChestplateOccupied, breathingAnimation, true);
			renderBreastWithTransforms(ent, rend.getModel().body, armorStack, matrixStack, vertexConsumers, vertexConsumer, packedLightIn, combineTex, overlayRed, overlayGreen,
					overlayBlue, overlayAlpha, bounceEnabled, rTotalX, rTotal, rightBounceRotation, breastSize, -breastOffsetX, breastOffsetY, breastOffsetZ, zOff,
					-outwardAngle, breasts.isUniboob, isChestplateOccupied, breathingAnimation, false);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void renderBreast(AbstractClientPlayerEntity entity, ItemStack armorStack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, VertexConsumer vertexConsumer,
							  int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, boolean left) {
		RenderLayer type = RenderLayer.getEntityTranslucent(entity.getSkinTexture());

		renderBox(left ? lBreast : rBreast, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (entity.isPartVisible(PlayerModelPart.JACKET)) {
			matrixStack.translate(0, 0, -0.015f);
			matrixStack.scale(1.05f, 1.05f, 1.05f);
			renderBox(left ? lBreastWear : rBreastWear, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		//TODO: Eventually we may want to expose a way via the API for mods to be able to override rendering
		// be it because they are not an armor item or the way they render their armor item is custom
		//Render Breast Armor
		if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
			Identifier armorTexture = getArmorResource(armorItem, false, null);
			Identifier overlayTexture = null;
			float armorR = 1f;
			float armorG = 1f;
			float armorB = 1f;
			if (armorItem instanceof DyeableArmorItem dyeableItem) {
				//TODO: FIX THIS
				int color = dyeableItem.getColor(armorStack);
				armorR = (float) (color >> 16 & 255) / 255.0F;
				armorG = (float) (color >> 8 & 255) / 255.0F;
				armorB = (float) (color & 255) / 255.0F;
			}
			matrixStack.push();
			matrixStack.translate(left ? 0.001f : -0.001f, 0.015f, -0.015f);
			matrixStack.scale(1.05f, 1, 1);
			WildfireModelRenderer.BreastModelBox armor = left ? lBoobArmor : rBoobArmor;
			RenderLayer armorType = RenderLayer.getArmorCutoutNoCull(armorTexture);
			VertexConsumer armorVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, armorType, false, armorStack.hasGlint());
			renderBox(armor, matrixStack, armorVertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, armorR, armorG, armorB, 1);
			if (overlayTexture != null) {
				RenderLayer overlayType = RenderLayer.getArmorCutoutNoCull(overlayTexture);
				VertexConsumer overlayVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, overlayType, false, armorStack.hasGlint());
				renderBox(armor, matrixStack, overlayVertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
			}
			matrixStack.pop();
		}
	}


	public static void renderBox(BreastModelBox cuboid, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		MatrixStack.Entry matrixEntryIn = matrixStack.peek();
		Matrix4f matrix4f = matrixEntryIn.getPositionMatrix();
		Matrix3f matrix3f = matrixEntryIn.getNormalMatrix();
		;

		WildfireModelRenderer.TexturedQuad[] var13 = cuboid.quads;
		int var14 = var13.length;

		for (int var15 = 0; var15 < var14; ++var15) {
			WildfireModelRenderer.TexturedQuad quad = var13[var15];
			Vec3f vector3f = quad.normal.copy();
			vector3f.transform(matrix3f);
			float f = vector3f.getX();
			float g = vector3f.getY();
			float h = vector3f.getZ();

			for (int i = 0; i < 4; ++i) {
				WildfireModelRenderer.PositionTextureVertex vertex = quad.vertexPositions[i];
				float j = vertex.vector3D.getX() / 16.0F;
				float k = vertex.vector3D.getY() / 16.0F;
				float l = vertex.vector3D.getZ() / 16.0F;
				Vector4f vector4f = new Vector4f(j, k, l, 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.texturePositionX, vertex.texturePositionY, packedOverlayIn, packedLightIn, f, g, h);
			}
		}
	}

	public static void renderBox(WildfireModelRenderer.ModelBox cuboid, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		MatrixStack.Entry matrixEntryIn = matrixStack.peek();
		Matrix4f matrix4f = matrixEntryIn.getPositionMatrix();
		Matrix3f matrix3f = matrixEntryIn.getNormalMatrix();
		;

		WildfireModelRenderer.TexturedQuad[] var13 = cuboid.quads;
		int var14 = var13.length;

		for (int var15 = 0; var15 < var14; ++var15) {
			WildfireModelRenderer.TexturedQuad quad = var13[var15];
			Vec3f vector3f = quad.normal.copy();
			vector3f.transform(matrix3f);
			float f = vector3f.getX();
			float g = vector3f.getY();
			float h = vector3f.getZ();

			for (int i = 0; i < 4; ++i) {
				WildfireModelRenderer.PositionTextureVertex vertex = quad.vertexPositions[i];
				float j = vertex.vector3D.getX() / 16.0F;
				float k = vertex.vector3D.getY() / 16.0F;
				float l = vertex.vector3D.getZ() / 16.0F;
				Vector4f vector4f = new Vector4f(j, k, l, 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.texturePositionX, vertex.texturePositionY, packedOverlayIn, packedLightIn, f, g, h);
			}
		}
	}

	public static void renderBox(OverlayModelBox cuboid, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		MatrixStack.Entry matrixEntryIn = matrixStack.peek();
		Matrix4f matrix4f = matrixEntryIn.getPositionMatrix();
		Matrix3f matrix3f = matrixEntryIn.getNormalMatrix();
		;

		WildfireModelRenderer.TexturedQuad[] var13 = cuboid.quads;
		int var14 = var13.length;

		for (int var15 = 0; var15 < var14; ++var15) {
			WildfireModelRenderer.TexturedQuad quad = var13[var15];
			Vec3f vector3f = quad.normal.copy();
			vector3f.transform(matrix3f);
			float f = vector3f.getX();
			float g = vector3f.getY();
			float h = vector3f.getZ();

			for (int i = 0; i < 4; ++i) {
				WildfireModelRenderer.PositionTextureVertex vertex = quad.vertexPositions[i];
				float j = vertex.vector3D.getX() / 16.0F;
				float k = vertex.vector3D.getY() / 16.0F;
				float l = vertex.vector3D.getZ() / 16.0F;
				Vector4f vector4f = new Vector4f(j, k, l, 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.texturePositionX, vertex.texturePositionY, packedOverlayIn, packedLightIn, f, g, h);
			}
		}
	}
}