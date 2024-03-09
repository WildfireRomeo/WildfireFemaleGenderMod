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

import com.wildfire.main.config.BooleanConfigKey;
import com.wildfire.main.config.ClientConfiguration;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.List;

@SuppressWarnings("unchecked")
public interface IWildfireWidgetBuilder<WIDGET extends ClickableWidget, BUILDER extends IWildfireWidgetBuilder<?, ?>> {
	BUILDER active(boolean active);
	BUILDER tooltip(Tooltip tooltip);
	BUILDER x(int x);
	BUILDER y(int y);
	BUILDER width(int width);
	BUILDER height(int height);

	/**
	 * Sets the X and Y coordinates of the button drawn on screen
	 *
	 * @implNote This is shorthand for {@link #x(int)}.{@link #y(int)}
	 */
	default BUILDER position(int x, int y) {
		return (BUILDER) this.x(x).y(y);
	}

	/**
	 * Sets the width and height for the built button
	 *
	 * @implNote This is shorthand for {@link #height(int)}.{@link #width(int)}
	 */
	default BUILDER size(int width, int height) {
		return (BUILDER) this.height(height).width(width);
	}

	/**
	 * Require that the provided {@link BooleanConfigKey} from {@link ClientConfiguration} is {@code true}
	 */
	default BUILDER require(BooleanConfigKey clientConfigOption) {
		return this.require(ClientConfiguration.INSTANCE.get(clientConfigOption),
				Tooltip.of(Text.translatable("wildfire_gender.tooltip.disabled_client_setting")));
	}

	/**
	 * Require at least one of the provided {@link BooleanConfigKey}s from {@link ClientConfiguration} is {@code true}
	 */
	default BUILDER requireAny(List<BooleanConfigKey> clientConfigOptions) {
		return this.require(clientConfigOptions.stream().anyMatch(ClientConfiguration.INSTANCE::get),
				Tooltip.of(Text.translatable("wildfire_gender.tooltip.disabled_client_setting")));
	}

	/**
	 * Require that the provided boolean value is {@code true}, making the built widget {@link #active inactive}
	 * and setting the provided tooltip if not.
	 */
	default BUILDER require(boolean value, Tooltip tooltip) {
		if(!value) {
			return (BUILDER) this.active(false).tooltip(tooltip);
		}
		return (BUILDER) this;
	}

	WIDGET build();
}
