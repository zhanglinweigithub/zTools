# 公共能力（z-tools-common）

内部模块。通知、剪贴板、文档落盘、Spring/`zTools` 配置读取、类型判断、JSON 美化、HTTP 方法与文档类型枚举等。

返回总览：[zTools](../README.md)

## 和用户相关的行为

- 接口文档、库表文档默认目录：`{项目根}/target/_docs/`（见 `DocOutput`）。
- 全局请求前缀、`server.port`、数据源 key 的读取顺序见 [配置模块](../z-tools-configure/README.md)。
- 示例 JSON 里普通类型的占位值（`stringValue`、`0`、日期格式串等）定义在 `NormalType`。

## 配置文件查找

`ProjectConfigs`：

1. 文件名：`*.yaml` → `*.yml` → `*.properties`
2. 路径：优先 `src/main/resources`
3. YAML 嵌套 key 会拍成 kebab-case 点分路径，以便对齐 `server.servlet.context-path` 这类 Spring key

功能模块不要直接去扫配置文件，统一走这里。
