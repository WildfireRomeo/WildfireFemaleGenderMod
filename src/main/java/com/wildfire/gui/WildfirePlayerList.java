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

package com.wildfire.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.gui.screen.WardrobeBrowserScreen;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.GenderPlayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.Comparator;

public class WildfirePlayerList extends EntryListWidget<WildfirePlayerList.Entry> {
    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new WildfirePlayerList.EntryOrderComparator());

    private static final Identifier TXTR_SYNC = new Identifier(WildfireGender.MODID, "textures/sync.png");
    private static final Identifier TXTR_UNKNOWN = new Identifier(WildfireGender.MODID, "textures/unknown.png");
    private static final Identifier TXTR_CACHED = new Identifier(WildfireGender.MODID, "textures/cached.png");
    private final int listWidth;
    private final WildfirePlayerListScreen parent;

    public WildfirePlayerList(WildfirePlayerListScreen parent, int listWidth, int top, int bottom) {
        super(MinecraftClient.getInstance(), parent.width-4, parent.height, top-6, bottom, 20);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    @Override
    protected int getScrollbarPositionX() {
        return parent.width / 2 + 53;
    }

    @Override
    public int getRowWidth() {
        return this.listWidth;
    }

    public void refreshList() {
        this.clearEntries();
        if(this.client.world == null || this.client.player == null) return;
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;

        for(PlayerListEntry playerList : ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList())) {
            PlayerEntity player = this.client.world.getPlayerByUuid(playerList.getProfile().getId());
            if(player != null) addEntry(new Entry(playerList));
        }
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
            btnOpenGUI = new WildfireButton(0, 0, 112, 20, Text.empty(), button -> {
                GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
                if(aPlr == null) return;

                try {
                    MinecraftClient.getInstance().setScreen(new WardrobeBrowserScreen(parent, nInfo.getProfile().getId()));
                } catch(Exception ignored) {}
            });
            GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
            if(aPlr != null) {
                btnOpenGUI.active = !aPlr.lockSettings;
            }
        }

        @Override
        public void render(DrawContext ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());

            RenderSystem.setShaderTexture(0, nInfo.getSkinTexture());
            PlayerSkinDrawer.draw(ctx, nInfo.getSkinTexture(), x+2, y+2, 16);
            ctx.drawTextWithShadow(font, name, x + 23, y + 2, 0xFFFFFF);

            if(aPlr != null) {
                btnOpenGUI.active = !aPlr.lockSettings;

                ctx.drawTextWithShadow(font, aPlr.getGender().getDisplayName(), x + 23, y + 11, 0xFFFFFF);
                if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.SYNCED) {
                    ctx.drawTexture(TXTR_SYNC, x + 98, y + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                    if (mouseX > x + 98 - 2 && mouseY > y + 11 - 2 && mouseX < y + 98 + 12 + 2 && mouseY < y + 20) {
                        parent.setTooltip(Text.translatable("wildfire_gender.player_list.state.synced"));
                    }

                } else if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.UNKNOWN) {
                    ctx.drawTexture(TXTR_UNKNOWN, x + 98, y + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                }
            } else {
                btnOpenGUI.active = false;
                ctx.drawTextWithShadow(font, Text.translatable("wildfire_gender.label.too_far").formatted(Formatting.RED), x + 23, y + 11, 0xFFFFFF);
            }
            this.btnOpenGUI.setX(x);
            this.btnOpenGUI.setY(y);
            this.btnOpenGUI.render(ctx, mouseX, mouseY, partialTicks);

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
    }

    @Environment(EnvType.CLIENT)
    static class EntryOrderComparator implements Comparator<PlayerListEntry> {
        private EntryOrderComparator() {}

        public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
            Team team = playerListEntry.getScoreboardTeam();
            Team team2 = playerListEntry2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare(team != null ? team.getName() : "", team2 != null ? team2.getName() : "").compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
        }
    }

}
