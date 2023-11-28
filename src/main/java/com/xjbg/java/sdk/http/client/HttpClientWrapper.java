package com.xjbg.java.sdk.http.client;

import com.xjbg.java.sdk.http.config.HttpConnectionConfig;
import com.xjbg.java.sdk.http.request.BaseHttpRequest;
import com.xjbg.java.sdk.http.util.HttpClientUtil;
import com.xjbg.java.sdk.util.CollectionUtil;
import com.xjbg.java.sdk.util.JsonUtil;
import com.xjbg.java.sdk.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author kesc
 * @since 2023-08-08 15:53
 */
@Getter
@Slf4j
@SuppressWarnings({"unchecked", "unused"})
public class HttpClientWrapper {
    private static final ResponseHandler<String> BASIC_RESPONSE_HANDLER = new BasicResponseHandler();
    private static final ResponseHandler<InputStream> STREAM_RESPONSE_HANDLER = new AbstractResponseHandler<InputStream>() {
        @Override
        public InputStream handleEntity(HttpEntity httpEntity) throws IOException {
            return httpEntity.getContent();
        }
    };
    private final HttpClient httpClient;

    public HttpClientWrapper() {
        this.httpClient = HttpClientUtil.createHttpClient(new HttpConnectionConfig());
    }

    public HttpClientWrapper(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private HttpResponse execute(HttpRequestBase requestBase) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("request url:{}, headers:{}", requestBase, Arrays.toString(requestBase.getAllHeaders()));
        }
        HttpResponse httpResponse = getHttpClient().execute(requestBase);
        if (httpResponse.getStatusLine().getStatusCode() >= 300) {
            String message = httpResponse.getEntity() != null ? EntityUtils.toString(httpResponse.getEntity()) : null;
            throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(),
                    StringUtil.isBlank(message) ? httpResponse.getStatusLine().getReasonPhrase() : message);
        }
        return httpResponse;
    }

    private <H> H execute(HttpRequestBase requestBase, ResponseHandler<H> responseHandler) throws IOException {
        HttpResponse httpResponse = execute(requestBase);
        return responseHandler.handleResponse(httpResponse);
    }

    private <T> T execute(HttpRequestBase requestBase, Class<T> resultClazz, Class<?>... parametricType) throws IOException {
        if (resultClazz.isAssignableFrom(InputStream.class)) {
            return (T) execute(requestBase, STREAM_RESPONSE_HANDLER);
        }
        String response = execute(requestBase, BASIC_RESPONSE_HANDLER);
        log.debug("response:{}", response);
        return CollectionUtil.isEmpty(parametricType) ? JsonUtil.toObject(response, resultClazz) : JsonUtil.toObject(response, resultClazz, parametricType);
    }

    public String get(String url) throws IOException {
        return get(BaseHttpRequest.builder().setHost(url).build());
    }

    public <B extends BaseHttpRequest> String get(B request) throws IOException {
        return get(request, String.class);
    }

    public <B extends BaseHttpRequest, R> R get(B request, Class<R> clazz, Class<?>... parametricType) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();

        HttpGet httpGet = new HttpGet(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpGet::addHeader);

        return execute(httpGet, clazz, parametricType);
    }

    public <B extends BaseHttpRequest> String post(B request) throws IOException {
        return post(request, String.class);
    }

    public <R, B extends BaseHttpRequest> R post(B request, Class<R> clazz, Class<?>... parametricType) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();
        Object body = request.getBody();

        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        HttpPost httpPost = new HttpPost(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpPost::addHeader);

        String jsonBody = JsonUtil.toJsonString(body);
        if (jsonBody != null) {
            httpPost.setEntity(new StringEntity(jsonBody));
        }

        return execute(httpPost, clazz, parametricType);
    }

    public <B extends BaseHttpRequest> String postForm(B request) throws IOException {
        return postForm(request, String.class);
    }

    public <R, B extends BaseHttpRequest> R postForm(B request, Class<R> clazz, Class<?>... parametricType) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();
        Map<String, String> body = Optional.ofNullable(JsonUtil.toMap(JsonUtil.toJsonString(request.getBody()), String.class, String.class)).orElse(new HashMap<>());

        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        HttpPost httpPost = new HttpPost(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpPost::addHeader);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        body.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, ContentType.APPLICATION_FORM_URLENCODED.getCharset()));

        return execute(httpPost, clazz, parametricType);
    }

    public <B extends BaseHttpRequest> String put(B request) throws IOException {
        return put(request, String.class);
    }

    public <R, B extends BaseHttpRequest> R put(B request, Class<R> clazz, Class<?>... parametricType) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();
        Object body = request.getBody();

        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        HttpPut httpPut = new HttpPut(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpPut::addHeader);

        String jsonBody = JsonUtil.toJsonString(body);
        if (jsonBody != null) {
            httpPut.setEntity(new StringEntity(jsonBody));
        }

        return execute(httpPut, clazz, parametricType);
    }

    public <B extends BaseHttpRequest> String patch(B request) throws IOException {
        return patch(request, String.class);
    }

    public <R, B extends BaseHttpRequest> R patch(B request, Class<R> clazz, Class<?>... parametricType) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();
        Object body = request.getBody();

        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        HttpPatch httpPatch = new HttpPatch(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpPatch::addHeader);

        String jsonBody = JsonUtil.toJsonString(body);
        if (jsonBody != null) {
            httpPatch.setEntity(new StringEntity(jsonBody));
        }

        return execute(httpPatch, clazz, parametricType);
    }

    public String delete(String url) throws IOException {
        return delete(BaseHttpRequest.builder().setHost(url).build());
    }

    public <B extends BaseHttpRequest> String delete(B request) throws IOException {
        return delete(request, String.class);
    }

    public <B extends BaseHttpRequest, R> R delete(B request, Class<R> clazz, Class<?>... parametricType) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();

        HttpDelete httpDelete = new HttpDelete(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpDelete::addHeader);

        return execute(httpDelete, clazz, parametricType);
    }

    public Map<String, String> options(String url) throws IOException {
        return options(BaseHttpRequest.builder().setHost(url).build());
    }

    public <B extends BaseHttpRequest> Map<String, String> options(B request) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();

        HttpOptions httpOptions = new HttpOptions(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpOptions::addHeader);

        HttpResponse httpResponse = execute(httpOptions);
        if (httpResponse.getEntity() != null) {
            EntityUtils.toString(httpResponse.getEntity());
        }
        return CollectionUtil.toMap(Arrays.asList(httpResponse.getAllHeaders()), Header::getName, Header::getValue);
    }

    public Map<String, String> head(String url) throws IOException {
        return head(BaseHttpRequest.builder().setHost(url).build());
    }

    public <B extends BaseHttpRequest> Map<String, String> head(B request) throws IOException {
        Map<String, String> headers = request.getHeaders();
        Map<String, String> params = request.getParams();

        HttpHead httpHead = new HttpHead(HttpClientUtil.buildUrl(request.getHost(), request.getPath(), params));
        headers.forEach(httpHead::addHeader);

        HttpResponse httpResponse = execute(httpHead);
        return CollectionUtil.toMap(Arrays.asList(httpResponse.getAllHeaders()), Header::getName, Header::getValue);
    }

}
