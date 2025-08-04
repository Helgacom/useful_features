package com.helga.lib.sender;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@Service
@AllArgsConstructor
public class SenderSMS {

//    private static final String API_URL = "https://api.devino.ru/send/";
//    private static final String API_KEY = "API_ключ";

    private static final String API_URL_TEST = "https://textbelt.com/text";

    @Value("${phone}")
    String phoneNumber;

    public void sendSmsTest(String phoneNumber, String message) throws Exception {
        var urlParameters = "phone=" + URLEncoder.encode(phoneNumber, "UTF-8")
                + "&message=" + URLEncoder.encode(message, "UTF-8")
                + "&key=textbelt";

        URL url = new URL(API_URL_TEST);
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (var os = connection.getOutputStream()) {
            byte[] input = urlParameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        log.info("Response Code: {}", responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            log.info("SMS отправлено успешно!");
        } else {
            log.error("Ошибка при отправке SMS. Код ответа: {}", responseCode);
        }
    }
}
