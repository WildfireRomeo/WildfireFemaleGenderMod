package com.wildfire.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.*;
/*import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModelColorPower;
import io.github.apace100.origins.component.OriginComponent;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;*/
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GenderLayer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

	private WildfireModelRenderer.BreastModelBox lBreast;
	private WildfireModelRenderer.OverlayModelBox lBreastWear;
	private WildfireModelRenderer.BreastModelBox rBreast;
	private WildfireModelRenderer.OverlayModelBox rBreastWear;

	private WildfireModelRenderer.ModelBox sBox;
	private WildfireModelRenderer.BreastModelBox rBoobArmor, lBoobArmor;

	private float preBreastSize = 0f;

	public GenderLayer(FeatureRendererContext render) {
		super(render);

		lBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		rBreast = new WildfireModelRenderer.BreastModelBox(64, 64, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.0F, false);
		lBreastWear = new WildfireModelRenderer.OverlayModelBox(true,64, 64, 17, 34, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBreastWear = new WildfireModelRenderer.OverlayModelBox(false,64, 64, 21, 34, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);

		lBoobArmor = new WildfireModelRenderer.BreastModelBox(64, 32, 16, 19, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new WildfireModelRenderer.BreastModelBox(64, 32, 20, 19, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		sBox = new WildfireModelRenderer.ModelBox(64, 32, 17, 19, -4F, 0.0F, 0F, 8, 5, 3, 0.0F, false);

	}

	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = new HashMap<String, Identifier>();
	public Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay) {

		String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
		return (Identifier) ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int packedLightIn, AbstractClientPlayerEntity ent, float limbAngle, float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		//Surround with a try/catch to fix for essential mod.
		if(ent == null) return;
		try {
			//0.5 or 0
			String playerName = ent.getUuid().toString();
			GenderPlayer plr = WildfireGender.getPlayerByName(playerName);
			if(plr == null) return;

			PlayerEntityRenderer rend = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(ent);
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

			//recalculate breast sizes if it's changed.
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

			/*if (FabricLoader.getInstance().isModLoaded("origins")) {
				//Origins Color/Translucency Compatibility
				List<ModelColorPower> modelColorPowers = PowerHolderComponent.getPowers(ent, ModelColorPower.class);
				if (modelColorPowers.size() > 0) {
					float r = modelColorPowers.stream().map(ModelColorPower::getRed).reduce((a, b) -> a * b).get();
					float g = modelColorPowers.stream().map(ModelColorPower::getGreen).reduce((a, b) -> a * b).get();
					float b = modelColorPowers.stream().map(ModelColorPower::getBlue).reduce((a, c) -> a * c).get();
					float a = Math.min(getTransparency(ent), modelColorPowers.stream().map(ModelColorPower::getAlpha).min(Float::compare).get());
					overlayRed = r;
					overlayGreen = g;
					overlayBlue = b;
					overlayAlpha = a;
				}
			}*/


			if (plr != null) {

				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

				float lTotal = MathHelper.lerp(partialTicks, plr.getLeftBreastPhysics().getPreBounceY(), plr.getLeftBreastPhysics().getBounceY());
				float lTotalX = MathHelper.lerp(partialTicks, plr.getLeftBreastPhysics().getPreBounceX(), plr.getLeftBreastPhysics().getBounceX());
				float leftBounceRotation = MathHelper.lerp(partialTicks, plr.getLeftBreastPhysics().getPreBounceRotation(), plr.getLeftBreastPhysics().getBounceRotation());
				float rTotal = MathHelper.lerp(partialTicks, plr.getRightBreastPhysics().getPreBounceY(), plr.getRightBreastPhysics().getBounceY());
				float rTotalX = MathHelper.lerp(partialTicks, plr.getRightBreastPhysics().getPreBounceX(), plr.getRightBreastPhysics().getBounceX());
				float rightBounceRotation = MathHelper.lerp(partialTicks, plr.getRightBreastPhysics().getPreBounceRotation(), plr.getRightBreastPhysics().getBounceRotation());
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

				ItemStack armorStack = ent.getInventory().getArmorStack(2);

				try {
					//System.out.println(net.optifine.CustomItems.getCustomArmorTexture(armorStack, 2, "0"));
					//Identifier test = WFGMOptifineMixin.getCustomArmorLocation(armorStack, 2, "0");
				} catch(Exception e) {
					e.printStackTrace();
				}

				if (FabricLoader.getInstance().isModLoaded("cosmetic-armor")) {
					Map<String, SlotGroup> slotMap = TrinketsApi.getPlayerSlots();
					SlotGroup slotGroup = slotMap.get("chest");
					Map<String, SlotType> slotGroupMap = slotGroup.getSlots();
					SlotType slotType = slotGroupMap.get("cosmetic");
					TrinketComponent trinketComponent = TrinketsApi.getTrinketComponent(ent).get();
					for (int i = 0; i < trinketComponent.getAllEquipped().size(); i++) {
						TrinketInventory trinketChestInventory = trinketComponent.getAllEquipped().get(i).getLeft().inventory();
						if (trinketChestInventory.getSlotType() == slotType) {
							i = trinketComponent.getAllEquipped().size();
							if (trinketChestInventory.getStack(0) != ItemStack.EMPTY) {
								armorStack = trinketChestInventory.getStack(0);
							}
						}
					}
				}

				boolean isChestplateOccupied =
						!armorStack.isItemEqual(new ItemStack(Items.ELYTRA, 1)) &&
								!ItemStack.areEqual(armorStack, new ItemStack(Items.AIR, 1));

				if (breastSize < 0.02f || (!plr.show_in_armor && isChestplateOccupied)) return;

				float zOff = 0.0625f - (plr.getLeftBreastPhysics().getBreastSize(partialTicks) * 0.0625f);
				breastSize = plr.getLeftBreastPhysics().getBreastSize(partialTicks) + 0.5f * Math.abs(plr.getLeftBreastPhysics().getBreastSize(partialTicks) - 0.7f) * 2f;

				//matrixStack.translate(0, 0, zOff);
				//System.out.println(bounceRotation);


				boolean teamSeeFriendly = false;
				if (ent.getScoreboardTeam() != null)
					teamSeeFriendly = ent.getScoreboardTeam().shouldShowFriendlyInvisibles();

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
				if (bounceEnabled) matrixStack.multiply(new Quaternion(0, leftBounceRotation, 0, true));
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

				matrixStack.multiply(new Quaternion(0, outwardAngle, 0, true));
				matrixStack.multiply(new Quaternion(-35f * totalRotation, 0, 0, true));

				if (!isChestplateOccupied && breathingAnimation) {
					float f5 = -MathHelper.cos(ent.age * 0.09F) * 0.45F + 0.45F;
					matrixStack.multiply(new Quaternion(f5, 0, 0, true));
				}

				matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX


				int combineTex = LivingEntityRenderer.getOverlay(ent, 0);
				RenderLayer type = RenderLayer.getEntityTranslucent(ent.getSkinTexture());
				VertexConsumer ivertexbuilder = vertexConsumers.getBuffer(type);

				if ((teamSeeFriendly && ent.isInvisible()) || !ent.isInvisible()) {
					renderBox(lBreast, matrixStack, ivertexbuilder, packedLightIn, combineTex, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					if (ent.isPartVisible(PlayerModelPart.JACKET)) {
						matrixStack.translate(0, 0, -0.015f * 1f);
						matrixStack.scale(1.05f, 1.05f, 1.05f);
						renderBox(lBreastWear, matrixStack, ivertexbuilder, packedLightIn, combineTex, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					}


					//RIGHT BOOB ARMOR
					if (!ItemStack.areEqual(armorStack, new ItemStack(Items.AIR, 1)) && !(armorStack.getItem() instanceof ElytraItem)) {

						Identifier ARMOR_TXTR = getOptifineArmorTexture(armorStack);
						if (ARMOR_TXTR != null) {
							if (armorStack.getItem() instanceof ArmorItem) {
								matrixStack.push();

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
									RenderLayer type2 = RenderLayer.getArmorCutoutNoCull(ARMOR_TXTR);
									VertexConsumer ivertexbuilder2 = vertexConsumers.getBuffer(type2);
									renderBox(lBoobArmor, matrixStack, ivertexbuilder2, packedLightIn, 0xFFFFFF, armorR, armorG, armorB, 1f);

									if (armorStack.hasEnchantments()) {
										RenderLayer type3 = RenderLayer.getArmorEntityGlint();
										VertexConsumer ivertexbuilder3 = vertexConsumers.getBuffer(type3);
										renderBox(lBoobArmor, matrixStack, ivertexbuilder3, packedLightIn, 0xFFFFFF, 1f, 1f, 1f, 1f);
									}
									matrixStack.pop();
								}
							}
						}
					}
				}

				matrixStack.pop();


				pushMatrix(matrixStack, rend.getModel().body, 0);
				//left breast
				if (bounceEnabled) {
					matrixStack.translate(rTotalX / 32f, 0, 0);
					matrixStack.translate(0, rTotal / 32f, 0);
				}


				matrixStack.translate(-breastOffsetX * 0.0625f, 0.05625f + (breastOffsetY * 0.0625f), zOff - 0.0625f * 2f + (breastOffsetZ * 0.0625f)); //shift down to correct position
				if (!plr.getBreasts().isUniboob) matrixStack.translate(0.0625f * 2, 0, 0);
				if (bounceEnabled) matrixStack.multiply(new Quaternion(0, rightBounceRotation, 0, true));
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

				matrixStack.multiply(new Quaternion(0, -outwardAngle, 0, true));
				matrixStack.multiply(new Quaternion(-35f * totalRotation2, 0, 0, true));

				if (!isChestplateOccupied && breathingAnimation) {
					float f5 = -MathHelper.cos(ent.age * 0.09F) * 0.45F + 0.45F;
					matrixStack.multiply(new Quaternion(f5, 0, 0, true));
				}

				matrixStack.scale(0.9995f, 1f, 1f); //z-fighting FIXXX

				int combineTexR = LivingEntityRenderer.getOverlay(ent, 0);
				RenderLayer typeR = RenderLayer.getEntityTranslucent(rend.getTexture(ent));
				VertexConsumer ivertexbuilderR = vertexConsumers.getBuffer(typeR);

				if ((teamSeeFriendly && ent.isInvisible()) || !ent.isInvisible()) {
					renderBox(rBreast, matrixStack, ivertexbuilderR, packedLightIn, combineTexR, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					if (ent.isPartVisible(PlayerModelPart.JACKET)) {
						matrixStack.translate(0, 0, -0.015f * 1f);
						matrixStack.scale(1.05f, 1.05f, 1.05f);
						renderBox(rBreastWear, matrixStack, ivertexbuilderR, packedLightIn, combineTexR, overlayRed, overlayGreen, overlayBlue, overlayAlpha);
					}


					//LEFT? BOOB ARMOR
					if (!ItemStack.areEqual(armorStack, new ItemStack(Items.AIR, 1)) && !(armorStack.getItem() instanceof ElytraItem)) {
						Identifier ARMOR_TXTR = getOptifineArmorTexture(armorStack);
						if (ARMOR_TXTR != null) {
							if (armorStack.getItem() instanceof ArmorItem) {
								matrixStack.push();

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
									RenderLayer type2 = RenderLayer.getArmorCutoutNoCull(ARMOR_TXTR);
									VertexConsumer ivertexbuilder2 = vertexConsumers.getBuffer(type2);
									renderBox(rBoobArmor, matrixStack, ivertexbuilder2, packedLightIn, 0xFFFFFF, armorR, armorG, armorB, getTransparency(ent));

									if (armorStack.hasEnchantments()) {
										RenderLayer type3 = RenderLayer.getArmorEntityGlint();
										VertexConsumer ivertexbuilder3 = vertexConsumers.getBuffer(type3);
										renderBox(rBoobArmor, matrixStack, ivertexbuilder3, packedLightIn, 0xFFFFFF, 1f, 1f, 1f, getTransparency(ent));
									}
									matrixStack.pop();
								}
							}
						}
					}
				}

				matrixStack.pop(); //pop right breast
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private Identifier getOptifineArmorTexture(ItemStack itemStack) {
		if(itemStack.getItem() instanceof ArmorItem) {
			return getArmorTexture((ArmorItem) itemStack.getItem(), false, null);
		}
		return null;
	}

	public static float getTransparency(LivingEntity ent) {
		float alphaChannel = 1f;
		boolean flag1 = ent.isInvisible() && !ent.isInvisibleTo(MinecraftClient.getInstance().player);
		if(flag1) alphaChannel = 0.15f; else if(ent.isInvisible()) alphaChannel = 0;
		return alphaChannel;
	}

	public static void pushMatrix(MatrixStack m, ModelPart mdl, float f7) {

		float rPointX = mdl.pivotX;
		float rPointY = mdl.pivotY;
		float rPointZ = mdl.pivotZ;
		float rAngleX = (float) (mdl.pitch);
		float rAngleY = (float) (mdl.yaw);
		float rAngleZ = (float) (mdl.roll);

		m.push();

		m.translate(rPointX * 0.0625f, rPointY * 0.0625f, rPointZ * 0.0625f);
		if (rAngleZ != 0.0F) {

			m.multiply(new Quaternion(0f, 0f, rAngleZ, false));
		}

		if (rAngleY != 0.0F) {
			m.multiply(new Quaternion(0f, rAngleY, 0f, false));
		}

		if (rAngleX != 0.0F) {
			m.multiply(new Quaternion(rAngleX, 0f, 0f, false));
		}
	}


	public static void renderBox(WildfireModelRenderer.BreastModelBox cuboid, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		MatrixStack.Entry matrixEntryIn = matrixStack.peek();
		Matrix4f matrix4f = matrixEntryIn.getPositionMatrix();
		Matrix3f matrix3f = matrixEntryIn.getNormalMatrix();

		WildfireModelRenderer.TexturedQuad[] var13 = cuboid.quads;
		int var14 = var13.length;

		for(int var15 = 0; var15 < var14; ++var15) {
			WildfireModelRenderer.TexturedQuad quad = var13[var15];
			Vec3f vector3f = quad.normal.copy();
			vector3f.transform(matrix3f);
			float f = vector3f.getX();
			float g = vector3f.getY();
			float h = vector3f.getZ();

			for(int i = 0; i < 4; ++i) {
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

	public static void renderBox(WildfireModelRenderer.OverlayModelBox cuboid, MatrixStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
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

