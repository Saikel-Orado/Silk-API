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

package pers.saikel0rado1iu.silk.api.modpass.pack;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.*;

/**
 * <h2>组资源包</h2>
 * 表示一个组资源包，将多个资源包作为一个资源包保存。
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu">
 *         <img alt="author" src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4">
 *         </a>
 * @since 1.2.2
 */
public class GroupResourcePack implements ResourcePack {
    protected final ResourceType type;
    protected final ModPack.Group group;
    protected final List<? extends ResourcePack> packs;
    protected final List<String> orderList;
    protected final Map<String, List<ResourcePack>> namespacedPacks = new Object2ObjectOpenHashMap<>();

    /**
     * @param type      资源类型
     * @param packs     所有资源包
     * @param orderList 排序列表
     * @param group     组模组包
     */
    public GroupResourcePack(ResourceType type, List<? extends ResourcePack> packs,
                             List<String> orderList, ModPack.Group group) {
        this.type = type;
        this.packs = packs;
        this.group = group;
        this.orderList = orderList;
        this.packs.forEach(pack -> pack.getNamespaces(this.type).forEach(
                namespace -> this.namespacedPacks
                        .computeIfAbsent(namespace, value -> new ArrayList<>())
                        .add(pack)));
    }

    private List<? extends ResourcePack> orderList(List<? extends ResourcePack> packList) {
        if (packList == null) {
            return null;
        }
        List<? extends ResourcePack> packs = Lists.newArrayList(packList);
        Map<String, Integer> orderMap = Maps.newHashMapWithExpectedSize(packs.size());
        for (int count = 0; count < orderList.size(); count++) {
            orderMap.put(orderList.get(count), Integer.MAX_VALUE - count);
        }
        packs.sort(Comparator.comparingInt(pack -> orderMap.getOrDefault(pack.getId(), 0)));
        Collections.reverse(packs);
        return packs;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        String fileName = String.join("/", segments);
        if ("pack.mcmeta".equals(fileName)) {
            String pack = String.format(
                    "{\"pack\":{\"pack_format\":%s,\"description\":{\"translate\":\"%s\"}}}",
                    SharedConstants.getGameVersion().getResourceVersion(type),
                    group.descKey);
            return () -> IOUtils.toInputStream(pack, Charsets.UTF_8);
        }
        String subPath = ("resourcepacks/" + group.id().getPath())
                .replace("/", FileSystems.getDefault().getSeparator());
        try (ModNioResourcePack modPack = ModNioResourcePack.create(group.id().toString(),
                group.modData().mod(), subPath, type, group.type(), false)) {
            return modPack.openRoot(segments);
        }
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        List<? extends ResourcePack> packs = orderList(namespacedPacks.get(id.getNamespace()));
        if (packs == null) {
            return null;
        }
        InputSupplier<InputStream> inputSupplier = null;
        for (ResourcePack pack : packs) {
            InputSupplier<InputStream> supplier = pack.open(type, id);
            if (supplier != null) {
                inputSupplier = supplier;
            }
        }
        return inputSupplier;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix,
                              ResultConsumer consumer) {
        List<? extends ResourcePack> packs = orderList(namespacedPacks.get(namespace));
        if (packs == null) {
            return;
        }
        for (ResourcePack pack : packs) {
            pack.findResources(type, namespace, prefix, consumer);
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return namespacedPacks.keySet();
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        InputSupplier<InputStream> inputSupplier = openRoot("pack.mcmeta");
        if (inputSupplier == null) {
            return null;
        }
        try (InputStream input = inputSupplier.get()) {
            return AbstractFileResourcePack.parseMetadata(metaReader, input);
        }
    }

    @Override
    public ResourcePackInfo getInfo() {
        return group.info;
    }

    @Override
    public void close() {
        packs.forEach(ResourcePack::close);
    }

    /**
     * 追加资源方法
     *
     * @param type      资源类型
     * @param id        资源包标识符
     * @param resources 资源列表
     */
    public void appendResources(ResourceType type, Identifier id, List<Resource> resources) {
        List<? extends ResourcePack> packs = orderList(namespacedPacks.get(id.getNamespace()));
        if (packs == null) {
            return;
        }
        Identifier metadataId = NamespaceResourceManager.getMetadataPath(id);
        for (ResourcePack pack : packs) {
            InputSupplier<InputStream> supplier = pack.open(type, id);
            if (supplier == null) {
                continue;
            }
            InputSupplier<ResourceMetadata> metadataSupplier = () -> {
                InputSupplier<InputStream> rawMetadataSupplier = pack.open(type, metadataId);
                return rawMetadataSupplier != null ? NamespaceResourceManager.loadMetadata(rawMetadataSupplier) : ResourceMetadata.NONE;
            };
            resources.add(new Resource(pack, supplier, metadataSupplier));
        }
    }

    /**
     * <h2>组资源包工厂类</h2>
     * 表示一个组资源包的工厂类
     *
     * @param type      资源类型
     * @param packs     资源包列表
     * @param orderList 排序列表
     * @param group     组模组包
     * @author <a href="https://github.com/Saikel-Orado-Liu">
     *         <img alt="author" src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4">
     *         </a>
     * @since 1.2.2
     */
    public record Factory(ResourceType type, List<? extends ResourcePack> packs,
                          List<String> orderList,
                          ModPack.Group group) implements ResourcePackProfile.PackFactory {
        @Override
        public ResourcePack open(ResourcePackInfo info) {
            return new GroupResourcePack(type, packs, orderList, group);
        }

        @SuppressWarnings("UnstableApiUsage")
        @Override
        public ResourcePack openWithOverlays(ResourcePackInfo info,
                                             ResourcePackProfile.Metadata metadata) {
            final ResourcePack basePack = open(group.info);
            final List<String> overlays = metadata.overlays();
            if (overlays.isEmpty()) {
                return basePack;
            }
            final List<ResourcePack> overlayPacks = new ArrayList<>(overlays.size());
            for (String overlay : overlays) {
                List<ModResourcePack> innerPacks = new ArrayList<>();
                ModResourcePackUtil.appendModResourcePacks(innerPacks, type, overlay);
                overlayPacks.add(new GroupResourcePack(type, innerPacks, orderList, group));
            }
            return new OverlayResourcePack(basePack, overlayPacks);
        }
    }
}
