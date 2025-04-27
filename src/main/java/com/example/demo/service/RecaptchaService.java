package com.example.demo.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.Properties;

public class RecaptchaService {
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private final String secretKey;
    private final HttpClient httpClient;

    public RecaptchaService() {
        // Load secret key from properties file or environment variable
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/application.properties"));
            this.secretKey = props.getProperty("recaptcha.secret.key");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load reCAPTCHA secret key", e);
        }
        this.httpClient = HttpClients.createDefault();
    }

    public boolean verifyRecaptcha(String recaptchaResponse) {
        try {
            HttpPost request = new HttpPost(RECAPTCHA_VERIFY_URL);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setEntity(new org.apache.http.client.entity.UrlEncodedFormEntity(
                java.util.Arrays.asList(
                    new org.apache.http.message.BasicNameValuePair("secret", secretKey),
                    new org.apache.http.message.BasicNameValuePair("response", recaptchaResponse)
                )
            ));

            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            
            // Parse JSON response using org.json.simple
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
            return (Boolean) jsonResponse.get("success");
        } catch (Exception e) {
            System.err.println("Error verifying reCAPTCHA: " + e.getMessage());
            return false;
        }
    }
} 