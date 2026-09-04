# 注解解析（z-tools-annotation）

内部模块，不直接出现在右键菜单。把 PSI 上的类 / 方法 / 参数 / 字段收成定义对象，供接口文档、cURL、Copy Json、YApi 使用。

返回总览：[zTools](../README.md)

## 对使用者意味着什么

你在 Controller 上写的 Spring / OpenAPI / Swagger / 校验 / Feign 注解，以及 JavaDoc，会进入文档和上传结果。本模块 **只读源码写出的值**，不补注解 `default`，必填和示例由各功能模块自己推断。

## 解析范围

| 类别 | 识别的注解（含 javax / jakarta、spring 5/6 等别名） |
| --- | --- |
| Web | `@Controller` `@RestController` `@RequestMapping` `@GetMapping` `@PostMapping` `@PutMapping` `@DeleteMapping` `@PatchMapping` `@RequestParam` `@PathVariable` `@RequestHeader` `@RequestBody` `@RequestPart` |
| OpenAPI / Swagger | `@Operation` `@ApiOperation` `@Schema` `@ApiModel` `@ApiModelProperty` `@Parameter` `@ApiParam` `@Tag` `@Api` |
| 校验 | `@NotNull` `@NotBlank` `@NotEmpty` `@Size` `@Min` `@Max` `@Pattern` `@Valid` `@Validated` |
| Feign | `feign.RequestLine` |

匹配只比较 **全限定名**，不看源码里写的短名。

## 字段展开

`TypeParser` 展开对象字段时：

- 跳过 `static`（含 `serialVersionUID`）
- 处理泛型、继承、集合 / 数组
- 循环引用截断
- 枚举收集常量名，供文档示例和 Copy Json 使用

## 给功能模块的约定

功能模块只依赖本模块与 `z-tools-common`，不要互相依赖。需要「未写 required 算不算必填」这类产品规则时，在 apidoc / yapi 里做，不要改本模块的「源码有什么就解析什么」。
