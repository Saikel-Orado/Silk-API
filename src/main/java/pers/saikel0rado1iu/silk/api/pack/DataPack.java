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

package pers.saikel0rado1iu.silk.api.pack;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pers.saikel0rado1iu.silk.annotation.SilkApi;
import pers.saikel0rado1iu.silk.api.ModBasicData;

/**
 * <p><b style="color:FFC800"><font size="+1">用于数据包的构建</font></b></p>
 * <style="color:FFC800">
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu"><img src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4"><p>
 * @since 0.1.0
 */
@SilkApi
public final class DataPack {
	private final ModBasicData mod;
	private final ResourcePackActivationType type;
	private final String packRoot;
	
	public DataPack(ModBasicData mod, ResourcePackActivationType type, String packRoot) {
		this.mod = mod;
		this.type = type;
		this.packRoot = packRoot;
	}
	
	public static String getName(ModBasicData mod) {
		return "dataPack." + mod.getId() + ".name";
	}
	
	@SilkApi
	public void registry() {
		ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(packRoot), mod.getMod(), Text.translatable(getName(mod)), type);
	}
}
