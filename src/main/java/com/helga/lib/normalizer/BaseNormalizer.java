package com.helga.lib.normalizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.helga.lib.normalizer.NumberToString.digitRus;

@Service
public class BaseNormalizer {

    private final PhoneConverter phoneConverter;

    @Autowired
    public BaseNormalizer(PhoneConverter phoneConverter) {
        this.phoneConverter = phoneConverter;
    }

    // метод может быть расширен другими видами нормализации (обработка дат, телефонов, времени, общепринятых сокращений и т.д.)
    public String normalizeText(String input) {

        // обработка тел номеров
        var convertedText = convertPhoneNumberToText(input);

        // обработка чисел
         convertedText = convertNumbersToWords(convertedText);

        return convertedText;
    }

    public String convertPhoneNumberToText(String text) {
        StringBuilder result = new StringBuilder();
        Pattern pattern = Pattern.compile("((\\()?(\\+7|8)?\\d{1,3}[\\s-]?\\(?\\d{1,4}?\\)?[\\s-]?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{1,9})");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String phone = matcher.group();
            String ph = convertNumbersToWords(phoneConverter.convertPhone(phone));
            matcher.appendReplacement(result, ph);
        }
        matcher.appendTail(result);
        return result.toString().trim();
    }

    // метод разбивает текст на массив элементов, если слово является числом, к нему применяется конвертация
    public String convertNumbersToWords(String text) {
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(convertNumberWord(word)).append(" ");
        }
        return result.toString().trim();
    }

    // вызов конвертации в зависимости от типа числа
    private String convertNumberWord(String word) {
        NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("ru"));
        try {
            Number number = format.parse(word);
            if (number instanceof Long) {
                return convertInt(Math.toIntExact((Long) number));
            } else if (number instanceof Double) {
                return convertDouble((Double) number);
            }
        } catch (ParseException e) {
            return word;
        }
        return word;
    }

    // преобразование целых чисел
    public String convertInt(int number) {
        var convertRus = new NumberToString.ConverterRus();
        return convertRus.numberToString(number, false);
    }

    // преобразование дробных чисел
    public String convertDouble(double number) {
        var fractionalPart = String.valueOf((int) Math.round((number - (int) number) * 100));
        var wholePart = String.valueOf((int) number);
        return digitRus(wholePart, fractionalPart, false);
    }
}

