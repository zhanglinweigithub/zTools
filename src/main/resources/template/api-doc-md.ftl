<#list apiList as api>

## ${api_index + 1}、${api.title!''}

<#if api.description??>
> ${api.description!''}
</#if>

### 接口信息

- 请求方式： `${api.baseInfo.requestType!''}`
- 接口路径： `${api.baseInfo.requestPath!''}`

### 请求参数

<#if api.requestInfo??>
<#if api.requestInfo.requestHeader?? && api.requestInfo.requestHeader.rowList?? && api.requestInfo.requestHeader.rowList?has_content>
#### 请求头参数（RequestHeader）

名称|类型|必填|说明|示例
:--:|:--:|:--:|:--:|:--:
<#list api.requestInfo.requestHeader.rowList as rowInfo>
${rowInfo.name!''}|${rowInfo.type!''}|${rowInfo.required?then('Y', 'N')}|${rowInfo.description!''}|${rowInfo.example!''}
</#list>
</#if>
<#if api.requestInfo.pathVariable?? && api.requestInfo.pathVariable.rowList?? && api.requestInfo.pathVariable.rowList?has_content>

#### 路径参数（PathVariable）

| 名称 | 类型 | 必填 | 说明 | 示例 |
| -- | -- | -- | -- | -- |
<#list api.requestInfo.pathVariable.rowList as rowInfo>
${rowInfo.name!''}|${rowInfo.type!''}|${rowInfo.required?then('Y', 'N')}|${rowInfo.description!''}|${rowInfo.example!''}
</#list>
</#if>
<#if api.requestInfo.requestParam?? && api.requestInfo.requestParam.rowList?? && api.requestInfo.requestParam.rowList?has_content>

#### 查询参数（RequestParam）

| 名称 | 类型 | 必填 | 说明 | 示例 |
| -- | -- | -- | -- | -- |
<#list api.requestInfo.requestParam.rowList as rowInfo>
${rowInfo.name!''}|${rowInfo.type!''}|${rowInfo.required?then('Y', 'N')}|${rowInfo.description!''}|${rowInfo.example!''}
</#list>
</#if>
<#if api.requestInfo.formParam?? && api.requestInfo.formParam.rowList?? && api.requestInfo.formParam.rowList?has_content>

#### 表单参数（FormParam）

| 名称 | 类型 | 必填 | 说明 | 示例 |
| -- | -- | -- | -- | -- |
<#list api.requestInfo.formParam.rowList as rowInfo>
${rowInfo.name!''}|${rowInfo.type!''}|${rowInfo.required?then('Y', 'N')}|${rowInfo.description!''}|${rowInfo.example!''}
</#list>
</#if>
<#if api.requestInfo.requestBody?? && api.requestInfo.requestBody.rowList?? && api.requestInfo.requestBody.rowList?has_content>

#### 请求体参数（RequestBody）

**描述**

| 名称 | 类型 | 必填 | 说明 | 示例 |
| -- | -- | -- | -- | -- |
<#list api.requestInfo.requestBody.rowList as rowInfo>
${rowInfo.name!''}|${rowInfo.type!''}|${rowInfo.required?then('Y', 'N')}|${rowInfo.description!''}|${rowInfo.example!''}
</#list>

**示例**

```json
${api.requestInfo.requestBodyJson!''}
```

</#if>
<#else>
无请求参数
</#if>
### 响应参数

<#if api.responseInfo??>
<#if api.responseInfo.responseBody?? && api.responseInfo.responseBody.rowList?? && api.responseInfo.responseBody.rowList?has_content>

#### 响应体（ResponseBody）

**描述**

| 名称 | 类型 | 必填 | 说明 | 示例 |
| -- | -- | -- | -- | -- |
<#list api.responseInfo.responseBody.rowList as rowInfo>
${rowInfo.name!''}|${rowInfo.type!''}|${rowInfo.required?then('Y', 'N')}|${rowInfo.description!''}|${rowInfo.example!''}
</#list>

**示例**

```json
${api.responseInfo.responseBodyJson!''}
```

</#if>
<#else>
无响应参数
</#if>
</#list>
