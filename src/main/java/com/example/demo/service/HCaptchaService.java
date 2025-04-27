package com.example.demo.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Properties;

public class HCaptchaService {
    private static final String HCAPTCHA_VERIFY_URL = "https://hcaptcha.com/siteverify";
    private final String secretKey;
    private final HttpClient httpClient;

    public HCaptchaService() {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/application.properties"));
            this.secretKey = props.getProperty("hcaptcha.secret.key");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load hCaptcha secret key", e);
        }
        this.httpClient = HttpClients.createDefault();
    }

    public boolean verifyCaptcha(String response) {
        try {
            HttpPost request = new HttpPost(HCAPTCHA_VERIFY_URL);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setEntity(new org.apache.http.client.entity.UrlEncodedFormEntity(
                java.util.Arrays.asList(
                    new org.apache.http.message.BasicNameValuePair("secret", secretKey),
                    new org.apache.http.message.BasicNameValuePair("response", response)
                )
            ));

            HttpResponse httpResponse = httpClient.execute(request);
            String responseBody = EntityUtils.toString(httpResponse.getEntity());
            
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
            return (Boolean) jsonResponse.get("success");
        } catch (Exception e) {
            System.err.println("Error verifying hCaptcha: " + e.getMessage());
            return false;
        }
    }
} 