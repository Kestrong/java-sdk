package com.xjbg.java.sdk.util;

import com.xjbg.java.sdk.enums.Encoding;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author kesc
 * @since 2019/6/24
 */
@Slf4j
public final class HttpClientUtil {
    private static final HttpClient CLIENT = create(100, 50);
    private static final ThreadLocal<Encoding> ENCODING = new ThreadLocal<>();
    private static final ResponseHandler<String> BASIC_RESPONSE_HANDLER = new BasicResponseHandler();
    private static final ResponseHandler<byte[]> BYTE_RESPONSE_HANDLER = new AbstractResponseHandler<byte[]>() {
        @Override
        public byte[] handleEntity(HttpEntity httpEntity) throws IOException {
            return EntityUtils.toByteArray(httpEntity);
        }
    };

    public static HttpClient create(int maxTotal, int maxPerRoute) {
        return create(maxTotal, maxPerRoute, 5000);
    }

    public static HttpClient create(int maxTotal, int maxPerRoute, int timeout) {
        ConnectionKeepAliveStrategy myStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 60 * 1000;
        };
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(Math.max(maxTotal, 10));
        connectionManager.setDefaultMaxPerRoute(Math.max(maxPerRoute, 5));
        connectionManager.setValidateAfterInactivity(120 * 1000);
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(myStrategy)
                .setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).setConnectTimeout(timeout).build())
                .build();
    }

    public static void setEncodingOnce(Encoding encoding) {
        ENCODING.set(encoding);
    }

    public static void removeEncoding() {
        ENCODING.remove();
    }

    public static String buildUri(String url, Map<String, String> params) {
        if (CollectionUtil.isEmpty(params)) {
            return url;
        }
        log.debug(url);
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setCharset(Charset.forName(ENCODING.get() == null ? Encoding.UTF_8.getEncoding() : ENCODING.get().getEncoding()));
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            String path = uriBuilder.build().toString();
            log.debug(path);
            return path;
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T execute(HttpRequestBase requestBase, Map<String, String> headers, ResponseHandler<T> handler) throws IOException {
        if (CollectionUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBase.addHeader(entry.getKey(), entry.getValue());
            }
        }
        T result = CLIENT.execute(requestBase, handler);
        log.debug(JsonUtil.toJsonString(result));
        return result;
    }

    public static String execute(HttpRequestBase requestBase, Map<String, String> headers) throws IOException {
        return execute(requestBase, headers, BASIC_RESPONSE_HANDLER);
    }

    public static String get(String url) throws IOException {
        return get(url, Collections.emptyMap(), Collections.emptyMap(), String.class);
    }

    public static String get(String url, Map<String, String> params) throws IOException {
        return get(url, Collections.emptyMap(), params, String.class);
    }

    public static <R> R get(String url, Class<R> clazz) throws IOException {
        return get(url, Collections.emptyMap(), Collections.emptyMap(), clazz);
    }

    public static <R> R get(String url, Map<String, String> headers, Map<String, String> params, Class<R> clazz) throws IOException {
        HttpGet httpGet = new HttpGet(buildUri(url, params));
        return JsonUtil.toObject(execute(httpGet, headers), clazz);
    }

    public static String post(String url, Map<String, String> params) throws IOException {
        return post(url, null, Collections.emptyMap(), params, String.class);
    }

    public static <REQ, RES> RES post(String url, REQ body, Class<RES> clazz) throws IOException {
        return post(url, body, Collections.emptyMap(), Collections.emptyMap(), clazz);
    }

    public static <REQ, RES> RES post(String url, REQ body, Map<String, String> headers, Map<String, String> params, Class<RES> clazz) throws IOException {
        HttpPost post = new HttpPost(buildUri(url, params));
        if (body != null) {
            post.setEntity(new StringEntity(JsonUtil.toJsonString(body), ENCODING.get() == null ? Encoding.UTF_8.getEncoding() : ENCODING.get().getEncoding()));
        }
        post.addHeader("Content-Type", "application/json");
        String result = execute(post, headers);
        return JsonUtil.toObject(result, clazz);
    }

    public static <RES> RES postForm(String url, Map<String, String> headers, Map<String, String> params, Class<RES> clazz) throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> paramList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(paramList, ENCODING.get() == null ? Encoding.UTF_8.getEncoding() : ENCODING.get().getEncoding()));
        }
        String result = execute(post, headers);
        return JsonUtil.toObject(result, clazz);
    }

    public static String put(String url, Map<String, String> params) throws IOException {
        return put(url, null, Collections.emptyMap(), params, String.class);
    }

    public static <REQ, RES> RES put(String url, REQ body, Class<RES> clazz) throws IOException {
        return put(url, body, Collections.emptyMap(), Collections.emptyMap(), clazz);
    }

    public static <REQ, RES> RES put(String url, REQ body, Map<String, String> headers, Map<String, String> params, Class<RES> clazz) throws IOException {
        HttpPut put = new HttpPut(buildUri(url, params));
        if (body != null) {
            put.setEntity(new StringEntity(JsonUtil.toJsonString(body), ENCODING.get() == null ? Encoding.UTF_8.getEncoding() : ENCODING.get().getEncoding()));
        }
        put.addHeader("Content-Type", "application/json");
        String result = execute(put, headers);
        return JsonUtil.toObject(result, clazz);
    }

    public static String delete(String url) throws IOException {
        return delete(url, Collections.emptyMap(), Collections.emptyMap(), String.class);
    }

    public static <RES> RES delete(String url, Class<RES> clazz) throws IOException {
        return delete(url, Collections.emptyMap(), Collections.emptyMap(), clazz);
    }

    public static <RES> RES delete(String url, Map<String, String> headers, Map<String, String> params, Class<RES> clazz) throws IOException {
        HttpDelete delete = new HttpDelete(buildUri(url, params));
        String result = execute(delete, headers);
        return JsonUtil.toObject(result, clazz);
    }

    public static byte[] getFile(String url) throws IOException {
        return getFile(url, Collections.emptyMap(), Collections.emptyMap());
    }

    public static byte[] getFile(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        HttpGet httpGet = new HttpGet(buildUri(url, params));
        return execute(httpGet, headers, BYTE_RESPONSE_HANDLER);
    }
}
