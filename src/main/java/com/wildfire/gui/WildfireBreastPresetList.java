package com.wildfire.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.screen.WildfireBreastCustomizationScreen;
import com.wildfire.main.WildfireGender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WildfireBreastPresetList extends EntryListWidget<WildfireBreastPresetList.Entry> {

    private class BreastPresetListEntry {

        public Identifier ident;

        public BreastPresetListEntry(String location) {
            ident = new Identifier(WildfireGender.MODID, "textures/presets/" + location);
        }
    }
    private BreastPresetListEntry[] BREAST_PRESETS = new BreastPresetListEntry[] {
            new BreastPresetListEntry("preset1.png"),
            new BreastPresetListEntry("preset2.png"),
            new BreastPresetListEntry("preset3.png"),
            new BreastPresetListEntry("preset4.png"),
    };
    private static final Identifier TXTR_SYNC = new Identifier(WildfireGender.MODID, "textures/sync.png");
    private static final Identifier TXTR_UNKNOWN = new Identifier(WildfireGender.MODID, "textures/unknown.png");
    private static final Identifier TXTR_CACHED = new Identifier(WildfireGender.MODID, "textures/cached.png");
    private final int listWidth;
    private final WildfireBreastCustomizationScreen parent;

    public WildfireBreastPresetList(WildfireBreastCustomizationScreen parent, int listWidth, int top, int bottom) {
        super(MinecraftClient.getInstance(), parent.width-4, parent.height, top-6, bottom, 54);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    @Override
    protected int getScrollbarPositionX() {
        return parent.width / 2 + 181;
    }

    @Override
    public int getRowWidth() {
        return this.listWidth;
    }

    public void refreshList() {
        this.clearEntries();
        if(this.client.world == null || this.client.player == null) return;
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;

        for(int i = 0; i < BREAST_PRESETS.length; i++) {
            addEntry(new Entry(BREAST_PRESETS[i]));
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}

    @Environment(EnvType.CLIENT)
    public class Entry extends EntryListWidget.Entry<WildfireBreastPresetList.Entry> {
        private final Identifier thumbnail;
        public final BreastPresetListEntry nInfo;
        private final WildfireButton btnOpenGUI;

        private Entry(final BreastPresetListEntry nInfo) {
            this.nInfo = nInfo;
            this.thumbnail = nInfo.ident;
            btnOpenGUI = new WildfireButton(0, 0, 54, 54, Text.empty(), button -> {
                /*GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
                if(aPlr == null) return;

                try {
                    MinecraftClient.getInstance().setScreen(new WardrobeBrowserScreen(parent, nInfo.getProfile().getId()));
                } catch(Exception ignored) {}*/
            });
        }

        @Override
        public void render(DrawContext ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;

            RenderSystem.setShaderTexture(0, thumbnail);
            ctx.drawTexture(thumbnail, x + 154, y + 2, 0, 0, 50, 50, 50,50);

            this.btnOpenGUI.setX(x + 152);
            this.btnOpenGUI.setY(y);
            this.btnOpenGUI.render(ctx, mouseX, mouseY, partialTicks);

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.btnOpenGUI.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
