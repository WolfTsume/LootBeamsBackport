# Loot Beams Backport

本项目使用AI编码

这是基于 Mixin 版 V1.0.6 修改的版本，适用于 Minecraft 1.7.10 / Forge，目前仅测试过GTNH整合包2.9.1的兼容性，其他整合包不保证可用。

## 安装与首次测试

Jar文件放入游戏实例的mods文件夹，移除旧版本。需要兼容的 Mixin 加载器，GTNH 2.9.1 自带的 UniMixins 即可。

## 目前改进的内容

- 改为在 `RenderManager.func_147939_a` 成功返回时统一绘制光柱，覆盖三维方块、普通图标和自定义物品渲染路径。移除了旧版按图标/Forge/ItemPhysic 分别绘制的注入点，避免漏画或重复绘制。
- 绘制时使用该次实体渲染传入的插值坐标，物品渲染器的平移、缩放和旋转已恢复。光柱以实体 X/Z 中心为轴旋转，不再随 GT 图标的左下角坐标系偏移。
- 绘制后恢复光柱改动的 OpenGL 状态；旋转和浮动使用帧插值，关闭旋转后仍可单独浮动。
- 高度、直径现在直接以方块为单位，不再受到物品模型的 0.25/0.5 缩放影响。可用 `beamHeight` / `beamDiameter` 调整。

## 过滤配置

配置文件为 .minecraft/config/beamingdrops/BeamingDropMixinsEarly.cfg。以下示例用于替换 "beam settings" 内的对应选项，修改后重启游戏。

| 配置项 | 默认值 | 含义 |
| --- | --- | --- |
| `S:itemBlackList` | 空列表 | 按物品注册 ID / metadata 或矿辞匹配 |
| `B:isWhitelist` | `false` | `false` 为物品黑名单，`true` 为物品白名单 |
| `S:colorBlackList` | 空列表 | 按解析得到的光柱颜色匹配 |
| `B:isColorWhitelist` | `false` | `false` 为颜色黑名单，`true` 为颜色白名单 |

物品过滤与颜色过滤需要**同时放行**。空黑名单全部放行，空白名单全部拦截。颜色过滤按解析结果匹配，不直接按稀有度枚举匹配。

### 示例一：只屏蔽白色物品

```text
B:isWhitelist=false
S:itemBlackList <
 >
B:isColorWhitelist=false
S:colorBlackList <
    white
 >
```

### 示例二：只允许蓝色和紫色名称的物品

```text
B:isWhitelist=false
S:itemBlackList <
 >
B:isColorWhitelist=true
S:colorBlackList <
    blue
    light_purple
 >
```

深紫色对应 `dark_purple`，常见稀有度的亮紫色对应 `light_purple`。金色为 `gold`（`#FFAA00`），黄色为 `yellow`（`#FFFF55`）。

支持的颜色名：`black`, `dark_blue`, `dark_green`, `dark_aqua`, `dark_red`, `dark_purple`, `gold`, `gray`, `dark_gray`, `blue`, `green`, `aqua`, `red`, `light_purple`, `yellow`, `white`。保留上游扩展色 `minecoin_gold`。也可写单个颜色码 `0`–`f`，或者 RGB 值，如 `#5555FF` / `0x5555FF`。不识别的颜色条目不匹配任何颜色。

### 示例三：屏蔽圆石及指定变种

```text
B:isWhitelist=false
S:itemBlackList <
    minecraft:cobblestone
    minecraft:wool:14
 >
B:isColorWhitelist=false
S:colorBlackList <
 >
```

- `modid:item`：匹配这个注册 ID 的**所有 metadata**。
- `modid:item:*`：同上。
- `modid:item:123`：仅匹配 metadata 为 `123` 的变种。
- `ore:oreCoal`：匹配矿辞 `oreCoal`，具体矿辞以游戏中的实际注册为准。
- GTNH 常用 metadata 区分大量变种；过滤某一种 GT 物品时应写完整的 `modid:item:meta`，避免把整个注册 ID 的变种全部过滤掉。ID 大小写需与注册名一致。

升级注意：V1.0.6 的裸 ID 原先仅匹配 metadata `0`，本版改为匹配所有 metadata。如要保留旧规则的范围，请将该条目显式改成 `modid:item:0`。不需要删除配置文件。

## 构建

在仓库目录运行 `gradlew.bat build`。运行 Gradle 使用 JDK 17 或 21
