# 插件配置（z-tools-configure）

项目级设置页与持久化。界面在 **Settings → z-tools**，数据写入当前工程 `.idea/zTools.xml`。各功能模块只读这里的配置，互不依赖。

返回总览：[zTools](../README.md)

## 打开方式

1. `Settings`（macOS 旧版为 `Preferences`）。
2. 左侧找到 **z-tools**。
3. 三个 Tab：**API & DB Document**、**Jasypt Crypto**、**YApi**。



## Tab：API & DB Document

给 [接口文档](../z-tools-apidoc/README.md) 和 [数据库文档](../z-tools-dbdoc/README.md) 共用。

| 项 | 说明 |
| --- | --- |
| Save Directory | 文档输出目录。空则使用 `{项目根}/target/_docs/` |
| Doc Type | `MarkDown` / `Html` / `Word` |
| Overwrite exists docs | 已有同名文件是否覆盖 |
| Exclude Fields | 接口文档排除字段，多个用 `;` 分隔，默认 `serialVersionUID` |



## Tab：Jasypt Crypto

给 [加解密](../z-tools-jasyptcrypto/README.md) 用。详见该模块 README。

要点：密码必填；多个密码用 `;`；算法下拉在后台从 JCE 加载并在 IDE 进程内缓存（与项目 JDK 无关）。`PBEWITHMD5ANDDES` 请配 `NoIvGenerator`。



## Tab：YApi

给 [上传 YApi](../z-tools-yapi/README.md) 用。填 Server URL 与 Token，点 **Resolve** 拉取项目 ID。

Resolve 会发网络请求并弹出进度框，属预期行为。



## 工程内配置文件（不是这个设置页）

部分功能还读项目资源文件：

| 文件 | 谁在用 |
| --- | --- |
| `application.yaml` / `.yml` / `.properties` | Restful 搜索、cURL、接口文档、YApi 的全局前缀；cURL 的 `server.port` |
| `zTools.yaml` / `.yml` / `.properties` | **仅数据库文档** 的 JDBC 数据源 |

优先级均为 yaml > yml > properties，且优先 `src/main/resources`。

Restful 弹窗里勾选的 HTTP 方法过滤也会记入 `zTools.xml`，没有单独界面。

## 注意事项

1. 配置是 **项目级** 的，换工程要分别设置。
2. `.idea/zTools.xml` 可能含 Jasypt 密码和 YApi Token，提交到 Git 前请确认。
3. 改 `application.yml` / `zTools.yaml` 后请保存，功能侧读的是磁盘内容。
