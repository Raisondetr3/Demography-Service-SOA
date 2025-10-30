package ru.itmo.client;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class UnsafeTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}
    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
}