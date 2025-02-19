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

package pers.saikel0rado1iu.silk.api.generate.data.client;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import pers.saikel0rado1iu.silk.api.magiccube.cauldron.LeveledCauldronLikeBlock;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <h2 style="color:FFC800">扩展方块状态模型生成器</h2>
 * 提供了其他可能用到的高级方块状态模型方法
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu"><img alt="author" src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4"></a>
 * @since 1.0.0
 */
public class ExtendedBlockStateModelGenerator extends BlockStateModelGenerator {
	/**
	 * @param blockStateModelGenerator 方块模型生成器
	 */
	public ExtendedBlockStateModelGenerator(BlockStateModelGenerator blockStateModelGenerator) {
		this(blockStateModelGenerator.blockStateCollector, blockStateModelGenerator.modelCollector, blockStateModelGenerator.simpleItemModelExemptionCollector);
	}
	
	/**
	 * @param blockStateCollector               方块状态收集器
	 * @param modelCollector                    模型收集器
	 * @param simpleItemModelExemptionCollector 简单物品模型排除收集器
	 */
	public ExtendedBlockStateModelGenerator(Consumer<BlockStateSupplier> blockStateCollector, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<Item> simpleItemModelExemptionCollector) {
		super(blockStateCollector, modelCollector, simpleItemModelExemptionCollector);
	}
	
	/**
	 * 获取坩埚纹理图表
	 *
	 * @param empty   空坩埚块
	 * @param content 内容物
	 * @return 坩埚纹理图表
	 */
	public static TextureMap getCauldronTextureMap(Block empty, Identifier content) {
		return new TextureMap()
				.put(TextureKey.PARTICLE, TextureMap.getSubId(empty, "_side"))
				.put(TextureKey.SIDE, TextureMap.getSubId(empty, "_side"))
				.put(TextureKey.TOP, TextureMap.getSubId(empty, "_top"))
				.put(TextureKey.BOTTOM, TextureMap.getSubId(empty, "_bottom"))
				.put(TextureKey.INSIDE, TextureMap.getSubId(empty, "_inner"))
				.put(TextureKey.CONTENT, content);
	}
	
	/**
	 * 注册立方体柱模型
	 *
	 * @param block       方块
	 * @param sideTexture 侧边纹理
	 * @param endTexture  两端纹理
	 */
	public void registerCubeColumn(Block block, Identifier sideTexture, Identifier endTexture) {
		TextureMap textureMap = TextureMap.sideEnd(sideTexture, endTexture);
		Identifier identifier = Models.CUBE_COLUMN.upload(block, textureMap, modelCollector);
		blockStateCollector.accept(createSingletonBlockState(block, identifier));
	}
	
	/**
	 * 注册地毯模型
	 *
	 * @param carpet 地毯方块
	 * @param isFlat 是平面
	 */
	public void registerCarpet(Block carpet, boolean isFlat) {
		if (isFlat) registerItemModel(carpet);
		blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(carpet, TexturedModel.CARPET.get(carpet).upload(carpet, modelCollector)));
	}
	
	/**
	 * 注册顶层土块模型
	 *
	 * @param soil 泥土方块
	 */
	public void registerTopSoil(Block soil) {
		Identifier identifier = TextureMap.getId(Blocks.DIRT);
		BlockStateVariant blockStateVariant = BlockStateVariant.create().put(VariantSettings.MODEL, Models.CUBE_BOTTOM_TOP.upload(Blocks.GRASS_BLOCK, "_snow", new TextureMap()
				.put(TextureKey.BOTTOM, identifier).inherit(TextureKey.BOTTOM, TextureKey.PARTICLE)
				.put(TextureKey.TOP, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_top"))
				.put(TextureKey.SIDE, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_snow")), modelCollector));
		Identifier modelId = TexturedModel.CUBE_BOTTOM_TOP.get(soil)
				.textures(textures -> textures.put(TextureKey.BOTTOM, identifier))
				.upload(soil, modelCollector);
		registerTopSoil(soil, modelId, blockStateVariant);
	}
	
	/**
	 * 注册藤蔓模型
	 *
	 * @param plant     植株方块
	 * @param plantStem 植株茎
	 * @param tintType  着色类型
	 */
	public void registerVines(Block plant, Block plantStem, BlockStateModelGenerator.TintType tintType) {
		registerPlantPart(plant, plantStem, tintType);
		registerItemModel(plant, "_plant");
		excludeFromSimpleItemModelGeneration(plantStem);
	}
	
	/**
	 * 注册连接块模型
	 *
	 * @param block 连接块
	 */
	public void registerConnectingBlock(Block block) {
		Identifier side = ModelIds.getBlockSubModelId(block, "_side");
		Identifier noSide = ModelIds.getBlockSubModelId(block, "_noside");
		Identifier noSide1 = ModelIds.getBlockSubModelId(block, "_noside1");
		Identifier noSide2 = ModelIds.getBlockSubModelId(block, "_noside2");
		Identifier noSide3 = ModelIds.getBlockSubModelId(block, "_noside3");
		blockStateCollector.accept(MultipartBlockStateSupplier.create(block)
				.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, side))
				.with(When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true))
				.with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true))
				.with(When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true))
				.with(When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true))
				.with(When.create().set(Properties.DOWN, true), BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true))
				.with(When.create().set(Properties.NORTH, false), new BlockStateVariant[]{BlockStateVariant.create().put(VariantSettings.MODEL, noSide).put(VariantSettings.WEIGHT, 2),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide1), BlockStateVariant.create().put(VariantSettings.MODEL, noSide2), BlockStateVariant.create().put(VariantSettings.MODEL, noSide3)})
				.with(When.create().set(Properties.EAST, false), new BlockStateVariant[]{BlockStateVariant.create().put(VariantSettings.MODEL, noSide1).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide).put(VariantSettings.WEIGHT, 2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)})
				.with(When.create().set(Properties.SOUTH, false), new BlockStateVariant[]{BlockStateVariant.create().put(VariantSettings.MODEL, noSide2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide3).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide).put(VariantSettings.WEIGHT, 2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide1).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)})
				.with(When.create().set(Properties.WEST, false), new BlockStateVariant[]{BlockStateVariant.create().put(VariantSettings.MODEL, noSide3).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide).put(VariantSettings.WEIGHT, 2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide1).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)})
				.with(When.create().set(Properties.UP, false), new BlockStateVariant[]{BlockStateVariant.create().put(VariantSettings.MODEL, noSide).put(VariantSettings.WEIGHT, 2).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide3).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide1).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide2).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)})
				.with(When.create().set(Properties.DOWN, false), BlockStateVariant.create().put(VariantSettings.MODEL, noSide3).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide2).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide1).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true),
						BlockStateVariant.create().put(VariantSettings.MODEL, noSide).put(VariantSettings.WEIGHT, 2).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)));
	}
	
	/**
	 * 注册自定义模型方块
	 *
	 * @param block      方块
	 * @param isFlatItem 是否为平面物品
	 */
	public void registerCustomModel(Block block, boolean isFlatItem) {
		if (isFlatItem) {
			registerItemModel(block.asItem());
		} else {
			new Model(Optional.of(ModelIds.getBlockModelId(block)), Optional.empty()).upload(ModelIds.getItemModelId(block.asItem()), new TextureMap(), modelCollector);
		}
		registerSimpleState(block);
	}
	
	/**
	 * 注册装满的坩埚块
	 *
	 * @param fluidTexture 液体纹理
	 * @param empty        空坩埚块
	 * @param full         装满的坩埚块
	 * @param template     模型模板
	 */
	public void registerFullCauldron(Identifier fluidTexture, Block empty, Block full, Model template) {
		blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(full, template.upload(full, getCauldronTextureMap(empty, fluidTexture), modelCollector)));
	}
	
	/**
	 * 注册可调整液面坩埚块
	 *
	 * @param fluidTexture 液体纹理
	 * @param empty        空坩埚块
	 * @param leveled      可调整液面坩埚块
	 * @param templates    模型模板
	 */
	public void registerLeveledCauldron(Identifier fluidTexture, Block empty, LeveledCauldronLikeBlock leveled, Model... templates) {
		BlockStateVariantMap.SingleProperty<Integer> map = BlockStateVariantMap.create(leveled.level());
		for (int count = 1; count <= leveled.maxLevel(); count++) {
			map = map.register(count, BlockStateVariant.create().put(VariantSettings.MODEL, templates[count - 1].upload(leveled, count != leveled.maxLevel() ? "_level" + count : "_full",
					getCauldronTextureMap(empty, fluidTexture), modelCollector)));
		}
		blockStateCollector.accept(VariantsBlockStateSupplier.create(leveled).coordinate(map));
	}
}
