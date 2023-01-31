/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022 WildfireRomeo

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

package com.wildfire.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.screen.WardrobeBrowserScreen;
import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class WildfirePlayerList extends EntryListWidget<WildfirePlayerList.Entry> {
    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new WildfirePlayerList.EntryOrderComparator());

    private static final Identifier TXTR_SYNC = new Identifier(WildfireGender.MODID, "textures/sync.png");
    private static final Identifier TXTR_UNKNOWN = new Identifier(WildfireGender.MODID, "textures/unknown.png");
    private static final Identifier TXTR_CACHED = new Identifier(WildfireGender.MODID, "textures/cached.png");

    private final int listWidth;
    private boolean loadingPlayers = true;

    private static final UUID CREATOR_UUID = UUID.fromString("33c937ae-6bfc-423e-a38e-3a613e7c1256");
    public boolean withCreator = false;
    private final WildfirePlayerListScreen parent;

    public WildfirePlayerList(WildfirePlayerListScreen parent, int listWidth, int top, int bottom) {
        super(MinecraftClient.getInstance(), parent.width-4, parent.height, top-6, bottom, 20);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    @Override
    protected int getScrollbarPositionX()
    {
        return parent.width / 2 + 53;
    }

    @Override
    public int getRowWidth()
    {
        return this.listWidth;
    }

    public void refreshList() {
        this.clearEntries();
        this.withCreator = false;
        if(this.client.player == null || this.client.world == null) return;

        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
        for(PlayerListEntry playerListEntry : list) {
            PlayerEntity player = this.client.world.getPlayerByUuid(playerListEntry.getProfile().getId());
            if(player != null) {
                if(player.getUuid().equals(CREATOR_UUID)) this.withCreator = true;
                addEntry(new WildfirePlayerList.Entry(playerListEntry));
            }
        }
    }

    @Override
    protected void renderBackground(MatrixStack mStack) {}

    public boolean isLoadingPlayers() {
        boolean loadingPlayers = false;
        for (Entry child : this.children()) {
            GenderPlayer aPlr = WildfireGender.getPlayerById(child.nInfo.getProfile().getId());
            if (aPlr == null) {
                loadingPlayers = true;
            }
        }
        return loadingPlayers;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}

    @Environment(EnvType.CLIENT)
    public class Entry extends EntryListWidget.Entry<WildfirePlayerList.Entry> {
        private final String name;
        public final PlayerListEntry nInfo;
        private final WildfireButton btnOpenGUI;

        private Entry(final PlayerListEntry nInfo) {
            this.nInfo = nInfo;
            this.name = nInfo.getProfile().getName();

            btnOpenGUI = new WildfireButton(0, 0, 112, 20,
                    Text.empty(),
                    button -> {
                        GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
                        if (aPlr == null) return;

                        try {
                            MinecraftClient.getInstance().setScreen(new WardrobeBrowserScreen(parent, nInfo.getProfile().getId()));
                        } catch (Exception ignored) {}
                    });

            GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
            if(aPlr != null) {
                btnOpenGUI.active = !aPlr.lockSettings;
            }
        }

        public PlayerListEntry getNetworkInfo() {
            return nInfo;
        }

        @Override
        public void render(MatrixStack m, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;

            PlayerEntity playerentity = MinecraftClient.getInstance().world.getPlayerByUuid(nInfo.getProfile().getId());
            GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
            boolean flag1 = false;
            RenderSystem.setShaderTexture(0, nInfo.getSkinTexture());
            int i3 = 8 + (flag1 ? 8 : 0);
            int j3 = 8 * (flag1 ? -1 : 1);
            Screen.drawTexture(m, left+2, top+2, 16, 16, 8.0F, (float)i3, 8, j3, 64, 64);
            if (playerentity != null && playerentity.isPartVisible(PlayerModelPart.HAT)) {
                int k3 = 8 + (flag1 ? 8 : 0);
                int l3 = 8 * (flag1 ? -1 : 1);
                Screen.drawTexture(m, left+1, top+1, 18, 18, 40.0F, (float)k3, 8, l3, 64, 64);
            }

            font.draw(m, name, left + 23, top + 2, 0xFFFFFF);
            if(aPlr != null) {
                btnOpenGUI.active = !aPlr.lockSettings;

                font.draw(m, aPlr.getGender().getDisplayName(), left + 23, top + 11, 0xFFFFFF);
                if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.SYNCED) {
                    RenderSystem.setShaderTexture(0, TXTR_SYNC);
                    Screen.drawTexture(m, left + 98, top + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                    if (mouseX > left + 98 - 2 && mouseY > top + 11 - 2 && mouseX < left + 98 + 12 + 2 && mouseY < top + 20) {
                        parent.setTooltip(Text.translatable("wildfire_gender.player_list.state.synced"));
                    }

                } else if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.UNKNOWN) {
                    RenderSystem.setShaderTexture(0, TXTR_UNKNOWN);
                    Screen.drawTexture(m, left + 98, top + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                }
            } else {
                btnOpenGUI.active = false;
                font.draw(m, Text.translatable("wildfire_gender.label.too_far").formatted(Formatting.RED), left + 23, top + 11, 0xFFFFFF);
            }
            this.btnOpenGUI.setX(left);
            this.btnOpenGUI.setY(top);
            this.btnOpenGUI.render(m, mouseX, mouseY, partialTicks);

            if(this.btnOpenGUI.isHovered()) {
                WildfirePlayerListScreen.HOVER_PLAYER = aPlr;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.btnOpenGUI.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.btnOpenGUI.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Environment(EnvType.CLIENT)
    static class EntryOrderComparator implements Comparator<PlayerListEntry> {
        private EntryOrderComparator() {
        }

        public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
            Team team = playerListEntry.getScoreboardTeam();
            Team team2 = playerListEntry2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare(team != null ? team.getName() : "", team2 != null ? team2.getName() : "").compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
        }
    }

}
