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

package com.wildfire.physics;

import com.wildfire.api.IGenderArmor;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.WildfireHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.Vec3;

public class BreastPhysics {

	//X-Axis
	private float bounceVelX = 0, targetBounceX = 0, velocityX = 0, positionX, prePositionX;
	//Y-Axis
	private float bounceVel = 0, targetBounceY = 0, velocity = 0, positionY, prePositionY;
	//Rotation
	private float bounceRotVel = 0, targetRotVel = 0, rotVelocity = 0, wfg_bounceRotation, wfg_preBounceRotation;

	private boolean justSneaking = false, alreadySleeping = false;

	private float breastSize = 0, preBreastSize = 0;

	private Vec3 prePos;
	private final EntityConfig entityConfig;
	public BreastPhysics(EntityConfig entityConfig) {
		this.entityConfig = entityConfig;
	}

	private int randomB = 1;
	private boolean alreadyFalling = false;

	/**
	 * @apiNote Only call on the client
	 */
	public void update(LivingEntity entity, IGenderArmor armor) {
		if (entity instanceof ArmorStand && !armor.armorStandsCopySettings()) {
			// optimization: skip physics on armor stands that either don't have a chestplate,
			// or have a chestplate we wouldn't copy player settings to
			return;
		}

		this.prePositionY = this.positionY;
		this.prePositionX = this.positionX;
		this.wfg_preBounceRotation = this.wfg_bounceRotation;
		this.preBreastSize = this.breastSize;

		if(this.prePos == null) {
			this.prePos = entity.position();
			return;
		}

		float breastWeight = entityConfig.getBustSize() * 1.25f;
		float targetBreastSize = entityConfig.getBustSize();

		if (!entityConfig.getGender().canHaveBreasts()) {
			targetBreastSize = 0;
		} else if (!entityConfig.getArmorPhysicsOverride()) { //skip resistance if physics is overridden
			float tightness = Mth.clamp(armor.tightness(), 0, 1);
			//Scale breast size by how tight the armor is, clamping at a max adjustment of shrinking by 0.15
			targetBreastSize *= 1 - 0.15F * tightness;
		}

		breastSize += (breastSize < targetBreastSize) ? Math.abs(breastSize - targetBreastSize) / 2f : -Math.abs(breastSize - targetBreastSize) / 2f;


		Vec3 motion = entity.position().subtract(this.prePos);
		this.prePos = entity.position();

		float bounceIntensity = (targetBreastSize * 3f) * Math.round((entityConfig.getBounceMultiplier() * 3) * 100) / 100f;
		if (!entityConfig.getArmorPhysicsOverride()) { //skip resistance if physics is overridden
			float resistance = Mth.clamp(armor.physicsResistance(), 0, 1);
			//Adjust bounce intensity by physics resistance of the worn armor
			bounceIntensity *= 1 - resistance;
		}

		if(!entityConfig.getBreasts().isUniboob()) {
			bounceIntensity = bounceIntensity * WildfireHelper.randFloat(0.5f, 1.5f);
		}
		if(entity.fallDistance > 0 && !alreadyFalling) {
			randomB = entity.level().random.nextBoolean() ? -1 : 1;
			alreadyFalling = true;
		}
		if(entity.fallDistance == 0) alreadyFalling = false;


		this.targetBounceY = (float) motion.y * bounceIntensity;
		this.targetBounceY += breastWeight;
		this.targetRotVel = -((entity.yBodyRot - entity.yBodyRotO) / 15f) * bounceIntensity;


		float f = (float) entity.getDeltaMovement().lengthSqr() / 0.2F;
		f = f * f * f;

		if (f < 1.0F) {
			f = 1.0F;
		}

		this.targetBounceY += Mth.cos(entity.walkAnimation.position() * 0.6662F + (float)Math.PI) * 0.5F * entity.walkAnimation.speed() * 0.5F / f;
		//WildfireGender.logger.debug("Rotation yaw: {}", plr.rotationYaw);

		this.targetRotVel += (float) motion.y * bounceIntensity * randomB;


		if(entity.getPose() == Pose.CROUCHING && !this.justSneaking) {
			this.justSneaking = true;
			this.targetBounceY += bounceIntensity;
		}
		if(entity.getPose() != Pose.CROUCHING && this.justSneaking) {
			this.justSneaking = false;
			this.targetBounceY += bounceIntensity;
		}


		//button option for extra entities
		if(entity.getVehicle() != null) {
			if(entity.getVehicle() instanceof Boat boat) {
				int rowTime = (int) boat.getRowingTime(0, entity.walkAnimation.position());
				int rowTime2 = (int) boat.getRowingTime(1, entity.walkAnimation.position());

				float rotationL = (float)Mth.clampedLerp(-(float)Math.PI / 3F, -0.2617994F, (double)((Mth.sin(-rowTime2) + 1.0F) / 2.0F));
				float rotationR = (float)Mth.clampedLerp(-(float)Math.PI / 4F, (float)Math.PI / 4F, (double)((Mth.sin(-rowTime + 1.0F) + 1.0F) / 2.0F));
				//WildfireGender.logger.debug("{}, {}", rotationL, rotationR);
				if(rotationL < -1 || rotationR < -0.6f) {
					this.targetBounceY = bounceIntensity / 3.25f;
				}
			}

			if(entity.getVehicle() instanceof Minecart cart) {
				float speed = (float) cart.getDeltaMovement().lengthSqr();
				if(Math.random() * speed < 0.5f && speed > 0.2f) {
					this.targetBounceY = (Math.random() > 0.5 ? -bounceIntensity : bounceIntensity) / 6f;
				}
			}
			if(entity.getVehicle() instanceof AbstractHorse horse) {
				float movement = (float) horse.getDeltaMovement().length();
				if(horse.tickCount % clampMovement(movement) == 5 && movement > 0.1f) {
					this.targetBounceY = bounceIntensity / 4f;
				}
			}
			if(entity.getVehicle() instanceof Pig pig) {
				float movement = (float) pig.getDeltaMovement().length();
				if(pig.tickCount % clampMovement(movement) == 5 && movement > 0.08f) {
					this.targetBounceY = bounceIntensity / 4f;
				}
			}
			if(entity.getVehicle() instanceof Strider strider) {
				double heightOffset = (double) strider.getBbHeight() - 0.19
									  + (double) (0.12F * Mth.cos(strider.walkAnimation.position() * 1.5f)
												 * 2F * Math.min(0.25F, strider.walkAnimation.speed()));
				this.targetBounceY += ((float) (heightOffset * 3f) - 4.5f) * bounceIntensity;
			}
		}
		if(entity.swinging && entity.tickCount % 5 == 0 && entity.getPose() != Pose.SLEEPING) {
			this.targetBounceY += (Math.random() > 0.5 ? -0.25f : 0.25f) * bounceIntensity;
		}
		if(entity.getPose() == Pose.SLEEPING && !this.alreadySleeping) {
			this.targetBounceY = bounceIntensity;
			this.alreadySleeping = true;
		}
		if(entity.getPose() != Pose.SLEEPING && this.alreadySleeping) {
			this.targetBounceY = bounceIntensity;
			this.alreadySleeping = false;
		}
		/*if(plr.getPose() == EntityPose.SWIMMING) {
			//WildfireGender.logger.debug(1 - plr.getRotationVec(tickDelta).getY());
			rotationMultiplier = 1 - (float) plr.getRotationVec(tickDelta).getY();
		}
		*/


		float percent =  entityConfig.getFloppiness();
		float bounceAmount = 0.45f * (1f - percent) + 0.15f; //0.6f * percent - 0.15f;
		bounceAmount = Mth.clamp(bounceAmount, 0.15f, 0.6f);
		float delta = 2.25f - bounceAmount;
		//if(plr.isInWater()) delta = 0.75f - (1f * bounceAmount); //water resistance

		float distanceFromMin = Math.abs(bounceVel + 0.5f) * 0.5f;
		float distanceFromMax = Math.abs(bounceVel - 2.65f) * 0.5f;

		if(bounceVel < -0.5f) {
			targetBounceY += distanceFromMin;
		}
		if(bounceVel > 2.5f) {
			targetBounceY -= distanceFromMax;
		}
		if(targetBounceY < -1.5f) targetBounceY = -1.5f;
		if(targetBounceY > 2.5f) targetBounceY = 2.5f;
		if(targetRotVel < -25f) targetRotVel = -25f;
		if(targetRotVel > 25f) targetRotVel = 25f;

		this.velocity = Mth.lerp(bounceAmount, this.velocity, (this.targetBounceY - this.bounceVel) * delta);
		//this.preY = MathHelper.lerp(0.5f, this.preY, (this.targetBounce - this.bounceVel) * 1.25f);
		this.bounceVel += this.velocity * percent * 1.1625f;

		//X
		this.velocityX = Mth.lerp(bounceAmount, this.velocityX, (this.targetBounceX - this.bounceVelX) * delta);
		this.bounceVelX += this.velocityX * percent;

		this.rotVelocity = Mth.lerp(bounceAmount, this.rotVelocity, (this.targetRotVel - this.bounceRotVel) * delta);
		this.bounceRotVel += this.rotVelocity * percent;

		this.wfg_bounceRotation = this.bounceRotVel;
		this.positionX = this.bounceVelX;
		this.positionY = this.bounceVel;

		if(this.positionY < -0.5f) this.positionY = -0.5f;
		if(this.positionY > 1.5f) {
			this.positionY = 1.5f;
			this.velocity = 0;
		}
	}

	public float getBreastSize(float partialTicks) {
		return Mth.lerp(partialTicks, preBreastSize, breastSize);
	}

	public float getPrePositionY() {
		return this.prePositionY;
	}

	public float getPositionY() {
		return this.positionY;
	}

	public float getPrePositionX() {
		return this.prePositionX;
	}

	public float getPositionX() {
		return this.positionX;
	}

	public float getBounceRotation() {
		return this.wfg_bounceRotation;
	}
	public float getPreBounceRotation() {
		return this.wfg_preBounceRotation;
	}

	private int clampMovement(float movement) {
		int val = (int) (10 - movement*2f);
		if(val < 1) val = 1;
		return val;
	}
}
