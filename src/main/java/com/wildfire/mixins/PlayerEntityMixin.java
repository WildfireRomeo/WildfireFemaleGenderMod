package com.wildfire.mixins;

import com.mojang.authlib.GameProfile;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.http.NameValuePair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


@Environment(EnvType.CLIENT)
@Mixin(value = PlayerEntity.class, priority = 900)
public abstract class PlayerEntityMixin extends LivingEntity {
	
	public float wfg_femaleBreast;
	public float wfg_preBounce;
	
	float bounceVel = 0;
	float targetBounce = 0;
	float preY = 0;
	
	boolean justSneaking = false;
	boolean alreadySleeping = false;
	
	public PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(EntityType.PLAYER, world);
	}

	@Inject(at = @At("TAIL"), method = "tick")
	public void onTick(CallbackInfo info) {
		tickWildfire();
	}

	public void tickWildfire() {
		if(!this.world.isClient()) return;
		GenderPlayer aPlr = WildfireGender.getPlayerByName(this.getUuid().toString());
		if(aPlr == null) return;
		PlayerEntity plr = (PlayerEntity) (Object) this;
		aPlr.getLeftBreastPhysics().update(plr);
		aPlr.getRightBreastPhysics().update(plr);


	}

}