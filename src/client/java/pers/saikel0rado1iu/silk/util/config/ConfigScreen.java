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

package pers.saikel0rado1iu.silk.util.config;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pers.saikel0rado1iu.silk.annotation.SilkApi;
import pers.saikel0rado1iu.silk.api.ModBasicData;

import java.util.Arrays;
import java.util.List;

/**
 * <p><b style="color:FFC800"><font size="+1">用于构建配置屏幕</font></b></p>
 * <style="color:FFC800">
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu"><img src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4"><p>
 * @since 0.1.0
 */
@SilkApi
public final class ConfigScreen extends Screen {
	private final boolean notSub;
	private final boolean isDouble;
	private final String keyPrefix;
	private final Screen parent;
	private final ConfigData configData;
	private OptionListWidget optionListWidget;
	private List<SimpleOption<?>> simpleOptionList;
	
	@SilkApi
	public ConfigScreen(Screen parent, ConfigData configData) {
		this(parent, true, false, configData, "", Text.translatable(getModConfigText(configData.mod, "")));
	}
	
	@SilkApi
	public ConfigScreen(Screen parent, ConfigData configData, boolean isDouble) {
		this(parent, true, isDouble, configData, "", Text.translatable(getModConfigText(configData.mod, "")));
	}
	
	private ConfigScreen(Screen parent, boolean notSub, boolean isDouble, ConfigData configData, String keyPrefix, Text title) {
		super(configData.type == ConfigData.Type.EXPERIMENTAL ? title.copy().formatted(Formatting.RED)
				: (configData.type == ConfigData.Type.DEPRECATED) ? title.copy().formatted(Formatting.YELLOW, Formatting.ITALIC)
				: title);
		this.notSub = notSub;
		this.isDouble = isDouble;
		this.parent = parent;
		this.keyPrefix = keyPrefix;
		this.configData = configData;
	}
	
	/**
	 * 获取模组配置文本翻译键
	 *
	 * @param mod 模组数据
	 * @param key 翻译键
	 * @return 模组配置文本翻译键
	 */
	public static String getModConfigText(ModBasicData mod, String key) {
		return "config." + mod.getId() + '.' + key + ("".equals(key) ? "text" : ".text");
	}
	
	/**
	 * 获取模组配置提示翻译键
	 *
	 * @param mod 模组数据
	 * @param key 翻译键
	 * @return 模组配置提示翻译键
	 */
	public static String getModConfigTip(ModBasicData mod, String key) {
		return "config." + mod.getId() + '.' + key + ("".equals(key) ? "tip" : ".tip");
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		render(context, optionListWidget, mouseX, mouseY, delta);
	}
	
	private void render(DrawContext context, OptionListWidget optionButtons, int mouseX, int mouseY, float tickDelta) {
		if (configData.type == ConfigData.Type.EXPERIMENTAL) {
			simpleOptionList.forEach(simpleOption -> {
				ClickableWidget widget = optionListWidget.getWidgetFor(simpleOption);
				if (widget == null || widget.getMessage().copy().getStyle().isItalic()) return;
				widget.setMessage(widget.getMessage().copy().formatted(Formatting.RED));
			});
		} else if (configData.type == ConfigData.Type.DEPRECATED) {
			simpleOptionList.forEach(simpleOption -> {
				ClickableWidget widget = optionListWidget.getWidgetFor(simpleOption);
				if (widget == null) return;
				if (widget.getMessage().getStyle().getColor() == null || !widget.getMessage().getStyle().getColor().getName().equals(Formatting.RED.getName()))
					widget.setMessage(widget.getMessage().copy().formatted(Formatting.YELLOW, Formatting.ITALIC));
				widget.active = false;
			});
		}
		renderBackground(context);
		optionButtons.render(context, mouseX, mouseY, tickDelta);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
		super.render(context, mouseX, mouseY, tickDelta);
	}
	
	@Override
	public void close() {
		if (client == null) return;
		client.setScreen(parent);
	}
	
	@Override
	protected void init() {
		super.init();
		// 添加黑色透明窗口
		optionListWidget = new OptionListWidget(client, width, height, 32, height - 32, 25);
		if (configData.type == ConfigData.Type.DEV) return;
		simpleOptionList = addSimpleOption();
		addSelectableChild(optionListWidget);
	}
	
	private List<SimpleOption<?>> addSimpleOption() {
		List<SimpleOption<?>> simpleOptionList = Lists.newArrayListWithExpectedSize(8);
		// 添加"支持我们"按钮
		//addDrawableChild(getSupportUsButton(this));
		// 添加完成按钮
		addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
			if (notSub) new ConfigWriter(configData).save();
			close();
		}).dimensions(width / 2 - 100, height - 26, 200, 20).build());
		// 添加所有配置选项按钮
		Object prev = null;
		for (String key : configData.configs.keySet()) {
			SimpleOption<?> simpleOption = null;
			String realKey = keyPrefix + key;
			Object defaultConfig = configData.defaults.get(key);
			Object object = configData.configs.get(key);
			if (object instanceof Boolean bool) {
				simpleOption = SimpleOption.ofBoolean(getModConfigText(configData.mod, realKey), value -> Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + (value ? ".on" : ".off")))), bool, (value) -> {
					configData.setConfig(key, value);
					if (notSub) new ConfigWriter(configData).save();
				});
			} else if (object instanceof Enum<?> e) {
				List<Object> enums = List.of(Arrays.stream(e.getDeclaringClass().getEnumConstants()).toArray());
				simpleOption = new SimpleOption<>(getModConfigText(configData.mod, realKey), value -> {
					for (Object o : enums)
						if (o == e)
							return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + '.' + enums.get(value).toString().toLowerCase())));
					return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey)));
				}, (optionText, value) -> Text.translatable(getModConfigText(configData.mod, realKey + '.' + enums.get(value).toString().toLowerCase())), new SimpleOption.MaxSuppliableIntCallbacks(0, () -> enums.size() - 1, enums.size() - 1), enums.indexOf(e), value -> {
					configData.setConfig(key, enums.get(value));
					if (notSub) new ConfigWriter(configData).save();
				});
			} else if (object instanceof List<?> list) {
				if (list.get(2) instanceof Integer i) {
					simpleOption = new SimpleOption<>(getModConfigText(configData.mod, realKey), value -> {
						if (value == list.get(0)) return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + ".min")));
						else if (value == list.get(1)) return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + ".max")));
						else if (value == defaultConfig) return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + ".default")));
						else return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey)));
					}, (optionText, value) -> {
						if (value == list.get(0)) return Text.translatable(getModConfigText(configData.mod, realKey + ".min"));
						else if (value == list.get(1)) return Text.translatable(getModConfigText(configData.mod, realKey + ".max"));
						else if (value == defaultConfig) return Text.translatable(getModConfigText(configData.mod, realKey + ".default"));
						else return Text.translatable(getModConfigText(configData.mod, realKey));
					}, new SimpleOption.ValidatingIntSliderCallbacks((int) list.get(0), (int) list.get(1)), Codec.intRange((int) list.get(0), (int) list.get(1)), i, value -> {
						configData.setConfig(key, value);
						if (notSub) new ConfigWriter(configData).save();
					});
				} else if (list.get(2) instanceof Float f) {
					simpleOption = new SimpleOption<>(getModConfigText(configData.mod, realKey), value -> {
						if (value == list.get(0)) return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + ".min")));
						else if (value == list.get(1)) return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + ".max")));
						else if (value == defaultConfig) return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey + ".default")));
						else return Tooltip.of(Text.translatable(getModConfigTip(configData.mod, realKey)));
					}, (optionText, value) -> {
						if (value == list.get(0)) return Text.translatable(getModConfigText(configData.mod, realKey + ".min"));
						else if (value == list.get(1)) return Text.translatable(getModConfigText(configData.mod, realKey + ".max"));
						else if (value == defaultConfig) return Text.translatable(getModConfigText(configData.mod, realKey + ".default"));
						else return Text.translatable(getModConfigText(configData.mod, realKey));
					}, new SimpleOption.ValidatingIntSliderCallbacks((int) ((float) list.get(0) * 100), (int) ((float) list.get(1) * 100)).withModifier(sliderProgressValue -> (float) sliderProgressValue / 100.0, value -> (int) (value * 100)), Codec.doubleRange((float) list.get(0), (float) list.get(1)), Double.valueOf(f), value -> {
						configData.setConfig(key, value.floatValue());
						if (notSub) new ConfigWriter(configData).save();
					});
				}
			} else if (object instanceof ConfigData cd) {
				simpleOption = SimpleOption.ofBoolean(getModConfigText(configData.mod, realKey), SimpleOption.constantTooltip(Text.of(getModConfigTip(configData.mod, realKey))),
						(optionText, value) -> Text.of(""), false, (value) -> MinecraftClient.getInstance().setScreen(new ConfigScreen(this, false, isDouble, cd, realKey + '.', Text.translatable(getModConfigText(configData.mod, realKey)))));
			}
			simpleOptionList.add(simpleOption);
			if (!isDouble) {
				optionListWidget.addSingleOptionEntry(simpleOption);
			} else if (simpleOptionList.size() % 2 == 0) {
				SimpleOption<?> prevOption = simpleOptionList.get(simpleOptionList.size() - 2);
				optionListWidget.addOptionEntry(prevOption, simpleOption);
				if (prev instanceof ConfigData cd) {
					if (cd.type == ConfigData.Type.EXPERIMENTAL) {
						ClickableWidget widget = optionListWidget.getWidgetFor(prevOption);
						if (widget != null) widget.setMessage(widget.getMessage().copy().formatted(Formatting.RED));
					} else if (cd.type == ConfigData.Type.DEPRECATED) {
						ClickableWidget widget = optionListWidget.getWidgetFor(prevOption);
						if (widget != null) widget.setMessage(widget.getMessage().copy().formatted(Formatting.YELLOW, Formatting.ITALIC));
					}
				}
			}
			prev = object;
			if (!(object instanceof ConfigData cd)) continue;
			if (cd.type == ConfigData.Type.EXPERIMENTAL) {
				ClickableWidget widget = optionListWidget.getWidgetFor(simpleOption);
				if (widget != null) widget.setMessage(widget.getMessage().copy().formatted(Formatting.RED));
			} else if (cd.type == ConfigData.Type.DEPRECATED) {
				ClickableWidget widget = optionListWidget.getWidgetFor(simpleOption);
				if (widget != null) widget.setMessage(widget.getMessage().copy().formatted(Formatting.YELLOW, Formatting.ITALIC));
			}
		}
		if (isDouble && simpleOptionList.size() % 2 == 1) optionListWidget.addOptionEntry(simpleOptionList.get(simpleOptionList.size() - 1), null);
		return simpleOptionList;
	}
}
