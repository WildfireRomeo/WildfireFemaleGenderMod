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

package com.wildfire.mixins.renderstate;

import com.wildfire.render.RenderStateEntityCapture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
@Implements(@Interface(iface = RenderStateEntityCapture.class, prefix = "wildfire_gender$"))
@Environment(EnvType.CLIENT)
abstract class LivingEntityRenderStateMixin {
	private @Unique @Nullable LivingEntity wildfire_gender$entity = null;

	public @Nullable LivingEntity wildfire_gender$getEntity() {
		return wildfire_gender$entity;
	}

	public void wildfire_gender$setEntity(LivingEntity entity) {
		this.wildfire_gender$entity = entity;
	}
}
