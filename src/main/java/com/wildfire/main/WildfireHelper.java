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

package com.wildfire.main;

import com.wildfire.api.IGenderArmor;
import com.wildfire.api.WildfireAPI;
import com.wildfire.render.armor.SimpleGenderArmor;
import com.wildfire.render.armor.EmptyGenderArmor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModels;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class WildfireHelper {
    // TODO migrate this from being hardcoded to being provided by resource packs instead?
    private static final Map<Identifier, IGenderArmor> VANILLA_ARMORS = Map.of(
            EquipmentModels.LEATHER, SimpleGenderArmor.LEATHER,
            EquipmentModels.CHAINMAIL, SimpleGenderArmor.CHAIN_MAIL,
            EquipmentModels.IRON, SimpleGenderArmor.IRON,
            EquipmentModels.GOLD, SimpleGenderArmor.GOLD,
            EquipmentModels.DIAMOND, SimpleGenderArmor.DIAMOND,
            EquipmentModels.NETHERITE, SimpleGenderArmor.NETHERITE
    );

    private WildfireHelper() {
        throw new UnsupportedOperationException();
    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    public static float randFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, (double) max + 1);
    }

    public static IGenderArmor getArmorConfig(ItemStack stack) {
        if (stack.isEmpty()) {
            return EmptyGenderArmor.INSTANCE;
        }

        if (WildfireAPI.getGenderArmors().get(stack.getItem()) != null) {
            return WildfireAPI.getGenderArmors().get(stack.getItem());
        }

        //TODO: Fabric Alternative to Capabilities? Maybe someone can help with this?
        var equippable = stack.get(DataComponentTypes.EQUIPPABLE);
        if(equippable != null && equippable.slot() == EquipmentSlot.CHEST) {
            var model = equippable.model();
	        return model.map(VANILLA_ARMORS::get).orElse(SimpleGenderArmor.FALLBACK);
        }

        return EmptyGenderArmor.INSTANCE;
    }
}
