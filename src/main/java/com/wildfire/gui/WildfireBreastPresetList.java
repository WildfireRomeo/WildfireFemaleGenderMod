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

import com.wildfire.gui.WildfireBreastPresetList.BreastPresetListEntry;
import com.wildfire.gui.screen.WildfireBreastCustomizationScreen;
import com.wildfire.main.GenderPlayer;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.BreastPresetConfiguration;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class WildfireBreastPresetList extends AbstractSelectionList<BreastPresetListEntry> {

    public boolean active = true;
    public boolean visible = true;

    private static final ResourceLocation TXTR_SYNC = WildfireGender.rl("textures/sync.png");
    private static final ResourceLocation TXTR_UNKNOWN = WildfireGender.rl("textures/unknown.png");
    private static final ResourceLocation TXTR_CACHED = WildfireGender.rl("textures/cached.png");
    private final WildfireBreastCustomizationScreen parent;
    private final int listWidth;
    private boolean hasPresets;

    public WildfireBreastPresetList(WildfireBreastCustomizationScreen parent, int listWidth, int top, int height) {
        super(Minecraft.getInstance(), 156, height, top, 32);
        setRenderHeader(false, 0);
        setRenderBackground(false);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    public boolean hasPresets() {
        return hasPresets;
    }

    @Override
    protected void renderSelection(@Nonnull GuiGraphics context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
    }

    @Override
    protected int getRowTop(int index) {
        return super.getRowTop(index) - 4;
    }

    @Override
    protected int getScrollbarPosition() {
        return parent.width / 2 + 181;
    }

    @Override
    public int getRowWidth() {
        return this.listWidth;
    }

    @Override
    protected void clearEntries() {
        super.clearEntries();
        hasPresets = false;
    }

    public void refreshList() {
        this.clearEntries();
        if (this.minecraft.level == null || this.minecraft.player == null) return;
        BreastPresetConfiguration[] CONFIGS = BreastPresetConfiguration.getBreastPresetConfigurationFiles();
        for (BreastPresetConfiguration presetCfg : CONFIGS) {
            addEntry(new BreastPresetListEntry(presetCfg));
        }
        hasPresets = CONFIGS.length > 0;
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput output) {
    }

    public class BreastPresetListEntry extends AbstractSelectionList.Entry<BreastPresetListEntry> {
        private final ResourceLocation thumbnail;
        private final String name;
        private final WildfireButton btnOpenGUI;

        private BreastPresetListEntry(BreastPresetConfiguration data) {
            this(MissingTextureAtlasSprite.getLocation(), data.get(BreastPresetConfiguration.PRESET_NAME), data);
            WildfireGender.logger.debug("Preset Name: {}", name);
        }

        private BreastPresetListEntry(ResourceLocation thumbnail, String name, BreastPresetConfiguration data) {
            this.name = name;
            this.thumbnail = thumbnail;
            btnOpenGUI = new WildfireButton(0, 0, getRowWidth() - 6, itemHeight, Component.empty(), button -> {
                GenderPlayer player = parent.getPlayer();
                player.updateBustSize(data);
                player.getBreasts().copyFrom(data);
                //TODO: I think we may need to save the gender info after loading and then update the settings to ensure it renders properly?
                // the rendering properly might function fine as is as the tab is still the preset tab so ability to have breasts changing
                // shouldn't necessarily matter?
                //GenderPlayer.saveGenderInfo(player);
                //parent.updatePresetTab();
            });
        }

        @Override
        public void render(@Nonnull GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            if (!visible) return;

            Font font = Minecraft.getInstance().font;
            //graphics.fill(x, y, x + entryWidth, y + entryHeight, 0x55005555);

            graphics.blit(thumbnail, x + 2, y + 2, 0, 0, 28, 28, 28,28);

            graphics.drawString(font, Component.literal(name), x + 34, y + 4, 0xFFFFFFFF, false);
            //graphics.drawString(font, Component.literal("07/25/2023 1:19 AM").formatted(Formatting.ITALIC), x + 34, y + 20, 0xFF888888, false);
            this.btnOpenGUI.setX(x);
            this.btnOpenGUI.setY(y);
            this.btnOpenGUI.render(graphics, mouseX, mouseY, partialTicks);

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (active && visible) {
                if (this.btnOpenGUI.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }
    }
}