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

package pers.saikel0rado1iu.silk.test.ropestick;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import pers.saikel0rado1iu.silk.impl.SilkRopeStick;
import pers.saikel0rado1iu.silk.api.ropestick.ItemGroupCreator;

/**
 * Test {@link ItemGroupCreator}
 */
public interface ItemGroupCreatorTest {
	/**
	 * test_item_group1
	 */
	RegistryKey<ItemGroup> TEST_ITEM_GROUP1 = ItemGroupCreator.create(SilkRopeStick.getInstance(), "test_item_group1");
	/**
	 * test_item_group2
	 */
	RegistryKey<ItemGroup> TEST_ITEM_GROUP2 = ItemGroupCreator.create(Items.ACACIA_BOAT, SilkRopeStick.getInstance(), "test_item_group2");
}
