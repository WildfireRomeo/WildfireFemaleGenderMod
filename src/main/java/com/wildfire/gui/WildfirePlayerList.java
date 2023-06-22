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

import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.gui.screen.WardrobeBrowserScreen;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.GenderPlayer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

public class WildfirePlayerList extends ObjectSelectionList<WildfirePlayerList.Entry>
{
    private static String stripControlCodes(String value) { return net.minecraft.util.StringUtil.stripColor(value); }

    private static final ResourceLocation TXTR_SYNC = new ResourceLocation(WildfireGender.MODID, "textures/sync.png");
    private static final ResourceLocation TXTR_CACHED = new ResourceLocation(WildfireGender.MODID, "textures/cached.png");
    private static final ResourceLocation TXTR_UNKNOWN = new ResourceLocation(WildfireGender.MODID, "textures/unknown.png");

    private final int listWidth;

    private WildfirePlayerListScreen parent;

    public WildfirePlayerList(WildfirePlayerListScreen parent, int listWidth, int top, int bottom)
    {
        super(parent.getMinecraft(), parent.width-4, parent.height, top-6, bottom, 20);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    @Override
    protected int getScrollbarPosition()
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
        PlayerInfo[] playersC = this.minecraft.getConnection().getOnlinePlayers().toArray(new PlayerInfo[0]);

        for (PlayerInfo loadedPlayer : playersC) {
            this.addEntry(new Entry(loadedPlayer));
        }
    }

    @Override
    protected void renderBackground(@Nonnull GuiGraphics graphics) {}

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

    public class Entry extends ObjectSelectionList.Entry<WildfirePlayerList.Entry> {

        private final String name;
        public final PlayerInfo nInfo;
        private final WildfireButton btnOpenGUI;

        private Entry(final PlayerInfo nInfo) {
            this.nInfo = nInfo;
            this.name = nInfo.getProfile().getName();
            btnOpenGUI = new WildfireButton(0, 0, 112, 20, Component.empty(), button -> {
                GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
                if(aPlr == null) return;

                try {
                    Minecraft.getInstance().setScreen(new WardrobeBrowserScreen(parent, nInfo.getProfile().getId()));
                } catch(Exception ignored) {}
            });
            GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
            if(aPlr != null) {
                btnOpenGUI.active = !aPlr.lockSettings;
            }
        }

        public PlayerInfo getNetworkInfo() {
            return nInfo;
        }

        @Override
        public void render(@Nonnull GuiGraphics graphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            Font font = minecraft.font;

            Player playerentity = minecraft.level.getPlayerByUUID(nInfo.getProfile().getId());
            GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
            boolean flag1 = false;
            ResourceLocation skinLocation = nInfo.getSkinLocation();
            int i3 = 8 + (flag1 ? 8 : 0);
            int j3 = 8 * (flag1 ? -1 : 1);
            graphics.blit(skinLocation, left+2, top+2, 16, 16, 8.0F, (float)i3, 8, j3, 64, 64);
            if (playerentity != null && playerentity.isModelPartShown(PlayerModelPart.HAT)) {
                int k3 = 8 + (flag1 ? 8 : 0);
                int l3 = 8 * (flag1 ? -1 : 1);
                graphics.blit(skinLocation, left+1, top+1, 18, 18, 40.0F, (float)k3, 8, l3, 64, 64);
            }

            graphics.drawString(font, name, left + 23, top + 2, 0xFFFFFF, false);
            if(aPlr != null) {
                btnOpenGUI.active = !aPlr.lockSettings;

                graphics.drawString(font, aPlr.getGender().getDisplayName(), left + 23, top + 11, 0xFFFFFF, false);
                if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.SYNCED) {
                    graphics.blit(TXTR_SYNC, left + 98, top + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                    if (mouseX > left + 98 - 2 && mouseY > top + 11 - 2 && mouseX < left + 98 + 12 + 2 && mouseY < top + 20) {
                        parent.setTooltip(Component.translatable("wildfire_gender.player_list.state.synced"));
                    }

                } else if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.UNKNOWN) {
                    graphics.blit(TXTR_UNKNOWN, left + 98, top + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                }
            } else {
                btnOpenGUI.active = false;
                graphics.drawString(font, Component.translatable("wildfire_gender.label.too_far").withStyle(ChatFormatting.RED), left + 23, top + 11, 0xFFFFFF, false);
            }
            this.btnOpenGUI.setX(left);
            this.btnOpenGUI.setY(top);
            this.btnOpenGUI.render(graphics, mouseX, mouseY, partialTicks);

            if(this.btnOpenGUI.isHoveredOrFocused()) {
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

        @Nonnull
        @Override
        public Component getNarration() {
            return Component.empty();
        }
    }
}
