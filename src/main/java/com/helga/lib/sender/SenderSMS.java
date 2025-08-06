package com.helga.lib.sender;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@AllArgsConstructor
public class SenderSMS {

//    private static final String API_URL = "https://api.devino.ru/send/";
//    private static final String API_KEY = "API_ключ";

    private static final String API_URL_TEST = "https://textbelt.com/text";

    private static final Logger log = LoggerFactory.getLogger(SenderSMS.class);

    public void sendSmsTest(String message) throws Exception {
        String phoneNumber = "89873081860";
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
