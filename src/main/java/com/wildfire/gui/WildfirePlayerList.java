/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wildfire.gui.screen.WildfirePlayerListScreen;
import com.wildfire.gui.screen.WardrobeBrowserScreen;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.GenderPlayer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

public class WildfirePlayerList extends ObjectSelectionList<WildfirePlayerList.Entry>
{
    private static String stripControlCodes(String value) { return net.minecraft.util.StringUtil.stripColor(value); }

    private static final ResourceLocation TXTR_SYNC = new ResourceLocation("wildfire_gender", "textures/sync.png");
    private static final ResourceLocation TXTR_CACHED = new ResourceLocation("wildfire_gender", "textures/cached.png");
    private static final ResourceLocation TXTR_UNKNOWN = new ResourceLocation("wildfire_gender", "textures/unknown.png");

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
            this.addEntry(new Entry(loadedPlayer, false));
        }
    }

    @Override
    protected void renderBackground(@Nonnull PoseStack mStack) {}

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
        private final boolean gender;
        public final PlayerInfo nInfo;
        private final WildfireButton btnOpenGUI;

        private Entry(final PlayerInfo nInfo, final boolean gender) {
            this.nInfo = nInfo;
            this.name = nInfo.getProfile().getName();
            this.gender = gender;
            btnOpenGUI = new WildfireButton(0, 0, 112, 20, TextComponent.EMPTY, button -> {
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
        public void render(@Nonnull PoseStack m, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            Font font = minecraft.font;

            Player playerentity = minecraft.level.getPlayerByUUID(nInfo.getProfile().getId());
            GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
            boolean flag1 = false;
            RenderSystem.setShaderTexture(0, nInfo.getSkinLocation());
            int i3 = 8 + (flag1 ? 8 : 0);
            int j3 = 8 * (flag1 ? -1 : 1);
            GuiComponent.blit(m, left+2, top+2, 16, 16, 8.0F, (float)i3, 8, j3, 64, 64);
            if (playerentity != null && playerentity.isModelPartShown(PlayerModelPart.HAT)) {
                int k3 = 8 + (flag1 ? 8 : 0);
                int l3 = 8 * (flag1 ? -1 : 1);
                GuiComponent.blit(m, left+1, top+1, 18, 18, 40.0F, (float)k3, 8, l3, 64, 64);
            }

            font.draw(m, name, left + 23, top + 2, 0xFFFFFF);
            if(aPlr != null) {
                btnOpenGUI.active = !aPlr.lockSettings;

                switch (aPlr.gender) {
                    //female
                    case 0 -> font.draw(m, new TranslatableComponent("wildfire_gender.label.female").withStyle(ChatFormatting.LIGHT_PURPLE), left + 23, top + 11, 0xFFFFFF);
                    //male
                    case 1 -> font.draw(m, new TranslatableComponent("wildfire_gender.label.male").withStyle(ChatFormatting.BLUE), left + 23, top + 11, 0xFFFFFF);
                    //other
                    case 2 -> font.draw(m, new TranslatableComponent("wildfire_gender.label.other").withStyle(ChatFormatting.GREEN), left + 23, top + 11, 0xFFFFFF);
                }
                if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.SYNCED) {
                    RenderSystem.setShaderTexture(0, TXTR_SYNC);
                    GuiComponent.blit(m, left + 98, top + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                    if (mouseX > left + 98 - 2 && mouseY > top + 11 - 2 && mouseX < left + 98 + 12 + 2 && mouseY < top + 20) {
                        parent.setTooltip(new TranslatableComponent("wildfire_gender.player_list.state.synced"));
                    }

                } else if (aPlr.getSyncStatus() == GenderPlayer.SyncStatus.UNKNOWN) {
                    RenderSystem.setShaderTexture(0, TXTR_UNKNOWN);
                    GuiComponent.blit(m, left + 98, top + 11, 12, 8, 0, 0, 12, 8, 12, 8);
                }
            } else {
                btnOpenGUI.active = false;
                font.draw(m, new TranslatableComponent("wildfire_gender.label.too_far").withStyle(ChatFormatting.RED), left + 23, top + 11, 0xFFFFFF);
            }
            this.btnOpenGUI.x = left;
            this.btnOpenGUI.y = top;
            this.btnOpenGUI.render(m, mouseX, mouseY, partialTicks);

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
            return TextComponent.EMPTY;
        }
    }
}
