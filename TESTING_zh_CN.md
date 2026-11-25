# V1.0.7 配置与验证说明

这是基于 Mixin 版 V1.0.6 修改的版本，适用于 Minecraft 1.7.10 / Forge，游戏内 mod ID 仍为 `beamingdrops`。

## 当前验证状态

- 已收到 GTNH 2.9.1 的游戏内反馈：光柱渲染可用，位置正确。
- V1.0.7 与已实测的 `V1.0.6-renderfix.2` 使用相同的渲染代码，本次仅整理版本号和文档。
- 14 项自动化测试覆盖名称颜色解析、过滤规则、旧配置保留及光柱几何。
- 光影包、其他整合包及所有自定义渲染器的组合尚未逐一验证。

当前没有必须阻塞提交的补测项。以下项目可在之后使用时按需做冒烟检查：

1. **过滤实际生效**：颜色黑名单加入 `white`，确认白色光柱隐藏、彩色保留；再改为颜色白名单，只允许 `blue` / `light_purple`。用物品过滤验证 `minecraft:cobblestone` 和 `minecraft:wool:14`，确认只影响匹配的物品或变种。每次只改变一类过滤规则，改完重启，最后恢复自己的配置。
2. **不同图像设置与堆叠**：快速/高品质图像设置下，分别丢单个和一组物品，观察有无重复光柱、闪烁或绕中心偏移。
3. **常用环境**：如平时使用光影，可在实际光影配置中观察颜色和遮挡；掉落物较多时关注帧率。这些组合尚未确认，不影响当前已验证环境的提交。

## 安装与首次测试

1. 退出游戏，将旧的 Beaming Drops / Loot Beams Backport JAR 移出客户端 `mods`，放入 `beamingdrops-V1.0.7.jar`。不要同时保留两版；不要安装 `-dev` / `-sources` JAR。
2. 原有配置会继续使用；首次启动会自动补充新的颜色过滤配置。无需删除旧配置。
3. 配置文件为 `.minecraft/config/beamingdrops/BeamingDropMixinsEarly.cfg`，修改后需重启游戏。
4. 首次测试先用默认的空黑名单，分别丢下白色、蓝色、紫色、金色名称的物品，并保留一个之前有光柱的物品作对照。默认允许所有颜色，包括普通白色物品。
5. 需要 GTNH 兼容的 Mixin 加载器；GTNH 2.9.1 已带 UniMixins，沿用整合包提供的版本即可。

颜色取物品名称第一个可见、非空白字符的有效颜色；连续格式码按顺序处理，加粗、斜体等样式不会覆盖颜色。`§r` 重置为白色，尾部的重置码不会影响已识别的文字颜色。没有名称颜色码时沿用物品稀有度颜色。多色名称只使用上述一个代表色生成整条光柱。

## V1.0.7 包含的渲染修复

- 改为在 `RenderManager.func_147939_a` 成功返回时统一绘制光柱，覆盖三维方块、普通图标和自定义物品渲染路径。移除了旧版按图标/Forge/ItemPhysic 分别绘制的注入点，避免漏画或重复绘制。
- 绘制时使用该次实体渲染传入的插值坐标，物品渲染器的平移、缩放和旋转已恢复。光柱以实体 X/Z 中心为轴旋转，不再随 GT 图标的左下角坐标系偏移。
- 绘制后恢复光柱改动的 OpenGL 状态；旋转和浮动使用帧插值，关闭旋转后仍可单独浮动。
- 高度、直径现在直接以方块为单位，不再受到物品模型的 0.25/0.5 缩放影响。同一配置的光柱可能比上一版更高、更粗，可用 `beamHeight` / `beamDiameter` 调整。

建议先使用空黑名单，分别测试泥土、圆石、GT 焦炉、GT 粗矿石和粉碎矿石，以及上一版显示正常的匠魂工具。每类先丢一个，再丢一组，绕着物品观察 10 秒；确认所有物品都有光柱、中心没有随旋转横向绕圈、成组物品不会产生多条重叠光柱。分别用快速/高品质图像设置测试一次，以覆盖平面与立体图标。

若仍有问题，请提供运行该版本那次的 `logs/fml-client-latest.log`（或 `logs/latest.log`），以及实际使用的 `config/beamingdrops/BeamingDropMixinsEarly.cfg`；偏移问题请同时截取一张能看到物品与光柱位置的图片，并注明物品完整 ID、metadata、数量、图像设置及光影配置。

## 过滤配置

以下选项都位于现有的 `"beam settings" { ... }` 中。示例用于替换对应选项，请勿重复添加同名选项。

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

在仓库目录运行 `gradlew.bat build`。运行 Gradle 使用 JDK 17 或 21，构建脚本会按需选择编译工具链，输出仍面向 Java 8。成品在 `build/libs/`，安装普通 JAR；`-dev` / `-sources` JAR 用于开发。

为恢复这份旧源码的构建，构建插件从仓库已不再提供的 `1.0.27` 调整为 `1.0.30`，并移除了代码未引用、已无法下载的可选 NEI 开发依赖。游戏实例中已有的 NEI 无需调整。

构建输出在 `build/libs/`，自动化测试报告在 `build/reports/tests/test/index.html`。自动化测试不涵盖实际 OpenGL / 光影渲染，游戏内验证范围见本文开头。
