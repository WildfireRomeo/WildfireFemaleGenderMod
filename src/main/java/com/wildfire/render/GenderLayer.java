package com.wildfire.render;
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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	private WildfireModelRenderer.BreastModelBox lBreast;
	private WildfireModelRenderer.OverlayModelBox lBreastWear;
	private WildfireModelRenderer.BreastModelBox rBreast;
	private WildfireModelRenderer.OverlayModelBox rBreastWear;

	private WildfireModelRenderer.ModelBox sBox;
	private WildfireModelRenderer.BreastModelBox rBoobArmor, lBoobArmor;

	private float preBreastSize = 0f;

	public GenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> player) {
		super(player);

		lBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		rBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		lBreastWear = new WildfireModelRenderer.OverlayModelBox(true,64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBreastWear = new WildfireModelRenderer.OverlayModelBox(false,64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);

		lBoobArmor = new WildfireModelRenderer.BreastModelBox(64, 32, 16, 19, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new WildfireModelRenderer.BreastModelBox(64, 32, 20, 19, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		//chest = new SteinModelRenderer.ModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 8, 5, 4, 0.0F, false);
		//chestwear = new SteinModelRenderer.ModelBox(64, 64, 17, 34, -4F, 0.0F, 0F, 8, 5, 3, 0.0F, false);
		sBox = new WildfireModelRenderer.ModelBox(64, 32, 17, 19, -4F, 0.0F, 0F, 8, 5, 3, 0.0F, false);

	}

	private static final Map<String, ResourceLocation> ARMOR_TEXTURE_CACHE = new HashMap<String, ResourceLocation>();
	private ResourceLocation getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay) {
		String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
		return (ResourceLocation) ARMOR_TEXTURE_CACHE.computeIfAbsent(string, ResourceLocation::new);
	}

	@Override
	public void render(PoseStack matrixStack, MultiBufferSource vertexConsumers, int packedLightIn, AbstractClientPlayer ent, float limbAngle, float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		//Surround with a try/catch to fix for essential mod.
		if(ent == null) return;
		try {
			//0.5 or 0
			String playerName = ent.getStringUUID();
			//System.out.println(ent.getUuid().toString());
			GenderPlayer plr = WildfireGender.getPlayerByName(playerName);
			if(plr == null) return;

			PlayerRenderer rend = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ent);
			PlayerModel<AbstractClientPlayer> model = rend.getModel();

			float breastOffsetX = Math.round((Math.round(plr.getBreasts().xOffset * 100f) / 100f) * 10) / 10f;
			float breastOffsetY = -Math.round((Math.round(plr.getBreasts().yOffset * 100f) / 100f) * 10) / 10f;
			float breastOffsetZ = -Math.round((Math.round(plr.getBreasts().zOffset * 100f) / 100f) * 10) / 10f;

			float bSize = plr.getLeftBreastPhysics().getBreastSize(partialTicks);
			float outwardAngle = (Math.round(plr.getBreasts().cleavage * 100f) / 100f) * 100f;
			outwardAngle = Math.min(outwardAngle, 10);
			boolean uniboob = plr.getBreasts().isUniboob;


			float reducer = 0;
			if (bSize < 0.84f) reducer++;
			if (bSize < 0.72f) reducer++;

			if(preBreastSize != bSize) {
				lBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
				rBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, (int) (4 - breastOffsetZ - reducer), 0.0F, false);
				lBoobArmor = new WildfireModelRenderer.BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
				rBoobArmor = new WildfireModelRenderer.BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
				preBreastSize = bSize;
			}

			//DEPENDENCIES
			float overlayRed = 1;
			float overlayGreen = 1;
			float overlayBlue = 1;
			float overlayAlpha = getTransparency(ent);


			if (plr != null) {

				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

				float lTotal = Mth.lerp(partialTicks, plr.getLeftBreastPhysics().getPreBounceY(), plr.getLeftBreastPhysics().getBounceY());
				float lTotalX = Mth.lerp(partialTicks, plr.getLeftBreastPhysics().getPreBounceX(), plr.getLeftBreastPhysics().getBounceX());
				float leftBounceRotation = Mth.lerp(partialTicks, plr.getLeftBreastPhysics().getPreBounceRotation(), plr.getLeftBreastPhysics().getBounceRotation());
				float rTotal = Mth.lerp(partialTicks, plr.getRightBreastPhysics().getPreBounceY(), plr.getRightBreastPhysics().getBounceY());
				float rTotalX = Mth.lerp(partialTicks, plr.getRightBreastPhysics().getPreBounceX(), plr.getRightBreastPhysics().getBounceX());
				float rightBounceRotation = Mth.lerp(partialTicks, plr.getRightBreastPhysics().getPreBounceRotation(), plr.getRightBreastPhysics().getBounceRotation());
				if (uniboob) {
					rTotal = lTotal;
					rTotalX = lTotalX;
					rightBounceRotation = leftBounceRotation;
				}
				float breastSize = plr.getLeftBreastPhysics().getBreastSize(partialTicks) * 1.5f;
				if (breastSize > 0.7f) breastSize = 0.7f;
				if (plr.getLeftBreastPhysics().getBreastSize(partialTicks) > 0.7f) {
					breastSize = plr.getLeftBreastPhysics().getBreastSize(partialTicks);
				}

				ItemStack armorStack = ent.getInventory().getArmor(2);

				boolean isChestplateOccupied =
						!ent.getItemBySlot(EquipmentSlot.CHEST).equals(new ItemStack(Items.ELYTRA, 1), true) &&
								!(ent.getItemBySlot(EquipmentSlot.CHEST).equals(new ItemStack(Items.AIR, 1), true));

				if (breastSize < 0.02f || (!plr.show_in_armor && isChestplateOccupied)) return;

				float zOff = 0.0625f - (plr.getLeftBreastPhysics().getBreastSize(partialTicks) * 0.0625f);
				breastSize = plr.getLeftBreastPhysics().getBreastSize(partialTicks) + 0.5f * Math.abs(plr.getLeftBreastPhysics().getBreastSize(partialTicks) - 0.7f) * 2f;

				//matrixStack.translate(0, 0, zOff);
				//System.out.println(bounceRotation);


				boolean teamSeeFriendly = false;
				if (ent.getTeam() != null)
					teamSeeFriendly = ent.getTeam().canSeeFriendlyInvisibles();

				boolean breathingAnimation = true;
				float rotationMultiplier = 0;
				boolean bounceEnabled = (plr.breast_physics && !isChestplateOccupied) || (plr.breast_physics && plr.breast_physics_armor && isChestplateOccupied); //oh, you found this?

				pushMatrix(matrixStack, rend.getModel().body, 0);
				//right breast
				if (bounceEnabled) {
					matrixStack.translate(lTotalX / 32f, 0, 0);
					matrixStack.translate(0, lTotal / 32f, 0);
				}

				matrixStack.translate(breastOffsetX * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f), zOff - 0.0625f * 2f + (breastOffsetZ * 0.0625f)); //shift down to correct position

				if (!plr.getBreasts().isUniboob) matrixStack.translate(-0.0625f * 2, 0, 0);
				if (bounceEnabled) matrixStack.mulPose(new Quaternion(0, leftBounceRotation, 0, true));
				if (!plr.getBreasts().isUniboob) matrixStack.translate(0.0625f * 2, 0, 0);

				if (bounceEnabled) {
					matrixStack.translate(0, -0.035f * breastSize, 0); //shift down to correct position
					rotationMultiplier = -lTotal / 12f;
				}
				float totalRotation = breastSize + rotationMultiplier;
				if (!bounceEnabled) {
					totalRotation = breastSize;
				}
				if (totalRotation > breastSize + 0.2f) totalRotation = breastSize + 0.2f;
				if (totalRotation > 1) totalRotation = 1; //hard limit for MAX

				if (isChestplateOccupied) matrixStack.translate(0, 0, 0.01f);

				matrixStack.mulPose(new Quaternion(0, outwardAngle, 0, true));
				matrixStack.mulPose(new Quaternion(-35f * totalRotation, 0, 0, true));

				if (!isChestplateOccupied && breathingAnimation) {
					float f5 = -Mth.cos(ent.tickCount * 0.09F) * 0.45F + 0.45F;
					matrixStack.mulPose(new Quaternion(f5, 0, 0, true));
				}

				matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX


				int combineTex = LivingEntityRenderer.getOverlayCoords(ent, 0);
				RenderType type = RenderType.entityTranslucent(ent.getSkinTextureLocation());
				VertexConsumer ivertexbuilder = vertexConsumers.getBuffer(type);

				if ((teamSeeFriendly && ent.isInvisible()) || !ent.isInvisible()) {
					renderBox(lBreast, matrixStack, ivertexbuilder, packedLightIn, combineTex, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					if (ent.isModelPartShown(PlayerModelPart.JACKET)) {
						matrixStack.translate(0, 0, -0.015f * 1f);
						matrixStack.scale(1.05f, 1.05f, 1.05f);
						renderBox(lBreastWear, matrixStack, ivertexbuilder, packedLightIn, combineTex, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					}


					//RIGHT BOOB ARMOR
					if (!ItemStack.isSame(armorStack, new ItemStack(Items.AIR, 1)) && !(armorStack.getItem() instanceof ElytraItem) && !(armorStack.getItem() instanceof AirItem)) {

						ResourceLocation ARMOR_TXTR = getArmorTexture((ArmorItem) armorStack.getItem(), false, null);
						if (ARMOR_TXTR != null) {
							if (armorStack.getItem() instanceof ArmorItem) {
								matrixStack.pushPose();

								float armorR = 1f;
								float armorG = 1f;
								float armorB = 1f;

								if (!(armorStack.getItem() instanceof ElytraItem)) {
									ArmorItem armoritem = (ArmorItem) armorStack.getItem();
									if (armoritem instanceof DyeableArmorItem) {
										int i = ((DyeableArmorItem) armoritem).getColor(armorStack);
										armorR = (float) (i >> 16 & 255) / 255.0F;
										armorG = (float) (i >> 8 & 255) / 255.0F;
										armorB = (float) (i & 255) / 255.0F;

									}
									matrixStack.translate(0.001f, 0.015f * 1f, -0.015f * 1f);
									matrixStack.scale(1.05f, 1, 1);
									RenderType type2 = RenderType.armorCutoutNoCull(ARMOR_TXTR);
									VertexConsumer ivertexbuilder2 = vertexConsumers.getBuffer(type2);
									renderBox(lBoobArmor, matrixStack, ivertexbuilder2, packedLightIn, 0xFFFFFF, armorR, armorG, armorB, 1f);

									if (armorStack.isEnchanted()) {
										RenderType type3 = RenderType.armorEntityGlint();
										VertexConsumer ivertexbuilder3 = vertexConsumers.getBuffer(type3);
										renderBox(lBoobArmor, matrixStack, ivertexbuilder3, packedLightIn, 0xFFFFFF, 1f, 1f, 1f, 1f);
									}
									matrixStack.popPose();
								}
							}
						}
					}
				}

				matrixStack.popPose();;


				pushMatrix(matrixStack, rend.getModel().body, 0);
				//left breast
				if (bounceEnabled) {
					matrixStack.translate(rTotalX / 32f, 0, 0);
					matrixStack.translate(0, rTotal / 32f, 0);
				}


				matrixStack.translate(-breastOffsetX * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f), zOff - 0.0625f * 2f + (breastOffsetZ * 0.0625f)); //shift down to correct position
				if (!plr.getBreasts().isUniboob) matrixStack.translate(0.0625f * 2, 0, 0);
				if (bounceEnabled) matrixStack.mulPose(new Quaternion(0, rightBounceRotation, 0, true));
				if (!plr.getBreasts().isUniboob) matrixStack.translate(-0.0625f * 2, 0, 0);

				if (bounceEnabled) {
					matrixStack.translate(0, -0.035f * breastSize, 0); //shift down to correct position
					rotationMultiplier = -rTotal / 12f;
				}
				float totalRotation2 = breastSize + rotationMultiplier;
				if (!bounceEnabled) {
					totalRotation2 = breastSize;
				}
				if (totalRotation2 > breastSize + 0.2f) totalRotation2 = breastSize + 0.2f;
				if (totalRotation2 > 1) totalRotation2 = 1; //hard limit for MAX

				if (isChestplateOccupied) matrixStack.translate(0, 0, 0.01f);

				matrixStack.mulPose(new Quaternion(0, -outwardAngle, 0, true));
				matrixStack.mulPose(new Quaternion(-35f * totalRotation2, 0, 0, true));

				if (!isChestplateOccupied && breathingAnimation) {
					float f5 = -Mth.cos(ent.tickCount * 0.09F) * 0.45F + 0.45F;
					matrixStack.mulPose(new Quaternion(f5, 0, 0, true));
				}

				matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX

				int combineTexR = LivingEntityRenderer.getOverlayCoords(ent, 0);
				RenderType typeR = RenderType.entityTranslucent(rend.getTextureLocation(ent));
				VertexConsumer ivertexbuilderR = vertexConsumers.getBuffer(typeR);

				if ((teamSeeFriendly && ent.isInvisible()) || !ent.isInvisible()) {
					renderBox(rBreast, matrixStack, ivertexbuilderR, packedLightIn, combineTexR, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					if (ent.isModelPartShown(PlayerModelPart.JACKET)) {
						matrixStack.translate(0, 0, -0.015f * 1f);
						matrixStack.scale(1.05f, 1.05f, 1.05f);
						renderBox(rBreastWear, matrixStack, ivertexbuilderR, packedLightIn, combineTexR, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					}


					//LEFT? BOOB ARMOR
					if (!ItemStack.isSame(armorStack, new ItemStack(Items.AIR, 1)) && !(armorStack.getItem() instanceof ElytraItem) && !(armorStack.getItem() instanceof AirItem)) {
						ResourceLocation ARMOR_TXTR = getArmorTexture((ArmorItem) armorStack.getItem(), false, null);
						if (ARMOR_TXTR != null) {
							if (armorStack.getItem() instanceof ArmorItem) {
								matrixStack.pushPose();

								float armorR = 1f;
								float armorG = 1f;
								float armorB = 1f;

								if (!(armorStack.getItem() instanceof ElytraItem)) {
									ArmorItem armoritem = (ArmorItem) armorStack.getItem();
									if (armoritem instanceof DyeableArmorItem) {
										int i = ((DyeableArmorItem) armoritem).getColor(armorStack);
										armorR = (float) (i >> 16 & 255) / 255.0F;
										armorG = (float) (i >> 8 & 255) / 255.0F;
										armorB = (float) (i & 255) / 255.0F;

									}
									matrixStack.translate(-0.001f, 0.015f * 1f, -0.015f * 1f);
									matrixStack.scale(1.05f, 1, 1);
									RenderType type2 = RenderType.armorCutoutNoCull(ARMOR_TXTR);
									VertexConsumer ivertexbuilder2 = vertexConsumers.getBuffer(type2);
									renderBox(rBoobArmor, matrixStack, ivertexbuilder2, packedLightIn, 0xFFFFFF, armorR, armorG, armorB, getTransparency(ent));

									if (armorStack.isEnchanted()) {
										RenderType type3 = RenderType.armorEntityGlint();
										VertexConsumer ivertexbuilder3 = vertexConsumers.getBuffer(type3);
										renderBox(rBoobArmor, matrixStack, ivertexbuilder3, packedLightIn, 0xFFFFFF, 1f, 1f, 1f, getTransparency(ent));
									}
									matrixStack.popPose();
								}
							}
						}
					}
				}

				matrixStack.popPose(); //pop right breast
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static float getTransparency(LivingEntity ent) {
		float alphaChannel = 1f;
		boolean flag1 = ent.isInvisible() && !ent.isInvisibleTo(Minecraft.getInstance().player);
		if(flag1) alphaChannel = 0.15f; else if(ent.isInvisible()) alphaChannel = 0;
		return alphaChannel;
	}

	public static void pushMatrix(PoseStack m, ModelPart mdl, float f7) {

		float rPointX = mdl.x;
		float rPointY = mdl.y;
		float rPointZ = mdl.z;
		float rAngleX = (float) (mdl.xRot);
		float rAngleY = (float) (mdl.yRot);
		float rAngleZ = (float) (mdl.zRot);

		m.pushPose();

		m.translate(rPointX * 0.0625f, rPointY * 0.0625f, rPointZ * 0.0625f);
		if (rAngleZ != 0.0F) {
			m.mulPose(new Quaternion(0f, 0f, rAngleZ, false));
		}

		if (rAngleY != 0.0F) {
			m.mulPose(new Quaternion(0f, rAngleY, 0f, false));
		}

		if (rAngleX != 0.0F) {
			m.mulPose(new Quaternion(rAngleX, 0f, 0f, false));
		}
	}



	public float getTransparency(AbstractClientPlayer ent) {
		float alphaChannel = 1f;
		boolean flag1 = ent.isInvisible() && !ent.isInvisibleTo(Minecraft.getInstance().player);
		if(flag1) alphaChannel = 0.15f; else if(ent.isInvisible()) alphaChannel = 0;
		return alphaChannel;
	}

	public static void renderBox(WildfireModelRenderer.ModelBox box, PoseStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStack.last().pose();
		Matrix3f matrix3f =	matrixStack.last().normal();

		WildfireModelRenderer.TexturedQuad[] var13 = box.quads;
		int var14 = var13.length;

		for(int var15 = 0; var15 < var14; ++var15) {
			WildfireModelRenderer.TexturedQuad quad = var13[var15];
			Vector3f vector3f = new Vector3f(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());
			vector3f.transform(matrix3f);
			float f = vector3f.x();
			float g = vector3f.y();
			float h = vector3f.z();

			for(int i = 0; i < 4; ++i) {
				WildfireModelRenderer.PositionTextureVertex vertex = quad.vertexPositions[i];
				float j = vertex.vector3D.x() / 16.0F;
				float k = vertex.vector3D.y() / 16.0F;
				float l = vertex.vector3D.z() / 16.0F;
				Vector4f vector4f = new Vector4f(j, k, l, 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.texturePositionX, vertex.texturePositionY, packedOverlayIn, packedLightIn, f, g, h);
			}
		}
	}

	public static void renderBox(WildfireModelRenderer.BreastModelBox box, PoseStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStack.last().pose();
		Matrix3f matrix3f =	matrixStack.last().normal();

		WildfireModelRenderer.TexturedQuad[] var13 = box.quads;
		int var14 = var13.length;

		for(int var15 = 0; var15 < var14; ++var15) {
			WildfireModelRenderer.TexturedQuad quad = var13[var15];
			Vector3f vector3f = new Vector3f(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());
			vector3f.transform(matrix3f);
			float f = vector3f.x();
			float g = vector3f.y();
			float h = vector3f.z();

			for(int i = 0; i < 4; ++i) {
				WildfireModelRenderer.PositionTextureVertex vertex = quad.vertexPositions[i];
				float j = vertex.vector3D.x() / 16.0F;
				float k = vertex.vector3D.y() / 16.0F;
				float l = vertex.vector3D.z() / 16.0F;
				Vector4f vector4f = new Vector4f(j, k, l, 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.texturePositionX, vertex.texturePositionY, packedOverlayIn, packedLightIn, f, g, h);
			}
		}
	}

	public static void renderBox(WildfireModelRenderer.OverlayModelBox box, PoseStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStack.last().pose();
		Matrix3f matrix3f =	matrixStack.last().normal();

		WildfireModelRenderer.TexturedQuad[] var13 = box.quads;
		int var14 = var13.length;

		for(int var15 = 0; var15 < var14; ++var15) {
			WildfireModelRenderer.TexturedQuad quad = var13[var15];
			Vector3f vector3f = new Vector3f(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());
			vector3f.transform(matrix3f);
			float f = vector3f.x();
			float g = vector3f.y();
			float h = vector3f.z();

			for(int i = 0; i < 4; ++i) {
				WildfireModelRenderer.PositionTextureVertex vertex = quad.vertexPositions[i];
				float j = vertex.vector3D.x() / 16.0F;
				float k = vertex.vector3D.y() / 16.0F;
				float l = vertex.vector3D.z() / 16.0F;
				Vector4f vector4f = new Vector4f(j, k, l, 1.0F);
				vector4f.transform(matrix4f);
				bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.texturePositionX, vertex.texturePositionY, packedOverlayIn, packedLightIn, f, g, h);
			}
		}
	}

}

