# 搜索并跳转 Restful 接口（z-tools-restful）

按 URL 搜索当前工程里的 Spring MVC / Feign 接口，选中后跳转到对应方法。类似 Go To Class，但是搜路径

## 入口

快捷键 **Ctrl + \\**（反斜杠）

也可在 Keymap 里搜索 `go-to-restful` 自行改键

![image-20260904215406254](../img/resuful.png)

## 使用步骤

1. 任意位置按下 `Ctrl + \`
2. 输入路径片段，如 `user`、`/api/users/{id}`
3. 可用过滤器按 **HTTP 方法**（GET / POST / …）缩小范围；可勾选仅当前模块
4. 回车跳转到方法源码

列表中的路径会拼上全局请求前缀，因此搜索时请带上 `context-path`（如果项目配了）

## 识别范围

- Spring：`@Controller` / `@RestController` 上的 `@RequestMapping`、`@GetMapping` 等
- Feign：`feign.RequestLine`。

## 全局请求前缀

从 `application.yaml` / `.yml` / `.properties` 读取：

1. `server.servlet.context-path`
2. 若未配置，再读 `spring.mvc.servlet.path`

例如 context-path 为 `/api`，方法 Mapping 为 `/users/{id}`，搜索列表中显示 `/api/users/{id}`

## 注意事项

1. 配置文件优先 `src/main/resources`；同名文件优先级：`application.yaml` > `application.yml` > `application.properties`
2. 修改配置后请保存文件；若列表仍是旧前缀，重新构建 / 重启工程后再搜
3. YAML 请规范缩进，否则可能解析不到前缀
4. 多模块同时存在 `application.yml` 时，插件取 resources 下找到的第一份，不一定是你当前运行的那个模块
