<#list apiList as api>

    ## ${index}、${api.title}

    > ${api.description}

    ### 接口信息

    - 请求方式： `${api.baseInfo.requestType}`
    - 接口路径： `${api.baseInfo.requestPath}`

    ### 请求参数

    <#if api.requestInfo != null>

        <#if api.requestInfo.requestHeader != null && api.requestInfo.requestHeader.rowList?has_content>

            #### 请求头参数（RequestHeader）

            名称|类型|必填|说明|示例
            :--:|:--:|:--:|:--:|:--:

            <#list api.requestInfo.requestHeader.rowList as rowInfo>

                ${rowInfo.name}|${rowInfo.type}|${rowInfo.required?then(Y, N)}|${rowInfo.description}|${rowInfo.example}
            </#list>

        </#if>

        <#if api.requestInfo.pathVariable != null && api.requestInfo.pathVariable.rowList?has_content>

            #### 路径参数（PathVariable）

            | 名称 | 类型 | 必填 | 说明 | 示例 |
            | :--: | :--: | :--: | :--: | :--: |

            <#list api.requestInfo.pathVariable.rowList as rowInfo>

                ${rowInfo.name}|${rowInfo.type}|${rowInfo.required?then(Y, N)}|${rowInfo.description}|${rowInfo.example}
            </#list>

        </#if>



        <#if api.requestInfo.requestParam != null && api.requestInfo.requestParam.rowList?has_content>

            #### 查询参数（RequestParam）

            | 名称 | 类型 | 必填 | 说明 | 示例 |
            | :--: | :--: | :--: | :--: | :--: |

            <#list api.requestInfo.requestParam.rowList as rowInfo>

                ${rowInfo.name}|${rowInfo.type}|${rowInfo.required?then(Y, N)}|${rowInfo.description}|${rowInfo.example}
            </#list>

        </#if>



        <#if api.requestInfo.formParam != null && api.requestInfo.formParam.rowList?has_content>

            #### 表单参数（FormParam）

            | 名称 | 类型 | 必填 | 说明 | 示例 |
            | :--: | :--: | :--: | :--: | :--: |

            <#list api.requestInfo.formParam.rowList as rowInfo>

                ${rowInfo.name}|${rowInfo.type}|${rowInfo.required?then(Y, N)}|${rowInfo.description}|${rowInfo.example}
            </#list>

        </#if>



        <#if api.requestInfo.requestBody != null && api.requestInfo.requestBody.rowList?has_content>

            #### 请求体参数（RequestBody）

            **描述**

            | 名称 | 类型 | 必填 | 说明 | 示例 |
            | :--: | :--: | :--: | :--: | :--: |

            <#list api.requestInfo.requestBody.rowList as rowInfo>

                ${rowInfo.name}|${rowInfo.type}|${rowInfo.required?then(Y, N)}|${rowInfo.description}|${rowInfo.example}

            </#list>

            **示例**

            ~~~json
            ${api.requestInfo.requestBodyJson}
            ~~~

        </#if>

    <#else>

        无请求参数

    </#if>

    ### 响应参数

    <#if api.responseInfo != null>

        <#if api.responseInfo.responseBody != null && api.responseInfo.responseBody.rowList?has_content>

            #### 响应体（ResponseBody）

            **描述**

            | 名称 | 类型 | 必填 | 说明 | 示例 |
            | :--: | :--: | :--: | :--: | :--: |

            <#list api.responseInfo.responseBody.rowList as rowInfo>

                ${rowInfo.name}|${rowInfo.type}|${rowInfo.required?then(Y, N)}|${rowInfo.description}|${rowInfo.example}

            </#list>

            **示例**

            ~~~json
            ${api.responseInfo.requestBodyJson}
            ~~~

        </#if>

    <#else>

        无响应参数

    </#if>

</#list>
