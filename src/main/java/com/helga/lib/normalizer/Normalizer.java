package com.helga.lib.normalizer;

import com.helga.lib.normalizer.converter.NumberToWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Normalizer {

    private final NumberToWord numberToWord;
    private final String[] numberOfZeros = new String[]{"один", "два", "три", "четыре", "пять", "шесть"};

    @Autowired
    public Normalizer(NumberToWord numberToWord) {
        this.numberToWord = numberToWord;
    }

    /**
     * Метод считает количество нулей
     * @param wordWithZeros слово следующего формата 000
     * @return 000 -> три нуля
     */
    private String zeroCounter(String wordWithZeros) {
        String zeros = "";
        boolean notZero = true;
        for (int i = 0; i < wordWithZeros.length(); i++) {
            if (wordWithZeros.charAt(i) != '0') {
                notZero = false;
            }
            if (notZero && wordWithZeros.charAt(i) == '0' && i > 0) {
                zeros = i < 6 ? numberOfZeros[i] : numberToWord.convertToWord(Integer.toString(i + 1), false);
                zeros = i + 1 > 4 ? zeros + " нулей " : zeros + " нуля ";
            } else if (notZero && wordWithZeros.charAt(i) == '0') {
                zeros = "ноль ";
            }
        }
        return zeros;
    }

    /**
     * Метод собирает номер телефона исходя из regex, вызывается в convertPhone()
     * @param limit   количество групп чисел составляющих номер телефона. 911 000 22 41 -> limit = 4
     * 8 911 000 22 41 -> limit = 5
     * @param matcher одна из групп чисел
     * @return телефонный номер разделенный пробелами.
     */
    private StringBuilder phoneLoopBuilder(int limit, Matcher matcher) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= limit; i++) {
            if (i == limit) {
                stringBuilder.append(matcher.group(i));
            } else {
                stringBuilder.append(matcher.group(i)).append(" ");
            }
        }
        return stringBuilder;
    }

    /**
     * Метод разбивает число на группы чисел, используя метод phoneLoopBuilder(),
     * если число не является номером телефона, то оставляет его неизменным для последующей конвертации
     * в прописное представление
     * пример: 89120022211 -> 8 912 002 22 11
     * @param text текст содержащий номер телефона
     * @return
     */
    private String convertPhone(String text) {
        String cleanedPhone = text.replaceAll("[^\\d\\s]", "");
        StringBuilder builder = new StringBuilder();
        Pattern elevenDigitPattern = Pattern.compile("(\\d{1})(\\d{3})(\\d{3})(\\d{2})(\\d{2})");
        Matcher elevenDigitMatcher = elevenDigitPattern.matcher(cleanedPhone);
        Pattern tenDigitPattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{2})(\\d{2})");
        Matcher tenDigitMatcher = tenDigitPattern.matcher(cleanedPhone);
        if (elevenDigitMatcher.matches()) {
            builder.append(phoneLoopBuilder(5, elevenDigitMatcher)).append(" ");
        } else if (tenDigitMatcher.matches()) {
            builder.append(phoneLoopBuilder(4, tenDigitMatcher)).append(" ");
        } else {
            builder.append(cleanedPhone).append(" ");
        }
        return builder.toString().trim();
    }

    /**
     * Осуществляет поиск номеров телефонов в тексте: если находим число из 10-11 цифр,
     * то разбиваем на подгруппы чисел с помощью вызова метода convertPhone() внутри цикла while
     * @param text содержащий номера телефонов
     * @return слово 89110002211 другое слово -> слово 8 911 000 22 11 другое слово
     */
    private String findPhone(String text) {
        text = text.replace("+", "плюс ");
        StringBuilder builder = new StringBuilder();
        String phone;
        Pattern phoneNumberpattern = Pattern.compile("((\\()?(\\+7|8)?\\d{1,3}[\\s-]?\\(?\\d{1,4}?\\)?[\\s-]?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{1,9})");
        Matcher matcher = phoneNumberpattern.matcher(text);
        while (matcher.find()) {
            phone = matcher.group();
            phone = convertPhone(phone);
            matcher.appendReplacement(builder, phone);
        }
        matcher.appendTail(builder);
        return builder.toString().trim();
    }

    /**
     * Шаблон (-?\d*,?\.?\d+%?) находит вещественные числа (целые, десятичные дроби) с точкой либо запятой.
     * Перед проверкой чисел в тексте проверяет не содержит ли строка номера телефонов.
     * @param text текст для перевода чисел в строку
     * @return возвращает строку содержащую числа прописью включая процент если присутствовал
     */
    public String convertNumbers(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        text = findPhone(text);
        Pattern pattern = Pattern.compile("-?\\d*,?\\.?\\d+%?,?");
        boolean percent = false;
        for (String word : text.split(" ")) {
            Matcher matcher = pattern.matcher(word);
            String numbers;
            String stringDigit;
            if (matcher.matches()) {
                numbers = matcher.group();
                if (numbers.endsWith("%")) {
                    numbers = numbers.substring(0, numbers.length() - 1);
                    percent = true;
                }
                if (numbers.startsWith("0")) {
                    String zeros = zeroCounter(numbers);
                    stringBuilder.append(zeros);
                }
                stringDigit = numbers.matches("(0)\\1+") ? "" : numberToWord.convertToWord(numbers, percent);
                word = word.replace(matcher.group(), stringDigit);
                stringBuilder.append(word).append(" ");
                percent = false;
            } else {
                stringBuilder.append(word).append(" ");
            }
        }
        return stringBuilder.toString().trim();
    }

    public boolean isRussianText(String text) {
        String russianPattern = "^[А-Яа-яЁё0-9\\s.,!?;:()\"'-]*$";
        return Pattern.matches(russianPattern, text);
    }
}
