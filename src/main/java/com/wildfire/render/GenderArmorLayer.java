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
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class GenderArmorLayer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>> extends GenderLayer<S, M> {

	private final SpriteAtlasTexture armorTrimsAtlas;
	private final EquipmentModelLoader equipmentModelLoader;
	protected static final BreastModelBox lBoobArmor, rBoobArmor;
	protected static final BreastModelBox lTrim, rTrim;
	private EntityConfig entityConfig;

	static {
		lBoobArmor = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		// apply a very slight delta to fix z-fighting with the armor
		lTrim = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.001F, false);
		rTrim = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.001F, false);
	}

	public GenderArmorLayer(FeatureRendererContext<S, M> render, BakedModelManager bakery, EquipmentModelLoader equipmentModelLoader) {
		super(render);
		this.armorTrimsAtlas = bakery.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
		this.equipmentModelLoader = equipmentModelLoader;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, S state, float limbAngle, float limbDistance) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player == null) {
			// we're currently in a menu, give up rendering before we crash the game
			return;
		}

		LivingEntity ent = getEntity(state);
		if(ent == null) return;

		final ItemStack chestplate = state.equippedChestStack;
		// Check if the worn item in the chest slot is actually equippable in the chest slot, and has a model to render
		var component = chestplate.get(DataComponentTypes.EQUIPPABLE);
		if(component == null || component.slot() != EquipmentSlot.CHEST || component.model().isEmpty()) return;
		// And similarly just entirely give up if the item has a renderer registered with Fabric API
		// This will likely result in the player's breasts sticking out through the armor layer unless the mod in question
		// implements an IGenderArmor to prevent them from rendering entirely, but oh well; at least we won't be
		// rendering a pink box.
		//noinspection UnstableApiUsage
		if(ArmorRendererRegistryImpl.get(chestplate.getItem()) != null) return;

		try {
			entityConfig = EntityConfig.getEntity(ent);
			if(entityConfig == null) return;

			if(!setupRender(state, entityConfig)) return;
			if(ent instanceof ArmorStandEntity && !genderArmor.armorStandsCopySettings()) return;

			int color = chestplate.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(chestplate, -1) : -1;
			boolean glint = chestplate.hasGlint();

			renderSides(state, getContextModel(), matrixStack, side -> {
				var modelId = component.model().orElseThrow();
				equipmentModelLoader.get(modelId).getLayers(EquipmentModel.LayerType.HUMANOID).forEach(layer -> {
					// mojang what the Optional hell is this
					int layerColor = layer.dyeable().map(dye -> {
						int defaultColor = dye.colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(-1);
						return color != -1 ? color : defaultColor;
					}).orElse(-1);
					var texture = layer.getFullTextureId(EquipmentModel.LayerType.HUMANOID);
					renderBreastArmor(texture, matrixStack, vertexConsumerProvider, light, side, layerColor, glint);
				});

				var trim = armorStack.get(DataComponentTypes.TRIM);
				if(trim != null) {
					renderArmorTrim(modelId, matrixStack, vertexConsumerProvider, light, trim, glint, side);
				}
			});
		} catch(Exception e) {
			WildfireGender.LOGGER.error("Failed to render breast armor", e);
		}
	}

	@Override
	protected void resizeBox(float breastSize) {
		// this has no relevance to armor
	}

	@Override
	protected void setupTransformations(S state, M model, MatrixStack matrixStack, BreastSide side) {
		super.setupTransformations(state, model, matrixStack, side);
		LivingEntity entity = Objects.requireNonNull(getEntity(state), "getEntity()");
		if((entity instanceof AbstractClientPlayerEntity player && player.isPartVisible(PlayerModelPart.JACKET)) ||
				(entity instanceof ArmorStandEntity && entityConfig.hasJacketLayer())) {
			matrixStack.translate(0, 0, -0.015f);
			matrixStack.scale(1.05f, 1.05f, 1.05f);
		}
		matrixStack.translate(side.isLeft ? 0.001f : -0.001f, 0.015f, -0.015f);
		matrixStack.scale(1.05f, 1, 1);
	}

	// TODO eventually expose some way for mods to override this, maybe through a default impl in IGenderArmor or similar
	protected void renderBreastArmor(Identifier texture, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
	                                 int light, BreastSide side, int color, boolean glint) {
		BreastModelBox armor = side.isLeft ? lBoobArmor : rBoobArmor;
		RenderLayer armorType = RenderLayer.getArmorCutoutNoCull(texture);
		VertexConsumer armorVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, armorType, glint);
		renderBox(armor, matrixStack, armorVertexConsumer, light, OverlayTexture.DEFAULT_UV, ColorHelper.fullAlpha(color));
	}

	protected void renderArmorTrim(Identifier model, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
	                               int packedLightIn, ArmorTrim trim, boolean hasGlint, BreastSide side) {
		BreastModelBox trimModelBox = side.isLeft ? lTrim : rTrim;
		Sprite sprite = this.armorTrimsAtlas.getSprite(trim.getTexture(EquipmentModel.LayerType.HUMANOID, model));
		VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
				vertexConsumerProvider.getBuffer(TexturedRenderLayers.getArmorTrims(trim.pattern().value().decal())));
		// Render the armor trim itself
		renderBox(trimModelBox, matrixStack, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
		// The enchantment glint however requires special handling; due to how Minecraft's enchant glint rendering works, rendering
		// it at the same time as the trim itself results in the glint not rendering in sync with the rest of the armor.
		// We *also* can't simply render the glint for both the trim and armor at the same time, due to the slight delta we apply
		// to fix z-fighting between the trim and armor - and as such - a glint has to be rendered for each respective layer.
		if(hasGlint) {
			renderBox(trimModelBox, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getArmorEntityGlint()),
					packedLightIn, OverlayTexture.DEFAULT_UV, -1);
		}
	}
}
