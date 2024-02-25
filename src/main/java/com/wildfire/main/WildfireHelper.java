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
import com.wildfire.main.networking.PacketSendGenderInfo;
import com.wildfire.main.networking.PacketSync;
import com.wildfire.render.armor.EmptyGenderArmor;
import com.wildfire.render.armor.SimpleGenderArmor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class WildfireHelper {

    public static float randFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, (double) max + 1);
    }

    public static IGenderArmor getArmorConfig(ItemStack stack) {
        if (stack.isEmpty()) {
            return EmptyGenderArmor.INSTANCE;
        }
        IGenderArmor capability = stack.getCapability(WildfireAPI.GENDER_ARMOR_CAPABILITY);
        if (capability != null) {
            return capability;
        }
        //Note: Vanilla armor will be handled above, as we attach the capability to the corresponding items
        if (stack.getItem() instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.CHESTPLATE) {
            //If it is an armor item, use our fallback value
            return SimpleGenderArmor.FALLBACK;
        }
        //If it is not an armor item default as if "nothing is being worn that covers the breast area"
        // this might not be fully accurate and may need some tweaks but in general is likely relatively
        // close to the truth of if it should render or not. This covers cases such as the elytra and
        // other wearables
        return EmptyGenderArmor.INSTANCE;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        //Expose our defaults for vanilla chest pieces so that if another mod wants to query the values they can easily do so
        event.registerItem(WildfireAPI.GENDER_ARMOR_CAPABILITY, (stack, context) -> SimpleGenderArmor.LEATHER, Items.LEATHER_CHESTPLATE);
        event.registerItem(WildfireAPI.GENDER_ARMOR_CAPABILITY, (stack, context) -> SimpleGenderArmor.CHAIN_MAIL, Items.CHAINMAIL_CHESTPLATE);
        event.registerItem(WildfireAPI.GENDER_ARMOR_CAPABILITY, (stack, context) -> SimpleGenderArmor.GOLD, Items.GOLDEN_CHESTPLATE);
        event.registerItem(WildfireAPI.GENDER_ARMOR_CAPABILITY, (stack, context) -> SimpleGenderArmor.IRON, Items.IRON_CHESTPLATE);
        event.registerItem(WildfireAPI.GENDER_ARMOR_CAPABILITY, (stack, context) -> SimpleGenderArmor.DIAMOND, Items.DIAMOND_CHESTPLATE);
        event.registerItem(WildfireAPI.GENDER_ARMOR_CAPABILITY, (stack, context) -> SimpleGenderArmor.NETHERITE, Items.NETHERITE_CHESTPLATE);
    }

    public static void registerPackets(RegisterPayloadHandlerEvent event) {
        //TODO: We can accept packets from fabric but for some reason it doesn't seem like NeoForge is properly letting fabric know we exist?
        IPayloadRegistrar registrar = event.registrar(WildfireGender.MODID).optional();
        //Client to server
        registrar.play(PacketSendGenderInfo.ID, PacketSendGenderInfo::new, builder -> builder.server(PacketSendGenderInfo::handleMainThread));
        //Server to client
        registrar.play(PacketSync.ID, PacketSync::new, builder -> builder.client(PacketSync::handleMainThread));
    }

    public static <ENTITY extends LivingEntity> void withEntityAngles(ENTITY entity, float yBodyRot, float yRot, float xRot, Consumer<ENTITY> runnable) {
        float oldYBodyRot = entity.yBodyRot;
        float oldYRot = entity.getYRot();
        float oldXRot = entity.getXRot();
        float oldYHeadRot0 = entity.yHeadRotO;
        float oldYHeadRot = entity.yHeadRot;
        entity.yBodyRot = yBodyRot;
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        entity.yHeadRot = yRot;
        entity.yHeadRotO = yRot;

        runnable.accept(entity);

        entity.yBodyRot = oldYBodyRot;
        entity.setYRot(oldYRot);
        entity.setXRot(oldXRot);
        entity.yHeadRotO = oldYHeadRot0;
        entity.yHeadRot = oldYHeadRot;
    }
}
