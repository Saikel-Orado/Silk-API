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

package pers.saikel0rado1iu.silk.test.modpass;

import pers.saikel0rado1iu.silk.api.modpass.ModBasicData;
import pers.saikel0rado1iu.silk.api.modpass.ModLaunch;
import pers.saikel0rado1iu.silk.api.modpass.ModPass;
import pers.saikel0rado1iu.silk.api.modpass.registry.LaunchRegistrationProvider;
import pers.saikel0rado1iu.silk.impl.SilkModPass;

import java.util.Set;

/**
 * Test {@link ModLaunch}
 */
public final class ModLaunchTest implements ModLaunch {
    /**
     * 模组主函数
     *
     * @param mod 提供的模组通
     */
    @Override
    public void main(ModPass mod) {
        modData().logger().info("ModLaunch");
    }

    /**
     * 注册表方法，提供注册表以供注册
     *
     * @return 注册表的类型集合
     */
    @Override
    public Set<Class<? extends LaunchRegistrationProvider<?>>> registries() {
        return Set.of();
    }

    /**
     * 用于提供模组数据以基于模组数据实现功能
     *
     * @return 模组数据
     */
    @Override
    public ModBasicData modData() {
        return SilkModPass.INSTANCE;
    }
}
