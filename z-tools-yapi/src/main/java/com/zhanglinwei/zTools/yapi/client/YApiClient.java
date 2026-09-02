package com.zhanglinwei.zTools.yapi.client;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.util.io.HttpRequests;
import com.zhanglinwei.zTools.common.constant.MediaType;
import com.zhanglinwei.zTools.common.constant.WebTypes;
import com.zhanglinwei.zTools.yapi.model.YApiInterfaceAddRequest;
import com.zhanglinwei.zTools.yapi.model.YApiInterfaceCat;
import com.zhanglinwei.zTools.yapi.model.YApiInterfaceCatAddRequest;
import com.zhanglinwei.zTools.yapi.model.YApiProject;
import com.zhanglinwei.zTools.yapi.model.YApiResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SLASH;

/**
 * YApi HTTP 客户端，封装项目查询、分类与接口保存。
 */
public class YApiClient {

    /** 字段名按下划线风格序列化，对齐 YApi 接口 */
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    /** 工具类，禁止实例化。 */
    private YApiClient() {}

    /**
     * 获取项目信息（通过 Token 解析项目 ID）。
     *
     * @param serverUrl YApi 服务器地址
     * @param token     项目 Token
     * @return YApiProject 项目信息
     * @throws IOException 网络错误、API 返回错误或 data 为空
     */
    public static YApiProject getProject(String serverUrl, String token) throws IOException {
        String url = buildUrl(serverUrl, "/api/project/get", token);
        String response = httpGet(url);
        Type type = new TypeToken<YApiResult<YApiProject>>() {}.getType();
        YApiResult<YApiProject> result = GSON.fromJson(response, type);
        checkResult(result);
        YApiProject project = result.getData();
        if (project == null) {
            throw new IOException("YApi 返回空的项目信息");
        }
        return project;
    }

    /**
     * 获取接口分类菜单。
     *
     * @param serverUrl YApi 服务器地址
     * @param projectId 项目 ID
     * @param token     项目 Token
     * @return 分类列表；data 为空时返回空列表
     * @throws IOException 网络错误或 API 返回错误
     */
    public static List<YApiInterfaceCat> getCatMenu(String serverUrl, Number projectId, String token) throws IOException {
        String url = buildUrl(serverUrl, "/api/interface/getCatMenu", token)
                + "&project_id=" + projectId;
        String response = httpGet(url);
        Type type = new TypeToken<YApiResult<List<YApiInterfaceCat>>>() {}.getType();
        YApiResult<List<YApiInterfaceCat>> result = GSON.fromJson(response, type);
        checkResult(result);
        return result.getData() != null ? result.getData() : Collections.emptyList();
    }

    /**
     * 添加接口分类。
     *
     * @param serverUrl YApi 服务器地址
     * @param request   分类请求
     * @param token     项目 Token
     * @return 创建的分类信息（包含 _id）
     * @throws IOException 网络错误、API 返回错误或 data 为空
     */
    public static YApiInterfaceCat addCat(String serverUrl, YApiInterfaceCatAddRequest request, String token) throws IOException {
        String url = buildUrl(serverUrl, "/api/interface/add_cat", token);
        String jsonBody = GSON.toJson(request);
        String response = httpPost(url, jsonBody);
        Type type = new TypeToken<YApiResult<YApiInterfaceCat>>() {}.getType();
        YApiResult<YApiInterfaceCat> result = GSON.fromJson(response, type);
        checkResult(result);
        YApiInterfaceCat created = result.getData();
        if (created == null || created.get_id() == null) {
            throw new IOException("YApi 创建分类失败");
        }
        return created;
    }

    /**
     * 保存接口（新增或更新）。同名同分类下自动覆盖，否则新增。
     *
     * @param serverUrl YApi 服务器地址
     * @param request   接口请求
     * @param token     项目 Token
     * @throws IOException 网络错误或 API 返回错误
     */
    public static void saveInterface(String serverUrl, YApiInterfaceAddRequest request, String token) throws IOException {
        String url = buildUrl(serverUrl, "/api/interface/save", token);
        String jsonBody = GSON.toJson(request);
        String response = httpPost(url, jsonBody);
        YApiResult<?> result = GSON.fromJson(response, YApiResult.class);
        checkResult(result);
    }

    /**
     * 构建 YApi API URL：去掉末尾斜杠后拼接 path，token 做 URL 编码。
     *
     * @param serverUrl YApi 服务器地址
     * @param path      API 路径，如 {@code /api/project/get}
     * @param token     项目 Token
     * @return 带 token 查询参数的完整 URL
     */
    private static String buildUrl(String serverUrl, String path, String token) {
        String base = serverUrl == null ? EMPTY : serverUrl.trim();
        while (base.endsWith(SLASH)) {
            base = base.substring(0, base.length() - 1);
        }
        return base + path + "?token=" + encodeToken(token);
    }

    /**
     * Token 按 UTF-8 做 URL 编码，避免特殊字符破坏查询串。
     *
     * @param token 原始 Token
     * @return 编码后的 Token
     */
    private static String encodeToken(String token) {
        String raw = token == null ? EMPTY : token;
        try {
            return URLEncoder.encode(raw, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
            return raw;
        }
    }

    /**
     * HTTP GET 请求。
     *
     * @param url 完整 URL
     * @return 响应正文
     * @throws IOException 网络错误
     */
    private static String httpGet(String url) throws IOException {
        return HttpRequests.request(url)
                .tuner(connection -> connection.setRequestProperty("Accept", "application/json"))
                .readString();
    }

    /**
     * HTTP POST 请求（JSON body）。
     *
     * @param url      完整 URL
     * @param jsonBody JSON 请求体
     * @return 响应正文
     * @throws IOException 网络错误
     */
    private static String httpPost(String url, String jsonBody) throws IOException {
        return HttpRequests.post(url, MediaType.APPLICATION_JSON_VALUE())
                .tuner(connection -> {
                    connection.setRequestProperty(WebTypes.CONTENT_TYPE, "application/json; charset=UTF-8");
                    connection.setDoOutput(true);
                })
                .connect(request -> {
                    request.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                    return request.readString();
                });
    }

    /**
     * 检查 YApi API 响应是否成功。
     *
     * @param result 反序列化后的通用响应
     * @throws IOException 空响应或业务失败
     */
    private static void checkResult(YApiResult<?> result) throws IOException {
        if (result == null) {
            throw new IOException("YApi 返回空响应");
        }
        if (!result.isSuccess()) {
            throw new IOException("YApi 错误: " + result.getErrorMsg());
        }
    }
}
