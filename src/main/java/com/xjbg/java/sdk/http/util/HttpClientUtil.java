package com.xjbg.java.sdk.http.util;

import com.xjbg.java.sdk.http.config.HttpConnectionConfig;
import com.xjbg.java.sdk.util.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author kesc
 * @since 2019/6/24
 */
@Slf4j
public final class HttpClientUtil {
    @SneakyThrows
    public static Object[] ignoreSslContext() {
        SSLContext sc = SSLContext.getInstance("TLS");
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        sc.init(null, new X509TrustManager[]{x509TrustManager}, new SecureRandom());
        return new Object[]{sc, x509TrustManager};
    }

    public static Object[] sslContext(String certFile) {
        if (StringUtil.isBlank(certFile)) {
            return null;
        }
        try {
            Collection<? extends Certificate> certificates = null;
            try (FileInputStream fis = new FileInputStream(certFile)) {
                certificates = CertificateFactory.getInstance("X.509").generateCertificates(fis);
            }

            if (certificates == null || certificates.isEmpty()) {
                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
            }

            // Any password will work.
            char[] password = "password".toCharArray();
            // Put the certificates a key store.
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // By convention, 'null' creates an empty key store.
            keyStore.load(null, password);

            int index = 0;
            for (Certificate certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate);
            }

            // Use it to build an X509 trust manager.
            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            final KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            return new Object[]{sslContext, trustManagers[0]};
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static HttpClient createHttpClient(HttpConnectionConfig connectionProperties) {
        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
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
            return connectionProperties.getKeepAlive();
        };

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(Math.max(connectionProperties.getMaxConnect(), 10));
        connectionManager.setDefaultMaxPerRoute(Math.max(connectionProperties.getMaxConnectPerRoute(), 5));
        connectionManager.setValidateAfterInactivity(connectionProperties.getValidateAfterInactivity());

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setUserAgent(connectionProperties.getUserAgent())
                .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(connectionProperties.getSocketTimeout())
                        .setConnectTimeout(connectionProperties.getConnectTimeout())
                        .setConnectionRequestTimeout(connectionProperties.getRequestTimeout()).build());

        if (connectionProperties.isIgnoreHttps()) {
            httpClientBuilder.setSSLContext((SSLContext) ignoreSslContext()[0]);
        } else {
            Object[] objects = sslContext(connectionProperties.getCertFile());
            if (objects != null) {
                httpClientBuilder.setSSLContext((SSLContext) objects[0]);
            }
        }
        return httpClientBuilder.build();
    }

    public static String buildQueryParam(Map<String, String> params) {
        if (params != null && params.size() > 0) {
            List<BasicNameValuePair> nameValuePairs = params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            return URLEncodedUtils.format(nameValuePairs, StandardCharsets.UTF_8);
        }
        return StringUtil.EMPTY;
    }

    public static String buildUrl(String host, String path, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(host);
        if (StringUtil.isNotBlank(path)) {
            if (!host.endsWith(StringUtil.VIRGULE) && !path.startsWith(StringUtil.VIRGULE)) {
                sb.append(StringUtil.VIRGULE);
            }
            sb.append(path);
        }
        String queryParam = buildQueryParam(params);
        if (StringUtil.isNotBlank(queryParam)) {
            if (sb.indexOf("?") > -1) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            sb.append(queryParam);
        }
        return sb.toString();
    }

}
