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

package pers.saikel0rado1iu.silk.util;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import pers.saikel0rado1iu.silk.annotation.SilkApi;

import java.util.function.Function;

/**
 * <p><b style="color:FFC800"><font size="+1">有关控制生物的特殊生成的部分数值与实用方法</font></b></p>
 * <p style="color:FFC800">此方法主要用于类似怪物类生物的生成，而不是动物类生物的生成。
 * 如果要实现动物类的生成要在 {@link Builder#otherChecker(SpawnRestriction.SpawnPredicate)} 中手动添加检测器</p>
 * <p style="color:FF0000">！注意！在特殊情况下，此方法可能看起来失效。此时需要将 {@link MobEntity#canSpawn(WorldView)} 与 {@link MobEntity#canSpawn(WorldAccess, SpawnReason)}
 * 返回 true 则可以修复此问题。但无法预测此操作可能带来的完整效果，但经过测试，绝大部分情况下并不会破坏生成规则</p>
 * <style="color:FFC800">
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu"><img src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4"><p>
 * @since 0.1.0
 */
@SilkApi
public interface SpawnUtil {
	@SilkApi
	static <T extends MobEntity> Builder<T> builder(Class<T> tClass) {
		return new Builder<>();
	}
	
	@SilkApi
	final class Builder<T extends MobEntity> {
		private boolean isDay = false;
		private boolean isNight = false;
		private boolean isMonster = false;
		private Function<Long, Boolean> timeChecker = aLong -> true;
		private Function<Integer, Boolean> lightChecker = aInt -> true;
		private Function<RegistryEntry<Biome>, Boolean> biomeChecker = biomeRegistryEntry -> true;
		private SpawnRestriction.SpawnPredicate<T> otherChecker = (type, world, spawnReason, pos, random) -> true;
		
		private Builder() {
		}
		
		/**
		 * 如果与 {@link Builder#night()} 一起使用则很可能无法生成生物
		 */
		@SilkApi
		public Builder<T> day() {
			isDay = true;
			return this;
		}
		
		/**
		 * 如果与 {@link Builder#day()} 一起使用则很可能无法生成生物
		 */
		@SilkApi
		public Builder<T> night() {
			isNight = true;
			return this;
		}
		
		@SilkApi
		public Builder<T> monster() {
			this.isMonster = true;
			return this;
		}
		
		@SilkApi
		public Builder<T> time(Function<Long, Boolean> timeChecker) {
			this.timeChecker = timeChecker;
			return this;
		}
		
		/**
		 * 此光照等级是从当前位置选取天空光照与方块光照中最高的作为参数，
		 * 如果要进行更精细的控制可以使用 {@link Builder#otherChecker(SpawnRestriction.SpawnPredicate)}
		 */
		@SilkApi
		public Builder<T> light(Function<Integer, Boolean> lightChecker) {
			this.lightChecker = lightChecker;
			return this;
		}
		
		@SilkApi
		public Builder<T> biome(Function<RegistryEntry<Biome>, Boolean> biomeChecker) {
			this.biomeChecker = biomeChecker;
			return this;
		}
		
		@SilkApi
		public Builder<T> otherChecker(SpawnRestriction.SpawnPredicate<T> otherChecker) {
			this.otherChecker = otherChecker;
			return this;
		}
		
		@SilkApi
		public SpawnRestriction.SpawnPredicate<T> build() {
			return (type, world, spawnReason, pos, random) -> MobEntity.canMobSpawn(type, world, spawnReason, pos, random)
					&& biomeChecker.apply(world.getBiome(pos))
					&& timeChecker.apply(world.getLunarTime())
					&& (!isDay || world.toServerWorld().isDay())
					&& (!isNight || world.toServerWorld().isNight())
					&& (!isMonster || world.getDifficulty() != Difficulty.PEACEFUL)
					&& lightChecker.apply(Math.max(world.getLightLevel(LightType.SKY, pos), world.getLightLevel(LightType.BLOCK, pos)))
					&& otherChecker.test(type, world, spawnReason, pos, random);
		}
	}
}
