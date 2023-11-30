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

import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class GenderArmorLayer<T extends LivingEntity, M extends BipedEntityModel<T>> extends GenderLayer<T, M> {

	private final SpriteAtlasTexture armorTrimsAtlas;
	protected final BreastModelBox lBoobArmor, rBoobArmor;
	protected final BreastModelBox lTrim, rTrim;
	private EntityConfig entityConfig;

	public GenderArmorLayer(FeatureRendererContext<T, M> render, BakedModelManager bakery) {
		super(render);
		armorTrimsAtlas = bakery.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);

		lBoobArmor = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		// apply a very slight delta to fix z-fighting with the armor
		lTrim = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.001F, false);
		rTrim = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.001F, false);
	}

	public Identifier getArmorResource(@Nonnull ArmorItem item, boolean legs, @Nullable String overlay) {
		String material = item.getMaterial().getName();
		String namespace = "minecraft";
		int namespaceDelim = material.indexOf(":");
		if(namespaceDelim >= 0) {
			namespace = material.substring(0, namespaceDelim);
			material = material.substring(namespaceDelim + 1);
		}
		return new Identifier(namespace, "textures/models/armor/" + material + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png");
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn, @Nonnull T ent, float limbAngle, float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player == null) {
			// we're currently in a menu, give up rendering before we crash the game
			return;
		}

		// If the entity has no armor to render, just immediately give up
		// Note that we have to be very fast at abandoning rendering here, as this class is also attached to armor stands
		if(ent.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) return;

		try {
			entityConfig = getConfig(ent);
			if(entityConfig == null) return;

			if(!setupRender(ent, entityConfig, partialTicks)) return;
			if(ent instanceof ArmorStandEntity && !genderArmor.armorStandsCopySettings()) return;
			BipedEntityModel<T> model = getContextModel();

			// Render left
			matrixStack.push();
			try {
				setupTransformations(ent, model.body, matrixStack, BreastSide.LEFT);
				renderBreastArmor(ent, matrixStack, vertexConsumerProvider, packedLightIn, BreastSide.LEFT);
			} finally {
				matrixStack.pop();
			}

			matrixStack.push();
			// Render right
			try {
				setupTransformations(ent, model.body, matrixStack, BreastSide.RIGHT);
				renderBreastArmor(ent, matrixStack, vertexConsumerProvider, packedLightIn, BreastSide.RIGHT);
			} finally {
				matrixStack.pop();
			}
		} catch (Exception e) {
			WildfireGender.LOGGER.error("Failed to render breast armor", e);
		}
	}

	@Override
	protected void setupTransformations(T entity, ModelPart body, MatrixStack matrixStack, BreastSide side) {
		super.setupTransformations(entity, body, matrixStack, side);
		if((entity instanceof AbstractClientPlayerEntity player && player.isPartVisible(PlayerModelPart.JACKET)) ||
				(entity instanceof ArmorStandEntity && entityConfig.hasJacketLayer())) {
			matrixStack.translate(0, 0, -0.015f);
			matrixStack.scale(1.05f, 1.05f, 1.05f);
		}
	}

	// TODO eventually expose some way for mods to override this, maybe through a default impl in IGenderArmor or similar
	protected void renderBreastArmor(T entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn, BreastSide side) {
		if(armorStack.isEmpty() || !(armorStack.getItem() instanceof ArmorItem armorItem)) return;

		// If the armor uses its own custom renderer, just give up rendering entirely, as the only thing we'd
		// actually be able to do here is simply render a pink box.
		// Note that we fail this far in to allow for mods to override this through means like a mixin,
		// until any sort of official compatibility API is added.
		//noinspection UnstableApiUsage
		if(ArmorRendererRegistryImpl.get(armorStack.getItem()) != null) return;

		Identifier armorTexture = getArmorResource(armorItem, false, null);
		Identifier overlayTexture = null;
		boolean hasGlint = armorStack.hasGlint();
		float armorR = 1f, armorG = 1f, armorB = 1f;
		if(armorItem instanceof DyeableArmorItem dyeableItem) {
			//overlayTexture = getArmorResource(entity, armorStack, EquipmentSlot.CHEST, "overlay");
			int color = dyeableItem.getColor(armorStack);
			armorR = (float) (color >> 16 & 255) / 255.0F;
			armorG = (float) (color >> 8 & 255) / 255.0F;
			armorB = (float) (color & 255) / 255.0F;
		}
		matrixStack.push();
		try {
			matrixStack.translate(side == BreastSide.LEFT ? 0.001f : -0.001f, 0.015f, -0.015f);
			matrixStack.scale(1.05f, 1, 1);
			BreastModelBox armor = side == BreastSide.LEFT ? lBoobArmor : rBoobArmor;
			RenderLayer armorType = RenderLayer.getArmorCutoutNoCull(armorTexture);
			VertexConsumer armorVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, armorType, false, hasGlint);
			renderBox(armor, matrixStack, armorVertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, armorR, armorG, armorB, 1);
			//noinspection ConstantValue
			if(overlayTexture != null) {
				RenderLayer overlayType = RenderLayer.getArmorCutoutNoCull(overlayTexture);
				VertexConsumer overlayVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, overlayType, false, hasGlint);
				renderBox(armor, matrixStack, overlayVertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
			}

			ArmorTrim.getTrim(entity.getWorld().getRegistryManager(), armorStack, true).ifPresent((trim) -> {
				renderArmorTrim(armorItem.getMaterial(), matrixStack, vertexConsumerProvider, packedLightIn, trim, hasGlint, side);
			});
		} finally {
			matrixStack.pop();
		}
	}

	protected void renderArmorTrim(ArmorMaterial material, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn,
	                             ArmorTrim trim, boolean hasGlint, BreastSide side) {
		BreastModelBox trimModelBox = side == BreastSide.LEFT ? lTrim : rTrim;
		Sprite sprite = this.armorTrimsAtlas.getSprite(trim.getGenericModelId(material));
		VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
				vertexConsumerProvider.getBuffer(TexturedRenderLayers.getArmorTrims(trim.getPattern().value().decal())));
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
}
