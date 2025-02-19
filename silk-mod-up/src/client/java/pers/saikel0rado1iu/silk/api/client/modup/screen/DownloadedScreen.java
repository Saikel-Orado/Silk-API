/*
 * This file is part of Silk API.
 * Copyright (C) 2023 Saikel Orado Liu
 *
 * Silk API is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Silk API is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Silk API. If not, see <https://www.gnu.org/licenses/>.
 */

package pers.saikel0rado1iu.silk.api.client.modup.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import pers.saikel0rado1iu.silk.api.client.modup.ClientUpdateManager;
import pers.saikel0rado1iu.silk.api.modup.UpdateData;
import pers.saikel0rado1iu.silk.api.pattern.widget.WidgetTexts;
import pers.saikel0rado1iu.silk.impl.SilkModUp;

import java.io.File;

/**
 * <h2 style="color:FFC800">下载完成屏幕</h2>
 * 下载完成屏幕
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu"><img alt="author" src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4"></a>
 * @since 0.1.0
 */
public class DownloadedScreen extends UpdateScreen {
	protected DownloadedScreen(@Nullable Screen parent, UpdateData updateData, ClientUpdateManager updateManager, Text title) {
		super(parent, updateData, updateManager, title);
		UpdateData.setCanShowChangelog(this.updateData.updateSettings(), true);
	}
	
	@Override
	protected void initWidgets(GridWidget gridWidget) {
		// 添加按钮
		int fullButtonWidth = screenWidth - INTERVAL;
		gridWidget.getMainPositioner().margin(0, BUTTON_SPACING, BUTTON_SPACING, 0);
		GridWidget.Adder adder = gridWidget.createAdder(2);
		adder.add(ButtonWidget.builder(Text.translatable(WidgetTexts.textKey(SilkModUp.getInstance(), "downloaded")).setStyle(Style.EMPTY.withBold(true)), (button) -> {
			Util.getOperatingSystem().open(new File(updateData.modData().jarPath().toString()));
			if (client != null) client.scheduleStop();
		}).width(fullButtonWidth).build(), 2);
		adder.add(returnToGameButton().width(fullButtonWidth).build(), 2);
		gridWidget.refreshPositions();
		SimplePositioningWidget.setPos(gridWidget, 1, screenHeight - BUTTON_SPACING - gridWidget.getHeight() + (height - screenHeight) / 2, width, height, 0.5F, 0);
		gridWidget.forEachChild(this::addDrawableChild);
		// 添加信息
		MultilineTextWidget messageText = new MultilineTextWidget(0, 0,
				Text.translatable(WidgetTexts.titleKey(SilkModUp.getInstance(), "downloaded"), updateData.modData().i18nName()), textRenderer)
				.setMaxWidth(screenWidth - INTERVAL);
		messageText.setPosition((width - messageText.getWidth()) / 2, (height - messageText.getHeight()) / 2 + 31);
		addDrawableChild(messageText);
	}
}