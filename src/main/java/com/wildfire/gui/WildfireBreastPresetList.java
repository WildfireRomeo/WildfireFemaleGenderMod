package com.wildfire.gui;

import com.wildfire.gui.screen.WildfireBreastCustomizationScreen;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.config.BreastPresetConfiguration;
import com.wildfire.main.entitydata.PlayerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class WildfireBreastPresetList extends EntryListWidget<WildfireBreastPresetList.Entry> {

    public boolean active = true;
    public boolean visible = true;

    public class BreastPresetListEntry {

        public Identifier ident;
        public String name;
        private BreastPresetConfiguration data;

        public BreastPresetListEntry(String name, BreastPresetConfiguration data) {
            this.name = name;
            this.data = data;
            this.ident = new Identifier(WildfireGender.MODID, "textures/presets/iknowthisisnull.png");
        }

    }

    private BreastPresetListEntry[] BREAST_PRESETS = new BreastPresetListEntry[] {

    };
    private static final Identifier TXTR_SYNC = new Identifier(WildfireGender.MODID, "textures/sync.png");
    private static final Identifier TXTR_UNKNOWN = new Identifier(WildfireGender.MODID, "textures/unknown.png");
    private static final Identifier TXTR_CACHED = new Identifier(WildfireGender.MODID, "textures/cached.png");
    private final int listWidth;
    private final WildfireBreastCustomizationScreen parent;

    public WildfireBreastPresetList(WildfireBreastCustomizationScreen parent, int listWidth, int top) {
        super(MinecraftClient.getInstance(), 156, parent.height, top, 32);
        this.setRenderHeader(false, 0);
        this.setRenderBackground(false);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    public BreastPresetListEntry[] getPresetList() {
        return BREAST_PRESETS;
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {}

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getRowLeft();
        int j = this.getRowWidth();
        int k = this.itemHeight;
        int l = this.getEntryCount();

        for(int m = 0; m < l; ++m) {
            int n = this.getRowTop(m);
            int o = this.getRowBottom(m);
            if (o >= this.getY() && n <= this.getBottom()) {
                this.renderEntry(context, mouseX, mouseY, delta, m, i, n, j, k);
            }
        }
    }

    @Override
    protected int getRowTop(int index) {
        return this.getY() - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
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

        //BREAST_PRESETS
        BreastPresetConfiguration[] CONFIGS = BreastPresetConfiguration.getBreastPresetConfigurationFiles();
        ArrayList<BreastPresetListEntry> tmpPresets = new ArrayList<>();
        for(BreastPresetConfiguration presetCfg : CONFIGS) {
            System.out.println("Preset Name: " + presetCfg.get(BreastPresetConfiguration.PRESET_NAME));
            tmpPresets.add(new BreastPresetListEntry(presetCfg.get(BreastPresetConfiguration.PRESET_NAME), presetCfg));
        }
        BREAST_PRESETS = tmpPresets.toArray(new BreastPresetListEntry[tmpPresets.size()]);

        if(this.client.world == null || this.client.player == null) return;

        for(BreastPresetListEntry breastPreset : BREAST_PRESETS) {
            addEntry(new Entry(breastPreset));
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Environment(EnvType.CLIENT)
    public class Entry extends EntryListWidget.Entry<WildfireBreastPresetList.Entry> {
        private final Identifier thumbnail;
        public final BreastPresetListEntry nInfo;
        private final WildfireButton btnOpenGUI;

        private Entry(final BreastPresetListEntry nInfo) {
            this.nInfo = nInfo;
            this.thumbnail = nInfo.ident;
            btnOpenGUI = WildfireButton.builder()
                    .position(0, 0)
                    .size(getRowWidth() - 6, itemHeight)
                    .text(Text.empty())
                    .onClick(button -> apply(parent.getPlayer()))
                    .build();
        }

        private void apply(PlayerConfig config) {
            BreastPresetConfiguration data = nInfo.data;
            config.updateBustSize(data.get(BreastPresetConfiguration.BUST_SIZE));
            config.getBreasts().updateXOffset(data.get(BreastPresetConfiguration.BREASTS_OFFSET_X));
            config.getBreasts().updateYOffset(data.get(BreastPresetConfiguration.BREASTS_OFFSET_Y));
            config.getBreasts().updateZOffset(data.get(BreastPresetConfiguration.BREASTS_OFFSET_Z));
            config.getBreasts().updateCleavage(data.get(BreastPresetConfiguration.BREASTS_CLEAVAGE));
            config.getBreasts().updateUniboob(data.get(BreastPresetConfiguration.BREASTS_UNIBOOB));
            PlayerConfig.saveGenderInfo(config);
        }

        @Override
        public void render(DrawContext ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
            if(!visible) return;

            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            //ctx.fill(x, y, x + entryWidth, y + entryHeight, 0x55005555);

            ctx.drawTexture(thumbnail, x + 2, y + 2, 0, 0, 28, 28, 28,28);

            ctx.drawText(font, Text.of(nInfo.name), x + 34, y + 4, 0xFFFFFFFF, false);
            //ctx.drawText(font, Text.translatable("07/25/2023 1:19 AM").formatted(Formatting.ITALIC), x + 34, y + 20, 0xFF888888, false);
            this.btnOpenGUI.setX(x);
            this.btnOpenGUI.setY(y);
            this.btnOpenGUI.render(ctx, mouseX, mouseY, partialTicks);

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(active && visible) {
                if (this.btnOpenGUI.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }
    }
}
