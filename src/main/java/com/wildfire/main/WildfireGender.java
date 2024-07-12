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

import com.mojang.logging.LogUtils;
import com.wildfire.api.IGenderArmor;
import com.wildfire.api.WildfireAPI;
import com.wildfire.main.config.GeneralClientConfig;
import com.wildfire.main.entitydata.BreastDataComponent;
import com.wildfire.main.entitydata.PlayerConfig;
import com.wildfire.main.networking.ClientboundSyncPacket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(WildfireGender.MODID)
public class WildfireGender {

    public static final String MODID = WildfireAPI.MODID;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Map<UUID, PlayerConfig> PLAYER_CACHE = new ConcurrentHashMap<>();
    private static WildfireGender instance;

    //Tracked player to the set of tracking players
    private final Map<UUID, Set<ServerPlayer>> trackedPlayers = new HashMap<>();

    public WildfireGender(IEventBus modEventBus) {
        instance = this;

        modEventBus.addListener(WildfireHelper::registerCapabilities);
        modEventBus.addListener(WildfireHelper::registerPackets);
        NeoForge.EVENT_BUS.addListener(this::onStartTracking);
        NeoForge.EVENT_BUS.addListener(this::onStopTracking);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onEntitySpawn);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onRightClickArmorStand);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static Set<ServerPlayer> getTrackers(Player target) {
        return instance.trackedPlayers.getOrDefault(target.getUUID(), Set.of());
    }

    @Nullable
    public static PlayerConfig getPlayerById(UUID id) {
        return PLAYER_CACHE.get(id);
    }

    public static PlayerConfig getOrAddPlayerById(UUID id) {
        return PLAYER_CACHE.computeIfAbsent(id, PlayerConfig::new);
    }

    private void onStartTracking(PlayerEvent.StartTracking evt) {
        if (evt.getTarget() instanceof Player toSync && evt.getEntity() instanceof ServerPlayer sendTo && sendTo.connection.hasChannel(ClientboundSyncPacket.TYPE)) {
            trackedPlayers.computeIfAbsent(toSync.getUUID(), uuid -> new HashSet<>()).add(sendTo);

            PlayerConfig genderToSync = WildfireGender.getPlayerById(toSync.getUUID());
            if (genderToSync == null) {
                return;
            }
            // Note that we intentionally don't check if we've previously synced a player with this code path;
            // because we use entity tracking to sync, it's entirely possible that one player would leave the
            // tracking distance of another, change their settings, and then re-enter their tracking distance;
            // we wouldn't sync while they're out of tracking distance, and as such, their settings would be out
            // of sync until they relog.
            PacketDistributor.sendToPlayer(sendTo, new ClientboundSyncPacket(genderToSync));
        }
    }

    private void onStopTracking(PlayerEvent.StopTracking evt) {
        if (evt.getTarget() instanceof Player toSync && evt.getEntity() instanceof ServerPlayer sendTo) {
            UUID uuid = toSync.getUUID();
            Set<ServerPlayer> trackers = trackedPlayers.get(uuid);
            if (trackers != null && trackers.remove(sendTo) && trackers.isEmpty()) {
                trackedPlayers.remove(uuid);
            }
        }
    }

    private static EquipmentSlot getEquipmentSlot(ItemStack stack) {
        EquipmentSlot slot = stack.getEquipmentSlot();
        if (slot == null) {
            Equipable equipable = Equipable.get(stack);
            return equipable == null ? EquipmentSlot.MAINHAND : equipable.getEquipmentSlot();
        }
        return slot;
    }

    private void onEntitySpawn(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ItemEntity entity && getEquipmentSlot(entity.getItem()) == EquipmentSlot.CHEST) {
            //Remove our tag if it is present when an item drops (such as from an armor stand being broken)
            ItemStack stack = entity.getItem();
            if (BreastDataComponent.removeFromStack(stack)) {
                entity.setItem(stack);
            }
        }
    }

    private void onRightClickArmorStand(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        //Copy of various checks from ArmorStand#interactAt, so that we can only apply it if a stack is being transferred
        if (!player.level().isClientSide && event.getTarget() instanceof ArmorStand armorStand && !armorStand.isMarker() && !player.isSpectator()) {
            ItemStack stack = player.getItemInHand(event.getHand());
            // Only apply to chestplates
            if (stack.isEmpty()) {
                EquipmentSlot clickedSlot = armorStand.getClickedSlot(event.getLocalPos());
                EquipmentSlot equipmentslot2 = armorStand.isDisabled(clickedSlot) ? getEquipmentSlot(stack) : clickedSlot;
                if (equipmentslot2 == EquipmentSlot.CHEST) {
                    //Copy of logic from ArmorStand#swapItem
                    ItemStack itemstack = armorStand.getItemBySlot(equipmentslot2);
                    if (!itemstack.isEmpty()) {
                        if ((armorStand.disabledSlots & 1 << equipmentslot2.getFilterFlag() + 8) == 0) {
                            //Stack is being removed from the armor stand, remove the corresponding tag key we added if it is present
                            BreastDataComponent.removeFromStack(itemstack);
                        }
                    }
                }
            } else if (getEquipmentSlot(stack) == EquipmentSlot.CHEST && WildfireHelper.getArmorConfig(stack).armorStandsCopySettings() &&
                       !armorStand.isDisabled(EquipmentSlot.CHEST)) {
                //Copy of logic from ArmorStand#swapItem
                ItemStack itemstack = armorStand.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemstack.isEmpty() && (armorStand.disabledSlots & 1 << EquipmentSlot.CHEST.getFilterFlag() + 8) != 0) {
                    return;
                } else if (itemstack.isEmpty() && (armorStand.disabledSlots & 1 << EquipmentSlot.CHEST.getFilterFlag() + 16) != 0) {
                    return;
                } else if (player.getAbilities().instabuild && itemstack.isEmpty()) {
                    //Copy the stack and set it in the armor stand manually, cancelling the event so that it doesn't go through
                    // so that we can apply it but not set nbt on the held stack
                    stack = stack.copyWithCount(1);
                    event.setCanceled(true);
                } else if (!itemstack.isEmpty()) {
                    //Stack is being removed from the armor stand remove the corresponding tag key we added if it is present
                    BreastDataComponent.removeFromStack(itemstack);
                    if (stack.getCount() > 1) {
                        //If the held stack has a size greater than one, we are only removing so can exit. Otherwise we are swapping
                        // so need to add to the held stack
                        return;
                    }
                } else {
                    //Copy the stack and set it in the armor stand manually, cancelling the event so that it doesn't go through
                    // so that we can apply it but not set nbt on the held stack
                    stack = stack.split(1);
                    event.setCanceled(true);
                }

                PlayerConfig playerConfig = WildfireGender.getPlayerById(player.getUUID());
                if (playerConfig == null) {
                    BreastDataComponent.removeFromStack(itemstack);
                } else {
                    IGenderArmor armorConfig = WildfireHelper.getArmorConfig(stack);
                    if (armorConfig.armorStandsCopySettings()) {
                        BreastDataComponent component = BreastDataComponent.fromPlayer(player, playerConfig);
                        if (component != null) {
                            component.write(player.level().registryAccess(), stack);
                        }
                    }
                }
                if (event.isCanceled()) {
                    //We cancelled it, so we need to now actually set it as well
                    armorStand.setItemSlot(EquipmentSlot.CHEST, stack);
                }
            }
        }
    }

  	public static Future<Optional<PlayerConfig>> loadGenderInfo(UUID uuid, boolean markForSync) {
  	  	return Util.ioPool().submit(() -> Optional.ofNullable(PlayerConfig.loadCachedPlayer(uuid, markForSync)));
  	}
}