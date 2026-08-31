由于 protocollib 停更故更新此插件

# ProtocolStringReplacer for Leaf 26.1.2

ProtocolStringReplacer 是一个在服务器发送数据包时替换文本与组件内容的 Minecraft 插件。

本仓库是面向 Leaf/Paper `26.1.2` 的维护版本。插件已内置自身需要的协议处理核心，运行 ProtocolStringReplacer 时不再需要额外安装 ProtocolLib。

## 主要改动

- 移除对外部 ProtocolLib 插件的运行时依赖。
- 内置 ProtocolStringReplacer 所需的数据包监听、注入、反射和包装器功能。
- 内置协议类会重定位到插件的私有命名空间，减少与其他插件的类冲突。
- 未内置 ProtocolLib 的插件主类、命令、更新器、Metrics、PacketLogging 和会话交互等外围功能。
- 新增 `v26_1_2` NMS 实现，并使用 Mojang production mappings 打包。
- 调整 Minecraft `26.1.2` 版本号的解析与选择逻辑。

## 支持环境

| 项目 | 当前目标 |
| --- | --- |
| 服务器 | Leaf/Paper `26.1.2` |
| Paper 开发包 | `26.1.2 build 74 stable` |
| Bukkit API | `26.1` |
| 插件版本 | `3.2.4` |
| 构建工具 | Gradle `9.6.1` |
| 构建 JDK | JDK `25` toolchain |
| 输出字节码 | Java `17` |

> 该分支专门针对 `26.1.2` 构建，未对其他 Minecraft 版本进行兼容性验证。

## 安装

1. 停止服务器。
2. 安装并启用 `NBTAPI`，这是当前版本的必需依赖。
3. 将构建好的 `ProtocolStringReplacer-bukkit-3.2.4-mojmap.jar` 放入服务器的 `plugins` 目录。
4. 启动服务器，让插件生成默认配置。
5. 编辑替换规则后，使用 `/psr reload` 重载插件配置。

ProtocolStringReplacer 本身不再需要 ProtocolLib。如果服务器上的其他插件仍依赖 ProtocolLib，可以继续保留它。

### 可选依赖

- `PlaceholderAPI 2.10.7+`：用于在替换内容中解析占位符。未安装时，占位符功能会被禁用。

## 命令

主命令为 `/protocolstringreplacer`，别名为 `/psr`。

| 命令 | 用途 | 权限 |
| --- | --- | --- |
| `/psr about` | 查看插件信息 | `protocolstringreplacer.command.about` |
| `/psr capture` | 捕获数据包内容 | `protocolstringreplacer.command.capture` |
| `/psr edit` | 编辑替换配置 | `protocolstringreplacer.command.edit` |
| `/psr parse` | 解析指定字符串 | `protocolstringreplacer.command.parse` |
| `/psr reload` | 重载配置 | `protocolstringreplacer.command.reload` |

## 构建

克隆仓库后，使用 JDK 25 执行 Gradle Wrapper。

### Windows

```powershell
.\gradlew.bat clean :bukkit:shadowJar
```

### Linux / macOS

```bash
./gradlew clean :bukkit:shadowJar
```

构建产物位于：

```text
bukkit/build/libs/ProtocolStringReplacer-bukkit-3.2.4-mojmap.jar
```

目标 Minecraft 版本与 Paper 构建号可在 `gradle.properties` 中查看。更改这些值并不代表自动兼容新版本，NMS 和数据包结构仍可能需要同步修改。

## 项目结构

```text
bukkit/                         ProtocolStringReplacer 插件主体
bukkit/nms/                     NMS 抽象与版本选择
bukkit/nms/v26_1_2/             Leaf/Paper 26.1.2 实现
embedded-protocol/              内置协议核心模块
embedded-protocol/src/main/     针对嵌入模式的定制引导代码
embedded-protocol/src/upstream/ 实际需要的 ProtocolLib 上游核心源码
```

## 验证状态

当前版本已完成：

- 针对 Leaf/Paper `26.1.2 build 74 stable` 的 Java 与 Kotlin 编译。
- `embedded-protocol` 模块级 clean build。
- `bukkit:shadowJar` 完整打包。
- 私有协议命名空间的静态依赖检查。
- 打包产物中 ProtocolLib 插件外壳与未重定位 `com.comphenix` 类的排除检查。

当前尚未在真实 Leaf `26.1.2` 服务器上完成启动、玩家进服与各类数据包替换的运行时验证。

## 上游与许可证

- ProtocolStringReplacer 原始项目由 Rothes 开发，本仓库在其源码基础上继续维护。
- 项目根目录的源码许可证见 [`LICENSE`](LICENSE)。
- 内置的 ProtocolLib 源码子集保留其上游许可证，见 [`embedded-protocol/PROTOCOLLIB_LICENSE.txt`](embedded-protocol/PROTOCOLLIB_LICENSE.txt)。

## 问题反馈

请在本仓库的 [Issues](https://github.com/palkane/ProtocolStringReplacer/issues) 中提交问题。反馈时请附上 Leaf/Paper 完整版本、Java 版本、插件列表和相关日志。
