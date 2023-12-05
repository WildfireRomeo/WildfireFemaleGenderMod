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

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.entitydata.Breasts;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireHelper;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.physics.BreastPhysics;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import com.wildfire.render.WildfireModelRenderer.OverlayModelBox;
import com.wildfire.render.WildfireModelRenderer.PositionTextureVertex;

import java.lang.Math;
import java.util.ConcurrentModificationException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.*;

@Environment(EnvType.CLIENT)
public class GenderLayer<T extends LivingEntity, M extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

	private BreastModelBox lBreast, rBreast;
	private final OverlayModelBox lBreastWear, rBreastWear;

	private float preBreastSize = 0f;
	private Breasts breasts;
	protected ItemStack armorStack;
	protected IGenderArmor genderArmor;
	protected boolean isChestplateOccupied, bounceEnabled, breathingAnimation;
	protected float breastOffsetX, breastOffsetY, breastOffsetZ, lPhysPositionY, lPhysPositionX, rPhysPositionY, rTotalX,
			lPhysBounceRotation, rPhysBounceRotation, breastSize, zOffset, outwardAngle;

	public GenderLayer(FeatureRendererContext<T, M> render) {
		super(render);
		lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		lBreastWear = new OverlayModelBox(true,64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBreastWear = new OverlayModelBox(false,64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
	}

	private @Nullable RenderLayer getRenderLayer(T entity) {
		boolean bodyVisible = !entity.isInvisible();
		boolean translucent = !bodyVisible && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
		Identifier texture = getTexture(entity);
		if(translucent) {
			return RenderLayer.getItemEntityTranslucentCull(texture);
		} else if(bodyVisible) {
			return RenderLayer.getEntityTranslucent(texture);
		} else if(entity.isGlowing()) {
			return RenderLayer.getOutline(texture);
		}
		return null;
	}

	protected @Nullable EntityConfig getConfig(T entity) {
		try {
			return EntityConfig.getEntity(entity);
		} catch(ConcurrentModificationException e) {
			// likely a temporary failure, try again later
			return null;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn, @Nonnull T ent, float limbAngle,
					   float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player == null) {
			// we're currently in a menu, give up rendering before we crash the game
			return;
		}

		EntityConfig entityConfig = getConfig(ent);
		if(entityConfig == null) return;

		try {
			if(!setupRender(ent, entityConfig, partialTicks)) return;
			int combineTex = LivingEntityRenderer.getOverlay(ent, 0);
			BipedEntityModel<T> model = getContextModel();

			// Render left
			matrixStack.push();
			try {
				setupTransformations(ent, model.body, matrixStack, BreastSide.LEFT);
				renderBreast(ent, matrixStack, vertexConsumerProvider, packedLightIn, combineTex, BreastSide.LEFT);
			} finally {
				matrixStack.pop();
			}

			// Render right
			matrixStack.push();
			try {
				setupTransformations(ent, model.body, matrixStack, BreastSide.RIGHT);
				renderBreast(ent, matrixStack, vertexConsumerProvider, packedLightIn, combineTex, BreastSide.RIGHT);
			} finally {
				matrixStack.pop();
			}
		} catch(Exception e) {
			WildfireGender.LOGGER.error("Failed to render breast layer", e);
		}
	}

	/**
	 * Common logic for setting up breast rendering
	 *
	 * @return {@code true} if rendering should continue
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	protected boolean setupRender(T entity, EntityConfig entityConfig, float partialTicks) {
		// Rendering breaks quite spectacularly on baby mobs, so just immediately give up before we even
		// attempt rendering on such an entity.
		if(entity.isBaby()) return false;

		armorStack = entity.getEquippedStack(EquipmentSlot.CHEST);
		//Note: When the stack is empty the helper will fall back to an implementation that returns the proper data
		genderArmor = WildfireHelper.getArmorConfig(armorStack);
		isChestplateOccupied = genderArmor.coversBreasts() && !entityConfig.getArmorPhysicsOverride();
		if(genderArmor.alwaysHidesBreasts() || !entityConfig.showBreastsInArmor() && isChestplateOccupied) {
			//If the armor always hides breasts or there is armor and the player configured breasts
			// to be hidden when wearing armor, we can just exit early rather than doing any calculations
			return false;
		}

		RenderLayer type = getRenderLayer(entity);
		if(type == null && !isChestplateOccupied) {
			// the entity is invisible and doesn't have a chestplate equipped
			return false;
		}

		breasts = entityConfig.getBreasts();
		breastOffsetX = Math.round((Math.round(breasts.getXOffset() * 100f) / 100f) * 10) / 10f;
		breastOffsetY = -Math.round((Math.round(breasts.getYOffset() * 100f) / 100f) * 10) / 10f;
		breastOffsetZ = -Math.round((Math.round(breasts.getZOffset() * 100f) / 100f) * 10) / 10f;

		BreastPhysics leftBreastPhysics = entityConfig.getLeftBreastPhysics();
		final float bSize = leftBreastPhysics.getBreastSize(partialTicks);
		outwardAngle = (Math.round(breasts.getCleavage() * 100f) / 100f) * 100f;
		outwardAngle = Math.min(outwardAngle, 10);

		float reducer = -1;
		if(bSize < 0.84f) reducer++;
		if(bSize < 0.72f) reducer++;

		if(preBreastSize != bSize) {
			lBreast = new BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
			rBreast = new BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
			preBreastSize = bSize;
		}

		lPhysPositionY = MathHelper.lerp(partialTicks, leftBreastPhysics.getPrePositionY(), leftBreastPhysics.getPositionY());
		lPhysPositionX = MathHelper.lerp(partialTicks, leftBreastPhysics.getPrePositionX(), leftBreastPhysics.getPositionX());
		lPhysBounceRotation = MathHelper.lerp(partialTicks, leftBreastPhysics.getPreBounceRotation(), leftBreastPhysics.getBounceRotation());
		if(breasts.isUniboob()) {
			rPhysPositionY = lPhysPositionY;
			rTotalX = lPhysPositionX;
			rPhysBounceRotation = lPhysBounceRotation;
		} else {
			BreastPhysics rightBreastPhysics = entityConfig.getRightBreastPhysics();
			rPhysPositionY = MathHelper.lerp(partialTicks, rightBreastPhysics.getPrePositionY(), rightBreastPhysics.getPositionY());
			rTotalX = MathHelper.lerp(partialTicks, rightBreastPhysics.getPrePositionX(), rightBreastPhysics.getPositionX());
			rPhysBounceRotation = MathHelper.lerp(partialTicks, rightBreastPhysics.getPreBounceRotation(), rightBreastPhysics.getBounceRotation());
		}
		breastSize = bSize * 1.5f;
		if(breastSize > 0.7f) breastSize = 0.7f;
		if(bSize > 0.7f) breastSize = bSize;
		if(breastSize < 0.02f) return false;

		zOffset = 0.0625f - (bSize * 0.0625f);
		breastSize = bSize + 0.5f * Math.abs(bSize - 0.7f) * 2f;

		float resistance = MathHelper.clamp(genderArmor.physicsResistance(), 0, 1);
		//Note: We only check if the breathing animation should be enabled if the chestplate's physics resistance
		// is less than or equal to 0.5 so that if we won't be rendering it we can avoid doing extra calculations
		breathingAnimation = ((entityConfig.getArmorPhysicsOverride() || resistance <= 0.5F) &&
				(!entity.isSubmergedInWater() || StatusEffectUtil.hasWaterBreathing(entity) ||
						entity.getWorld().getBlockState(new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ())).isOf(Blocks.BUBBLE_COLUMN)));
		bounceEnabled = entityConfig.hasBreastPhysics() && (!isChestplateOccupied || resistance < 1); //oh, you found this?
		return true;
	}

	protected void setupTransformations(T entity, ModelPart body, MatrixStack matrixStack, BreastSide side) {
		boolean left = side == BreastSide.LEFT;
		matrixStack.translate(body.pivotX * 0.0625f, body.pivotY * 0.0625f, body.pivotZ * 0.0625f);
		if(body.roll != 0.0F) {
			matrixStack.multiply(new Quaternionf().rotationXYZ(0f, 0f, body.roll));
		}
		if(body.yaw != 0.0F) {
			matrixStack.multiply(new Quaternionf().rotationXYZ(0f, body.yaw, 0f));
		}
		if(body.pitch != 0.0F) {
			matrixStack.multiply(new Quaternionf().rotationXYZ(body.pitch, 0f, 0f));
		}

		if(bounceEnabled) {
			matrixStack.translate((left ? lPhysPositionX : rTotalX) / 32f, 0, 0);
			matrixStack.translate(0, (left ? lPhysPositionY : rPhysPositionY) / 32f, 0);
		}

		matrixStack.translate((left ? breastOffsetX : -breastOffsetX) * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f), zOffset - 0.0625f * 2f + (breastOffsetZ * 0.0625f)); //shift down to correct position

		if(!breasts.isUniboob()) {
			matrixStack.translate(-0.0625f * 2 * (left ? 1 : -1), 0, 0);
		}
		if(bounceEnabled) {
			matrixStack.multiply(new Quaternionf().rotationXYZ(0, (float)((left ? lPhysBounceRotation : rPhysBounceRotation) * (Math.PI / 180f)), 0));
		}
		if(!breasts.isUniboob()) {
			matrixStack.translate(0.0625f * 2 * (left ? 1 : -1), 0, 0);
		}

		float rotationMultiplier = 0;
		if(bounceEnabled) {
			matrixStack.translate(0, -0.035f * breastSize, 0); //shift down to correct position
			rotationMultiplier = -(left ? lPhysPositionY : rPhysPositionY) / 12f;
		}
		float totalRotation = breastSize + rotationMultiplier;
		if(!bounceEnabled) {
			totalRotation = breastSize;
		}
		if(totalRotation > breastSize + 0.2F) {
			totalRotation = breastSize + 0.2F;
		}
		totalRotation = Math.min(totalRotation, 1); //hard limit for MAX

		if(isChestplateOccupied) {
			matrixStack.translate(0, 0, 0.01f);
		}

		matrixStack.multiply(new Quaternionf().rotationXYZ(0, (float)((left ? outwardAngle : -outwardAngle) * (Math.PI / 180f)), 0));
		matrixStack.multiply(new Quaternionf().rotationXYZ((float)(-35f * totalRotation * (Math.PI / 180f)), 0, 0));

		if(breathingAnimation) {
			float f5 = -MathHelper.cos(entity.age * 0.09F) * 0.45F + 0.45F;
			matrixStack.multiply(new Quaternionf().rotationXYZ((float)(f5 * (Math.PI / 180f)), 0, 0));
		}

		matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX
	}

	private void renderBreast(T entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn, int packedOverlayIn, BreastSide side) {
		RenderLayer breastRenderType = getRenderLayer(entity);
		if(breastRenderType == null) return; // only render if the player is visible in some capacity
		float alpha = entity.isInvisible() ? 0.15F : 1;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(breastRenderType);
		renderBox(side == BreastSide.LEFT ? lBreast : rBreast, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, 1f, 1f, 1f, alpha);
		if(entity instanceof AbstractClientPlayerEntity player && player.isPartVisible(PlayerModelPart.JACKET)) {
			matrixStack.translate(0, 0, -0.015f);
			matrixStack.scale(1.05f, 1.05f, 1.05f);
			renderBox(side == BreastSide.LEFT ? lBreastWear : rBreastWear, matrixStack, vertexConsumer, packedLightIn, packedOverlayIn, 1f, 1f, 1f, alpha);
		}
	}

	protected static void renderBox(WildfireModelRenderer.ModelBox model, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
	                                float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		Matrix3f matrix3f = matrixStack.peek().getNormalMatrix();
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
