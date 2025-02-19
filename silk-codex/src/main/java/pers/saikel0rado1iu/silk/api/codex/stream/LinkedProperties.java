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

package pers.saikel0rado1iu.silk.api.codex.stream;

import com.google.common.collect.Sets;

import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * <h2 style="color:FFC800">有序的 {@link Properties}</h2>
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu"><img alt="author" src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4"></a>
 * @since 1.0.0
 */
public final class LinkedProperties extends Properties {
	private final LinkedHashSet<String> linkedSet = Sets.newLinkedHashSetWithExpectedSize(8);
	
	@Override
	public synchronized Object put(Object key, Object value) {
		linkedSet.add((String) key);
		return super.put(key, value);
	}
	
	/**
	 * 获取 linkedSet
	 *
	 * @return linkedSet
	 */
	public LinkedHashSet<String> linkedSet() {
		return linkedSet;
	}
}
