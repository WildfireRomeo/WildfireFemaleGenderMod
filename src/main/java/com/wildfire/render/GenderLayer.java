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

package com.wildfire.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.api.IGenderArmor;
import com.wildfire.main.Breasts;
import com.wildfire.main.WildfireHelper;
import com.wildfire.physics.BreastPhysics;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import com.wildfire.render.WildfireModelRenderer.OverlayModelBox;
import com.wildfire.render.WildfireModelRenderer.PositionTextureVertex;

import java.lang.Math;
import javax.annotation.Nonnull;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
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
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GenderLayer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = new HashMap<>();
	private final SpriteAtlasTexture armorTrimsAtlas;

	private BreastModelBox lBreast, rBreast;
	private final OverlayModelBox lBreastWear, rBreastWear;
	private final BreastModelBox lBoobArmor, rBoobArmor;
	private final BreastModelBox lTrim, rTrim;

	private float preBreastSize = 0f;

	public GenderLayer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> render,
	                   BakedModelManager bakery) {
		super(render);
		armorTrimsAtlas = bakery.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);

		lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		lBreastWear = new OverlayModelBox(true,64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBreastWear = new OverlayModelBox(false,64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);

		lBoobArmor = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		// apply a very slight delta to fix z-fighting with the armor
		lTrim = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 5, 0.001F, false);
		rTrim = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 5, 0.001F, false);
	}

	public Identifier getArmorResource(ArmorItem item, boolean legs, @Nullable String overlay) {
		String texturePath = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
		return ARMOR_TEXTURE_CACHE.computeIfAbsent(texturePath, Identifier::new);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn, @Nonnull AbstractClientPlayerEntity ent, float limbAngle,
					   float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		if (ent.isInvisibleTo(MinecraftClient.getInstance().player)) {
			//Exit early if the entity shouldn't actually be seen
			return;
		}
		//Surround with a try/catch to fix for essential mod.
		try {
			GenderPlayer plr = WildfireGender.getPlayerById(ent.getUuid());
			if(plr == null) return;

			ItemStack armorStack = ent.getEquippedStack(EquipmentSlot.CHEST);
			//Note: When the stack is empty the helper will fall back to an implementation that returns the proper data
			IGenderArmor genderArmor = WildfireHelper.getArmorConfig(armorStack);
			boolean isChestplateOccupied = genderArmor.coversBreasts();
			if (genderArmor.alwaysHidesBreasts() || !plr.showBreastsInArmor() && isChestplateOccupied) {
				//If the armor always hides breasts or there is armor and the player configured breasts
				// to be hidden when wearing armor, we can just exit early rather than doing any calculations
				return;
			}

			PlayerEntityRenderer rend = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(ent);
			PlayerEntityModel<AbstractClientPlayerEntity> model = rend.getModel();

			Breasts breasts = plr.getBreasts();
			float breastOffsetX = Math.round((Math.round(breasts.getXOffset() * 100f) / 100f) * 10) / 10f;
			float breastOffsetY = -Math.round((Math.round(breasts.getYOffset() * 100f) / 100f) * 10) / 10f;
			float breastOffsetZ = -Math.round((Math.round(breasts.getZOffset() * 100f) / 100f) * 10) / 10f;

			BreastPhysics leftBreastPhysics = plr.getLeftBreastPhysics();
			final float bSize = leftBreastPhysics.getBreastSize(partialTicks);
			float outwardAngle = (Math.round(breasts.getCleavage() * 100f) / 100f) * 100f;
			outwardAngle = Math.min(outwardAngle, 10);

			float reducer = 0;
			if (bSize < 0.84f) reducer++;
			if (bSize < 0.72f) reducer++;

			if (preBreastSize != bSize) {
				lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
				rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
				preBreastSize = bSize;
			}

			//Note: We only render if the entity is not visible to the player, so we can assume it is visible to the player
			float overlayAlpha = ent.isInvisible() ? 0.15F : 1;
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

			float lTotal = MathHelper.lerp(partialTicks, leftBreastPhysics.getPreBounceY(), leftBreastPhysics.getBounceY());
			float lTotalX = MathHelper.lerp(partialTicks, leftBreastPhysics.getPreBounceX(), leftBreastPhysics.getBounceX());
			float leftBounceRotation = MathHelper.lerp(partialTicks, leftBreastPhysics.getPreBounceRotation(), leftBreastPhysics.getBounceRotation());
			float rTotal;
			float rTotalX;
			float rightBounceRotation;
			if (breasts.isUniboob()) {
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
			if (bSize > 0.7f) breastSize = bSize;
			if (breastSize < 0.02f) return;

			float zOff = 0.0625f - (bSize * 0.0625f);
			breastSize = bSize + 0.5f * Math.abs(bSize - 0.7f) * 2f;

			//matrixStack.translate(0, 0, zOff);
			//System.out.println(bounceRotation);

			float resistance = MathHelper.clamp(genderArmor.physicsResistance(), 0, 1);
			//Note: We only check if the breathing animation should be enabled if the chestplate's physics resistance
			// is less than or equal to 0.5 so that if we won't be rendering it we can avoid doing extra calculations
			boolean breathingAnimation = resistance <= 0.5F &&
										 (!ent.isSubmergedInWater() || StatusEffectUtil.hasWaterBreathing(ent) ||
										  ent.getWorld().getBlockState(new BlockPos(ent.getBlockX(), ent.getBlockY(), ent.getBlockZ())).isOf(Blocks.BUBBLE_COLUMN));
			boolean bounceEnabled = plr.hasBreastPhysics() && (!isChestplateOccupied || resistance < 1); //oh, you found this?

			int combineTex = LivingEntityRenderer.getOverlay(ent, 0);
			RenderLayer type = RenderLayer.getEntityTranslucent(rend.getTexture(ent));
			renderBreastWithTransforms(ent, model.body, armorStack, matrixStack, vertexConsumerProvider, type, packedLightIn, combineTex,
					overlayAlpha, bounceEnabled, lTotalX, lTotal, leftBounceRotation, breastSize, breastOffsetX, breastOffsetY, breastOffsetZ, zOff,
					outwardAngle, breasts.isUniboob(), isChestplateOccupied, breathingAnimation, true);
			renderBreastWithTransforms(ent, model.body, armorStack, matrixStack, vertexConsumerProvider, type, packedLightIn, combineTex,
					overlayAlpha, bounceEnabled, rTotalX, rTotal, rightBounceRotation, breastSize, -breastOffsetX, breastOffsetY, breastOffsetZ, zOff,
					-outwardAngle, breasts.isUniboob(), isChestplateOccupied, breathingAnimation, false);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void renderBreastWithTransforms(AbstractClientPlayerEntity entity, ModelPart body, ItemStack armorStack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
											RenderLayer breastRenderType, int packedLightIn, int combineTex, float alpha, boolean bounceEnabled, float totalX, float total, float bounceRotation,
											float breastSize, float breastOffsetX, float breastOffsetY, float breastOffsetZ, float zOff, float outwardAngle, boolean uniboob,
											boolean isChestplateOccupied, boolean breathingAnimation, boolean left) {
		matrixStack.push();
		//Surround with a try/catch to fix for essential mod.
		try {
			matrixStack.translate(body.pivotX * 0.0625f, body.pivotY * 0.0625f, body.pivotZ * 0.0625f);
			if (body.roll != 0.0F) {
				matrixStack.multiply(new Quaternionf().rotationXYZ(0f, 0f, body.roll));
			}
			if (body.yaw != 0.0F) {
				matrixStack.multiply(new Quaternionf().rotationXYZ(0f, body.yaw, 0f));
			}
			if (body.pitch != 0.0F) {
				matrixStack.multiply(new Quaternionf().rotationXYZ(body.pitch, 0f, 0f));
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
				matrixStack.multiply(new Quaternionf().rotationXYZ(0, (float)(bounceRotation * (Math.PI / 180f)), 0));
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

			matrixStack.multiply(new Quaternionf().rotationXYZ(0, (float)(outwardAngle * (Math.PI / 180f)), 0));
			matrixStack.multiply(new Quaternionf().rotationXYZ((float)(-35f * totalRotation * (Math.PI / 180f)), 0, 0));

			if (breathingAnimation) {
				float f5 = -MathHelper.cos(entity.age * 0.09F) * 0.45F + 0.45F;
				matrixStack.multiply(new Quaternionf().rotationXYZ((float)(f5 * (Math.PI / 180f)), 0, 0));
			}

			matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX

			renderBreast(entity, armorStack, matrixStack, vertexConsumerProvider, breastRenderType, packedLightIn, combineTex, alpha, left);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			matrixStack.pop();
		}
	}

	private void renderBreast(AbstractClientPlayerEntity entity, ItemStack armorStack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, RenderLayer breastRenderType,
	                          int packedLightIn, int packedOverlayIn, float alpha, boolean left) {
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(breastRenderType);
		renderBox(left ? lBreast : rBreast, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, 1f, 1f, 1f, alpha);
		if (entity.isPartVisible(PlayerModelPart.JACKET)) {
			matrixStack.translate(0, 0, -0.015f);
			matrixStack.scale(1.05f, 1.05f, 1.05f);
			renderBox(left ? lBreastWear : rBreastWear, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, 1f, 1f, 1f, alpha);
		}

		if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
			renderVanillaLikeBreastArmor(entity, matrixStack, vertexConsumerProvider, armorItem, armorStack, packedLightIn, left);
		}
	}

	// TODO eventually expose some way for mods to override this, maybe through a default impl in IGenderArmor or similar
	private void renderVanillaLikeBreastArmor(PlayerEntity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, ArmorItem armorItem,
	                                          ItemStack armorStack, int packedLightIn, boolean left) {
		Identifier armorTexture = getArmorResource(armorItem, false, null);
		Identifier overlayTexture = null;
		boolean hasGlint = armorStack.hasGlint();
		float armorR = 1f, armorG = 1f, armorB = 1f;
		if (armorItem instanceof DyeableArmorItem dyeableItem) {
			//overlayTexture = getArmorResource(entity, armorStack, EquipmentSlot.CHEST, "overlay");
			int color = dyeableItem.getColor(armorStack);
			armorR = (float) (color >> 16 & 255) / 255.0F;
			armorG = (float) (color >> 8 & 255) / 255.0F;
			armorB = (float) (color & 255) / 255.0F;
		}
		matrixStack.push();
		try {
			matrixStack.translate(left ? 0.001f : -0.001f, 0.015f, -0.015f);
			matrixStack.scale(1.05f, 1, 1);
			WildfireModelRenderer.BreastModelBox armor = left ? lBoobArmor : rBoobArmor;
			RenderLayer armorType = RenderLayer.getArmorCutoutNoCull(armorTexture);
			VertexConsumer armorVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, armorType, false, hasGlint);
			renderBox(armor, matrixStack, armorVertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, armorR, armorG, armorB, 1);
			//noinspection ConstantValue
			if (overlayTexture != null) {
				RenderLayer overlayType = RenderLayer.getArmorCutoutNoCull(overlayTexture);
				VertexConsumer overlayVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, overlayType, false, hasGlint);
				renderBox(armor, matrixStack, overlayVertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
			}

			ArmorTrim.getTrim(entity.getWorld().getRegistryManager(), armorStack).ifPresent((trim) -> {
				renderArmorTrim(armorItem.getMaterial(), matrixStack, vertexConsumerProvider, packedLightIn, trim, hasGlint, left);
			});
		} finally {
			matrixStack.pop();
		}
	}

	private void renderArmorTrim(ArmorMaterial material, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn,
	                             ArmorTrim trim, boolean hasGlint, boolean left) {
		BreastModelBox trimModelBox = left ? lTrim : rTrim;
		Sprite sprite = this.armorTrimsAtlas.getSprite(trim.getGenericModelId(material));
		VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumerProvider.getBuffer(TexturedRenderLayers.getArmorTrims()));
		// Render the armor trim itself
		renderBox(trimModelBox, matrixStack, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
		// The enchantment glint however requires special handling; due to how Minecraft's enchant glint rendering works, rendering
		// it at the same time as the trim itself results in the glint not rendering in sync with the rest of the armor.
		// We *also* can't simply render the glint for both the trim and armor at the same time, due to the slight delta we apply
		// to fix z-fighting between the trim and armor - and as such - a glint has to be rendered for each respective layer.
		if(hasGlint) {
			renderBox(trimModelBox, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getArmorEntityGlint()),
					packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
		}
	}

	private static void renderBox(WildfireModelRenderer.ModelBox model, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
		float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		Matrix3f matrix3f =	matrixStack.peek().getNormalMatrix();
		for (WildfireModelRenderer.TexturedQuad quad : model.quads) {
			Vector3f vector3f = new Vector3f(quad.normal.x, quad.normal.y, quad.normal.z);
			vector3f.mul(matrix3f);
			float normalX = vector3f.x;
			float normalY = vector3f.y;
			float normalZ = vector3f.z;
			for (PositionTextureVertex vertex : quad.vertexPositions) {
				float j = vertex.x() / 16.0F;
				float k = vertex.y() / 16.0F;
				float l = vertex.z() / 16.0F;
				Vector4f vector4f = new Vector4f(j, k, l, 1.0F);
				vector4f.mul(matrix4f);
				bufferIn.vertex(vector4f.x, vector4f.y, vector4f.z, red, green, blue, alpha, vertex.texturePositionX(), vertex.texturePositionY(), packedOverlayIn, packedLightIn, normalX, normalY, normalZ);
			}
		}
	}
}
