package com.xjbg.java.sdk.http.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2023-10-25 10:13
 */
@Getter
@Setter
public class HttpConnectionConfig {
    /**
     * 连接存活时间 单位：毫秒
     */
    private int keepAlive = 60_000;
    /**
     * 多久检查一次连接池失效的连接 单位：毫秒
     */
    private int validateAfterInactivity = 300_000;
    /**
     * 自定义请求头的user-agent属性
     */
    private String userAgent;
    /**
     * 获取连接超时 单位：毫秒
     */
    private int connectTimeout = 10000;
    /**
     * socket超时 单位：毫秒
     */
    private int socketTimeout = 10000;
    /**
     * 请求超时 单位：毫秒
     */
    private int requestTimeout = 10000;
    /**
     * 最大连接数
     */
    private int maxConnect = 60;
    /**
     * 每个路由最大连接数
     */
    private int maxConnectPerRoute = 30;
    /**
     * https证书
     */
    private String certFile;
    /**
     * 是否忽略https校验，无需证书也可以发起https请求，生产上不推荐开启，建议使用证书
     */
    private boolean ignoreHttps = false;
}
