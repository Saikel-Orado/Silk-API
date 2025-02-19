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

package pers.saikel0rado1iu.silk.api.modpass.log;

import net.fabricmc.loader.api.FabricLoader;
import pers.saikel0rado1iu.silk.api.modpass.ModPass;
import pers.saikel0rado1iu.silk.impl.SilkModPass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * <h2>更新日志</h2>
 * 用于读取模组的更新日志
 *
 * @author <a href="https://github.com/Saikel-Orado-Liu">
 *         <img alt="author" src="https://avatars.githubusercontent.com/u/88531138?s=64&v=4">
 *         </a>
 * @since 1.0.0
 */
public interface Changelog {
    /**
     * 获取更新日志路径
     * <p>
     * 读取的更新日志位置于资源包根目录下的 {@code CHANGELOG} 文件夹内
     *
     * @param modPass 模组通
     * @return 更新日志路径
     * @throws URISyntaxException     可能发生 URI 语法异常
     * @throws NoSuchElementException 可能发生无此元素异常
     */
    static Path path(ModPass modPass) throws NoSuchElementException, URISyntaxException {
        return FabricLoader.getInstance()
                           .getModContainer(modPass.modData().id())
                           .orElseThrow()
                           .findPath("")
                           .orElseThrow()
                           .resolve("CHANGELOG");
    }

    /**
     * 获取特定语言的更新日志
     * <p>
     * 不同语言的日志文件名为 {@code "id().langCode.md"}
     *
     * @param modPass  模组通
     * @param langCode 语言代码
     * @return 如果有 {@code langCode} 的更新日志则返回此语言的更新日志<p> 如果没有 {@code langCode} 的更新日志则返回 {@code en_us}
     *         语言的更新日志<p> 如果没有更新日志则返回 {@link  Optional#empty()}
     */
    static Optional<Path> get(ModPass modPass, String langCode) {
        String msg = "Unexpected error: Unable to read the changelog path!";
        try {
            String changelogName = String.format("%s.%s.md", modPass.modData().id(), langCode);
            Path changelog = path(modPass).resolve(changelogName);
            if (Files.exists(changelog)) {
                return Optional.of(changelog);
            }
            changelogName = String.format("%s.%s.md", modPass.modData().id(), "en_us");
            changelog = path(modPass).resolve(changelogName);
            if (Files.exists(changelog)) {
                return Optional.of(changelog);
            }
            return Optional.empty();
        } catch (NoSuchElementException | URISyntaxException e) {
            SilkModPass.INSTANCE.logger().warn(msg);
            return Optional.empty();
        }
    }

    /**
     * 读取更新日志
     *
     * @param modPass  模组通
     * @param langCode 语言代码
     * @return 日志的字符串
     */
    static String read(ModPass modPass, String langCode) {
        try {
            Optional<Path> path = get(modPass, langCode);
            if (path.isEmpty()) {
                return "Changelog does not exist!";
            }
            StringBuilder changelog = new StringBuilder().append(Files.readString(path.get(), StandardCharsets.UTF_8));
            // 把不同的标题大小归一化
            for (int count = 0; count < 5; count++) {
                changelog = new StringBuilder(changelog.toString().replaceAll("## ", "# "));
            }
            // 修正换行符问题
            String[] str = changelog
                    .toString()
                    .replaceAll("\r", "\n")
                    .replaceAll("\n\n", "\n")
                    .split("\n");
            // 把 md 标题切换为 mc 粗体
            for (int count = 0; count < str.length; count++) {
                if (!str[count].contains("# ")) {
                    continue;
                }
                changelog.setLength(0);
                str[count] = changelog
                        .append(str[count].replaceFirst("# ", "§l"))
                        .append("§r")
                        .toString();
            }
            changelog.setLength(0);
            for (String s : str) {
                changelog.append(s).append("\n");
            }
            // 处理缩进与无序列表
            changelog = new StringBuilder(changelog
                    .toString()
                    .replaceAll("- ", "・")
                    .replaceAll("\\* ", "・")
                    .replaceAll("\\+ ", "・"));
            if (langCode.contains("zh")) {
                changelog = new StringBuilder(changelog
                        .toString()
                        .replaceAll("\t", "　")
                        .replaceAll(" ", "·"));
            } else {
                changelog = new StringBuilder(changelog.toString().replaceAll("\t", "  "));
            }
            return changelog.toString();
        } catch (IOException e) {
            return "changelog does not exist!";
        }
    }
}
