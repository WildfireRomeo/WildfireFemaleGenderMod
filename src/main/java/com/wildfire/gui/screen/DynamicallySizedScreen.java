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
import com.wildfire.main.WildfireGender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dynamically resized GUI screen based on how many elements are displayed, rendered in a vertical list.
 */
public abstract class DynamicallySizedScreen extends BaseWildfireScreen {

	private static final int TOP_HEIGHT = 15;
	private static final int BOTTOM_HEIGHT = 9;
	private static final int BOTTOM_V = TOP_HEIGHT + 21;
	private static final Identifier BACKGROUND = new Identifier(WildfireGender.MODID, "textures/gui/settings_bg.png");

	/**
	 * The total width of the entire UI, including the background
	 */
	public static final int BACKGROUND_WIDTH = 172;
	/**
	 * The width for individual UI elements such as buttons and sliders
	 */
	public static final int WIDTH = 157;
	/**
	 * How tall each UI element should be
	 */
	public static final int HEIGHT = 20;

	/**
	 * How many elements there are, not including any {@link WildfireButton#isCloseButton() close buttons}; this is set
	 * when {@link #init()} is called
	 */
	protected int listElements;

	protected DynamicallySizedScreen(Text title, Screen parent, UUID uuid) {
		super(title, parent, uuid);
	}

	@Override
	protected void init() {
		this.listElements = this.getListElementCount();
		this.repositionElements();
		super.init();
	}

	/**
	 * Predicate method checking if the provided {@link Element} should be rendered in the drawn list of widgets
	 *
	 * @implNote This returns {@code false} on any {@link WildfireButton#isCloseButton() close buttons},
	 *           {@link ClickableWidget non-clickable} and/or {@link ClickableWidget#visible invisible widgets}
	 */
	public static boolean shouldBeInList(Element element) {
		if(element instanceof WildfireButton button && button.isCloseButton()) {
			return false;
		}
		return element instanceof ClickableWidget clickable && clickable.visible;
	}

	/**
	 * Returns the count of all applicable elements that should be rendered in the drawn list
	 *
	 * @apiNote This is a relatively expensive method call; you should instead use the cached {@link #listElements} field.
	 */
	public int getListElementCount() {
		return Math.toIntExact(this.children().stream()
				.filter(DynamicallySizedScreen::shouldBeInList)
				.count()) + 1;
	}

	/**
	 * Returns the total height of all combined elements
	 */
	public int getTotalElementHeight() {
		return listElements * HEIGHT;
	}

	/**
	 * Returns the Y position where the top of the UI begins to render
	 */
	public int getTopY() {
		return (this.height / 2) - (getTotalElementHeight() / 2) + TOP_HEIGHT;
	}

	/**
	 * Returns the Y position where the GUI ends rendering
	 */
	public int getBottomY() {
		return getTopY() + getTotalElementHeight();
	}

	/**
	 * Dynamically repositions all elements on the current screen to fit within the GUI, including placing a
	 * {@link WildfireButton#isCloseButton() close button} on the top of the UI
	 */
	protected void repositionElements() {
		final AtomicInteger count = new AtomicInteger(0);
		final int top = getTopY();
		children().forEach(element -> {
			if(element instanceof WildfireButton button && button.isCloseButton()) {
				button.setY(top - 11);
				button.setX(this.width / 2 + 73);
			} else if(element instanceof ClickableWidget clickable && clickable.visible) {
				clickable.setY(top + (HEIGHT * count.getAndIncrement()));
				clickable.setX((this.width / 2) - (WIDTH / 2) - 1);
			}
		});
	}

	@Override
	public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.renderBackground(ctx, mouseX, mouseY, delta);
		final int width = BACKGROUND_WIDTH;
		final int x = (this.width - width) / 2;
		final int topY = getTopY();
		ctx.drawTexture(BACKGROUND, x, topY - TOP_HEIGHT, 0, 0, width, TOP_HEIGHT);
		final int elements = listElements - 1;
		int y = topY;
		for(int count = 0; count < elements; count++) {
			ctx.drawTexture(BACKGROUND, x, y, 0, TOP_HEIGHT, width, HEIGHT);
			y += HEIGHT;
		}
		ctx.drawTexture(BACKGROUND, x, getTopY() + (HEIGHT * elements), 0, BOTTOM_V, width, BOTTOM_HEIGHT);
		drawTitle(ctx, (this.width / 2) - 79, getTopY() - 10);
	}

	/**
	 * Utility method for rendering a title on the rendered GUI at the correct X and Y positions
	 */
	protected abstract void drawTitle(DrawContext ctx, int x, int y);
}
