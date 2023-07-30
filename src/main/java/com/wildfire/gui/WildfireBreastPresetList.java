package com.wildfire.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wildfire.gui.screen.WildfireBreastCustomizationScreen;
import com.wildfire.main.WildfireGender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.time.format.TextStyle;
import java.util.Map;

public class WildfireBreastPresetList extends EntryListWidget<WildfireBreastPresetList.Entry> {

    public boolean active = true;
    public boolean visible = true;

    private class BreastPresetListEntry {

        public Identifier ident;
        public String name;
        private JsonObject data;

        public BreastPresetListEntry(String name, String location) {
            this.name = name;
            this.ident = new Identifier(WildfireGender.MODID, "textures/presets/" + location);
            load();
        }

        private void load() {
            Path saveDir = FabricLoader.getInstance().getConfigDir();
            System.out.println("SAVE DIR: " + saveDir.toString());

            data = new JsonObject();
            File CFG_FILE = saveDir.resolve("WildfireGender/presets").resolve(this.name + ".json").toFile();

            try (FileReader configurationFile = new FileReader(CFG_FILE)) {
                JsonObject obj = new Gson().fromJson(configurationFile, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String key = entry.getKey();
                    data.add(key, entry.getValue());
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BreastPresetListEntry[] BREAST_PRESETS = new BreastPresetListEntry[] {
            new BreastPresetListEntry("Normal", "preset1.png"),
            new BreastPresetListEntry("Curved", "preset2.png"),
            new BreastPresetListEntry("Small", "preset3.png"),
            new BreastPresetListEntry("Large", "preset4.png"),
    };
    private static final Identifier TXTR_SYNC = new Identifier(WildfireGender.MODID, "textures/sync.png");
    private static final Identifier TXTR_UNKNOWN = new Identifier(WildfireGender.MODID, "textures/unknown.png");
    private static final Identifier TXTR_CACHED = new Identifier(WildfireGender.MODID, "textures/cached.png");
    private final int listWidth;
    private final WildfireBreastCustomizationScreen parent;

    public WildfireBreastPresetList(WildfireBreastCustomizationScreen parent, int listWidth, int top, int bottom) {
        super(MinecraftClient.getInstance(), 156, parent.height, top, bottom, 32);
        this.setRenderHeader(false, 0);
        this.setRenderSelection(false);
        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getRowLeft();
        int j = this.getRowWidth();
        int k = this.itemHeight;
        int l = this.getEntryCount();

        for(int m = 0; m < l; ++m) {
            int n = this.getRowTop(m);
            int o = this.getRowBottom(m);
            if (o >= this.top && n <= this.bottom) {
                this.renderEntry(context, mouseX, mouseY, delta, m, i, n, j, k);
            }
        }

    }

    @Override
    protected int getRowTop(int index) {
        return this.top - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
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
            btnOpenGUI = new WildfireButton(0, 0, getRowWidth() - 6, itemHeight, Text.empty(), button -> {
                /*GenderPlayer aPlr = WildfireGender.getPlayerById(nInfo.getProfile().getId());
                if(aPlr == null) return;

                try {
                    MinecraftClient.getInstance().setScreen(new WardrobeBrowserScreen(parent, nInfo.getProfile().getId()));
                } catch(Exception ignored) {}*/
            });
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


    public int getLeft() {
        return left;
    }
    public int getRight() {
        return right;
    }
    public int getTop() {
        return top;
    }
    public int getBottom() {
        return bottom;
    }
}
