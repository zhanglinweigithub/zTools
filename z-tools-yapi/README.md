# 上传到 YApi（z-tools-yapi）

把 Spring Mapping 或 Feign `@RequestLine` 接口上传到 YApi。方法上触发只传当前接口；类上触发上传该类全部可识别接口

## 入口

编辑器右键 → **Upload YApi**

仅当光标在 **方法** 或 **类** 上时菜单可用



## 使用步骤

1. 先配置 YApi（见下方）。未配置时第一次上传会弹出连接对话框
2. 光标放在接口方法或 Controller / Feign 接口类上
3. 右键 **Upload YApi**
4. 后台任务：拉取分类 → 没有则创建 → 逐个保存接口
5. 全部成功提示 `Uploaded N interfaces to YApi`

分类名优先级：`@Schema(title)` → `@Tag(name)` → `@Api` → `@ApiModel` → 类 JavaDoc → `未命名分类`。已存在同名分类则复用，不会重复创建

路径会拼上 `application.*` 里的全局请求前缀（与 Restful 搜索相同）



## 配置

**Settings → z-tools → YApi**

| 项 | 说明 |
| --- | --- |
| YApi Server URL | 服务根地址，如 `http://yapi.example.com`，不要带具体接口路径 |
| Project Token | YApi 项目 Token |
| Project ID | 只读。填好 URL 和 Token 后点 **Resolve**，从 `/api/project/get` 解析 `_id` |

三项都有才能上传。配置保存在 `.idea/zTools.xml`

第一次右键上传若未配置，会弹出 **Configure YApi Connection**，保存成功后继续上传



## 会上传什么

与接口文档类似的字段解析：Path / Query / Header / JSON Body / 表单文件
说明与必填优先级：OpenAPI → Swagger → Spring → 校验 → JavaDoc

支持：

- Spring Web Mapping 方法
- Feign `feign.RequestLine`

不支持的方法会提示 `Only web or Feign methods are supported!`；类里一个都没有则 `The Web method was not found in the class!`

## 示例

```java
@Tag(name = "用户")
@RestController
@RequestMapping("/users")
public class UserController {

    @Operation(summary = "创建用户")
    @PostMapping
    public User create(@RequestBody User user) {
        return user;
    }
}
```

上传后 YApi 分类为「用户」，接口路径为 `{context-path}/users`，Body 为 User 的 JSON Schema 风格字段

## 注意事项

1. Project ID 必须是数字；解析失败请检查 Token 是否属于目标项目
2. 上传过程可取消；单个接口失败会单独提示，其它接口继续
3. 需要本机能访问 YApi 地址
4. 改完 context-path 请保存 `application.yml` 后再上传，否则路径可能缺前缀
