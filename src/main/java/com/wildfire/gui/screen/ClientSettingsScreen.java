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

package com.wildfire.gui.screen;

import com.wildfire.gui.WildfireButton;
import com.wildfire.main.config.ClientConfiguration;
import com.wildfire.main.config.ConfigKey;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Function;

public class ClientSettingsScreen extends DynamicallySizedScreen {

	private static final Text LOCAL_ONLY = Text.translatable("wildfire_gender.tooltip.local_only");
	private static final Text ENABLED = Text.translatable("wildfire_gender.label.enabled").formatted(Formatting.GREEN);
	private static final Text DISABLED = Text.translatable("wildfire_gender.label.disabled").formatted(Formatting.RED);

	protected ClientSettingsScreen(Screen parent) {
		super(Text.translatable("wildfire_gender.client_options"), parent, null);
	}

	@Override
	protected void init() {
		// NOTE: buttons/sliders do not need to have a set X/Y position, as super.init() will automatically reposition them

		this.addDrawableChild(WildfireButton.builder()
				.textSupplier(() -> Text.translatable("wildfire_gender.client_options.override_armor_physics",
						ClientConfiguration.INSTANCE.get(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE) ? ENABLED : DISABLED))
				.tooltip(Tooltip.of(Text.translatable("wildfire_gender.tooltip.client_options.override_armor_physics.line1")
						.append("\n\n")
						.append(Text.translatable("wildfire_gender.tooltip.client_options.override_armor_physics.line2"))
						.append("\n\n")
						.append(LOCAL_ONLY)))
				.size(WIDTH, HEIGHT)
				.onClick(button -> set(ClientConfiguration.ARMOR_PHYSICS_OVERRIDE, current -> !current))
				.build());

		this.addDrawableChild(WildfireButton.builder()
				.textSupplier(() -> Text.translatable("wildfire_gender.client_options.breast_rendering",
						ClientConfiguration.INSTANCE.get(ClientConfiguration.ENABLE_BREAST_RENDERING) ? ENABLED : DISABLED))
				.tooltip(Tooltip.of(Text.translatable("wildfire_gender.tooltip.client_options.breast_rendering")
						.append("\n\n")
						.append(LOCAL_ONLY)))
				.size(WIDTH, HEIGHT)
				.onClick(button -> set(ClientConfiguration.ENABLE_BREAST_RENDERING, current -> !current))
				.build());

		this.addDrawableChild(WildfireButton.builder()
				.textSupplier(() -> Text.translatable("wildfire_gender.client_options.hurt_sounds",
						ClientConfiguration.INSTANCE.get(ClientConfiguration.ENABLE_GENDER_HURT_SOUNDS) ? ENABLED : DISABLED))
				.tooltip(Tooltip.of(Text.translatable("wildfire_gender.tooltip.client_options.hurt_sounds")
						.append("\n\n")
						.append(LOCAL_ONLY)))
				.size(WIDTH, HEIGHT)
				.onClick(button -> set(ClientConfiguration.ENABLE_GENDER_HURT_SOUNDS, current -> !current))
				.build());

		this.addDrawableChild(WildfireButton.builder()
				.text(Text.literal("X"))
				.scrollableText(false)
				.narrationSupplier(narrationText -> Text.translatable("gui.narrate.button", Text.translatable("gui.back")))
				.size(9, 9)
				.closes(this)
				.build());

		super.init();
	}

	@Override
	protected void drawTitle(DrawContext ctx, int x, int y) {
		ctx.drawText(textRenderer, getTitle(), x, y, 4473924, false);
	}

	private <TYPE> void set(ConfigKey<TYPE> key, Function<TYPE, TYPE> setter) {
		TYPE current = ClientConfiguration.INSTANCE.get(key);
		ClientConfiguration.INSTANCE.set(key, setter.apply(current));
		ClientConfiguration.INSTANCE.save();
	}
}
