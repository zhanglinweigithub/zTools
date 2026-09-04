# Jasypt 加解密（z-tools-jasyptcrypto）

在编辑器里用 Jasypt PBE 加密 / 解密文本，默认包裹为 Spring 常见的 `ENC(...)`。适合 `application.yml` 里的密码、密钥等敏感项

## 入口

编辑器右键 → **Jasypt Crypto**

| 菜单 | 作用 |
| --- | --- |
| **Encrypt** | 把选中明文加密，写回为 `ENC(密文)` |
| **Decrypt** | 把选中的 `ENC(...)` 或裸密文解密，写回编辑器 |
| **Decrypt To Clipboard** | 解密选区，复制到剪贴板，不改文件 |
| **Decrypt File** | 扫描整个文件，把所有 `ENC(...)` 还原为明文 |

![image-20260904215547806](../img/jasyptcrypto.png)

## 使用前配置

**Settings → z-tools → Jasypt Crypto**

至少填一个 **Password**。多个密码用 `;` 分隔：加密时弹窗选一个；解密时按顺序尝试直到成功

其它项需与运行时 Jasypt（如 Spring `jasypt.encryptor.*`）保持一致：

| 项 | 含义 | 默认 |
| --- | --- | --- |
| Algorithm | PBE 算法 | `PBEWITHMD5ANDDES` |
| Iterations | 密钥派生迭代次数 | `1000` |
| Output Type | `base64` / `hexadecimal` | `base64` |
| Enc Wrapper | 密文包裹，`%s` 为密文 | `ENC(%s)` |
| Salt Generator | `ZeroSaltGenerator` 密文可复现；`RandomSaltGenerator` 每次不同 | Zero |
| IV Generator | `PBEWITHMD5ANDDES` 用 `NoIvGenerator`；需要 IV 的算法用 `RandomIvGenerator` | NoIv |

算法列表在后台加载，打开设置页时不应卡住界面。配置写在项目 `.idea/zTools.xml`

未配密码时操作会提示：`Password is required. Please configure it in Settings > z-tools.`

## 示例

`application.yml`：

```yaml
spring:
  datasource:
    password: 123456
```

1. 选中 `123456`
2. 右键 **Jasypt Crypto → Encrypt**
3. 变成类似：

```yaml
spring:
  datasource:
    password: ENC(xK8a...)
```

选中整段 `ENC(xK8a...)`，**Decrypt** 会写回 `123456`；**Decrypt To Clipboard** 则只把明文放到剪贴板

整份配置要一次性解开时，打开该文件（不必选中），**Decrypt File**：所有能解开的 `ENC(...)` 变成明文，解不开的保持原样

## 多密码

设置：`devSecret;prodSecret`

- 加密：弹出选择框，选本次使用的密码。
- 解密 / Decrypt File：先试 `devSecret`，失败再试 `prodSecret`

## 注意事项

1. Encrypt / Decrypt / Decrypt To Clipboard 需要先选中文本；Decrypt File 对当前打开的整个文档生效
2. 算法、盐、IV、迭代次数、输出编码必须与解密端一致，否则会 `Decryption failure.`
3. `PBEWITHMD5ANDDES` 不要配随机 IV
4. 不要把真实生产密码写进 README、截图或提交到 Git；`.idea/zTools.xml` 含密码时请自行忽略或勿提交
