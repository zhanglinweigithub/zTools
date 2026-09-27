# 填充转换方法体（z-tools-convert）

根据方法的返回类型和唯一入参，自动生成 JavaBean 转换方法体。

只依赖 `z-tools-common`

## 入口

光标放在转换方法上（签名或方法体内）：

- **Alt + Enter** → **Convert a to b**（setter）
- **Alt + Enter** → **Convert a to b (Builder)**（`builder()` 链式）
- 编辑器右键 → **Convert a to b (Builder)**（仅 Builder；光标不在可转换方法上时不显示）

## 使用步骤

1. 写好方法签名，方法体可为空，例如 `UserDO toDO(UserDTO userDTO)`
2. 源类型、目标类型需有对应 getter / setter（含 Lombok 生成的访问器、父类字段）
3. Builder 风格还需要目标类型有静态 `builder()`（Lombok `@Builder` 或本插件 Generate → Builder）
4. 光标放在该方法上，选对应菜单；已有方法体会被替换

## 示例：setter

生成前：

```java
public UserDO toDO(UserDTO userDTO) {

}
```

生成后：

```java
public UserDO toDO(UserDTO userDTO) {
    if (userDTO == null) {
        return null;
    }

    UserDO userDO = new UserDO();
    userDO.setName(userDTO.getName());
    userDO.setChildren(userDTO.getChildren());

    // unmatched
    userDO.setRemark();

    return userDO;
}
```

## 示例：Builder

```java
public UserDO toDO(UserDTO userDTO) {
    if (userDTO == null) {
        return null;
    }

    return UserDO.builder()
            .name(userDTO.getName())
            .children(userDTO.getChildren())
            // unmatched
            .remark()
            .build();
}
```

## 生成规则

1. 入参为 `null` 则返回 `null`
2. setter 风格：`new` 目标类型（需无参构造）
3. Builder 风格：`Dest.builder()...build()`，链式方法名由目标 setter 转成属性名（`setName` → `name`）
4. 按目标类（含父类）的 setter 找源类同名 getter，类型可赋值才拷贝
5. 匹配到的字段直接赋值，不逐字段判空
6. 目标有、源没有（或类型不兼容）的字段：setter 生成 `userDO.setRemark();`，Builder 生成 `.remark()`
7. 不递归转换嵌套对象，集合按引用拷贝

## 何时不出现

- 不是具体方法（接口/抽象方法、构造器）
- 入参不是恰好 1 个
- 返回类型或入参是 void、基本类型、集合、Map、数组、接口、枚举、record、`Object`，或 JDK 类型（`java.` / `javax.` / `jakarta.`）
