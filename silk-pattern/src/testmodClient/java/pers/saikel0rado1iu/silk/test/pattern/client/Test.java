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

package pers.saikel0rado1iu.silk.test.pattern.client;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import pers.saikel0rado1iu.silk.api.client.event.pattern.AddButtonInGameMenuCallback;
import pers.saikel0rado1iu.silk.api.client.event.pattern.AddButtonInTitleScreenCallback;
import pers.saikel0rado1iu.silk.api.client.pattern.screen.PlaceholderScreen;
import pers.saikel0rado1iu.silk.api.modpass.ModClient;
import pers.saikel0rado1iu.silk.api.modpass.ModData;
import pers.saikel0rado1iu.silk.api.modpass.ModPass;
import pers.saikel0rado1iu.silk.api.modpass.registry.ClientRegistrationProvider;
import pers.saikel0rado1iu.silk.api.pattern.widget.WidgetTexts;
import pers.saikel0rado1iu.silk.impl.SilkPattern;
import pers.saikel0rado1iu.silk.test.pattern.client.screen.ModScreenTest;

import java.util.Set;
import java.util.function.Function;

/**
 * 测试
 */
public final class Test implements ModClient {
	/**
	 * 模组主函数
	 *
	 * @param mod 提供的模组通
	 */
	@Override
	public void main(ModPass mod) {
		AddButtonInTitleScreenCallback.EVENT.register(new AddButtonInTitleScreenCallback() {
			@Override
			public <T extends Element & Drawable & Selectable> boolean add(MinecraftClient client, Screen parent, Function<T, T> addFunction, int y, int spacingY, boolean hasMainButton) {
				if (hasMainButton) return false;
				//noinspection unchecked
				addFunction.apply((T) ButtonWidget.builder(WidgetTexts.text(SilkPattern.getInstance(), "test"), button -> MinecraftClient.getInstance().setScreen(new ModScreenTest(parent, SilkPattern.getInstance())))
						.dimensions(parent.width / 2 - 100, y - spacingY, 200, 20).build());
				return true;
			}
		});
		AddButtonInGameMenuCallback.EVENT.register((client, parent, adder) -> adder.add(ButtonWidget.builder(WidgetTexts.text(SilkPattern.getInstance(), "test"),
				button -> MinecraftClient.getInstance().setScreen(new PlaceholderScreen(parent, SilkPattern.getInstance()))).width(204).build(), 2));
	}
	
	/**
	 * 注册表方法，提供注册表以供注册
	 *
	 * @return 注册表的类型集合
	 */
	@Override
	public Set<Class<? extends ClientRegistrationProvider<?>>> registry() {
		return ImmutableSet.of();
	}
	
	/**
	 * 用于提供模组数据以基于模组数据实现功能
	 *
	 * @return 模组数据
	 */
	@Override
	public ModData modData() {
		return SilkPattern.getInstance();
	}
}
