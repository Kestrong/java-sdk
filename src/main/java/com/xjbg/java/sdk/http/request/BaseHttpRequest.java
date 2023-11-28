package com.xjbg.java.sdk.http.request;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kesc
 * @since 2023-11-07 14:07
 */
@Getter
@Setter
public class BaseHttpRequest {
    /**
     * 域名
     */
    private String host;
    /**
     * 接口路径
     */
    private String path;
    /**
     * 附加的请求头信息
     */
    private final Map<String, String> headers = new HashMap<>();
    /**
     * 附加的url参数
     */
    private final Map<String, String> params = new HashMap<>();
    /**
     * post请求附加的请求体
     */
    private Object body;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BaseHttpRequest request = new BaseHttpRequest();

        public Builder setHost(String host) {
            request.setHost(host);
            return this;
        }

        public Builder setPath(String path) {
            request.setPath(path);
            return this;
        }

        public Builder setBody(Object body) {
            request.setBody(body);
            return this;
        }

        public Builder addParam(String key, String value) {
            request.getParams().put(key, value);
            return this;
        }

        public Builder addParams(Map<String, String> params) {
            request.getParams().putAll(params);
            return this;
        }

        public Builder addHeader(String key, String value) {
            request.getHeaders().put(key, value);
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            request.getHeaders().putAll(headers);
            return this;
        }

        public BaseHttpRequest build() {
            return request;
        }
    }

}
