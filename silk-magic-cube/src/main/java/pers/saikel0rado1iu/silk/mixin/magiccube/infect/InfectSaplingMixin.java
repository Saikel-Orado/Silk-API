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

package pers.saikel0rado1iu.silk.mixin.magiccube.infect;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.saikel0rado1iu.silk.api.magiccube.infect.InfectSapling;

/**
 * <h2 style="color:FFC800">{@link InfectSapling} 混入</h2>
 * 设置树苗成长感染回调
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu"><img alt="author" src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4"></a>
 * @since 0.1.0
 */
@Mixin(SaplingBlock.class)
abstract class InfectSaplingMixin {
	@Inject(method = "generate", at = @At(value = "INVOKE",
			target = "L net/minecraft/block/SaplingGenerator;generate(L net/minecraft/server/world/ServerWorld;L net/minecraft/world/gen/chunk/ChunkGenerator;L net/minecraft/util/math/BlockPos;L net/minecraft/block/BlockState;L net/minecraft/util/math/random/Random;)Z"),
			cancellable = true)
	private void generate(ServerWorld world, BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
		if (world.getBlockState(pos.down()).getBlock() instanceof InfectSapling infectSapling) {
			infectSapling.generate(world, pos, state, random, (SaplingBlock) (Object) this);
			ci.cancel();
		}
	}
}
