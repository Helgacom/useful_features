package com.helga.lib.normalizer;

import org.apache.commons.lang3.StringUtils;

public class NumberToString {
    final static String[] fractionRus =   {"", "десятых", "сотых", "тысячных", "десятитысячных", "стотысячных", "миллионных", "десятимиллионных", "стомиллионных", "миллиардных", "десятимиллиардных", "стомиллиардных"};
    final static String[] fractionFemaleRus =   {"", "десятая", "сотая", "тысячная", "десятитысячная", "стотысячная", "миллионная", "десятимиллионная", "стомиллионная", "миллиардная", "десятимиллиардная", "стомиллиардная"};

    private static String getFractionRus(String fraction) {
        int n = Integer.parseInt(StringUtils.right(fraction,2));
        int n2 = n % 100;
        int n1 = n % 10;
        int count = fraction.length();
        if (n1 == 1 && n2 != 11)  // 1, 21, 31, 41, 51,.... 91
            return fractionFemaleRus[count];
        else
            return fractionRus[count];
    }

    public static String digitRus(String number, String fraction, boolean percent) {
        Converter converter = new ConverterRus();
        if (fraction.replace("0", "").isEmpty())
            fraction = ""; // Если одни нули в дробной части - то это целое число
        boolean isFraction = fraction.isEmpty() == false;
        StringBuilder result = new StringBuilder();
        if (number.replace("-", "").length() <= 12) {
            long n = Long.parseLong(number);
            result.append(converter.numberToString(n, isFraction));
            if (isFraction) {
                result.append(((Math.abs(n) % 100) == 1) ? "целая" : "целых");
            }
        } else {
            // Произносим по две цифры
            if (number.startsWith("-")) {
                result.append("минус ");
                number = number.replace("-","");
            }
            String[] str = number.split("(?<=\\G.{2})");
            for (int i = 0; i < str.length; i++) {
                long n = Long.parseLong(str[i]);
                result.append(converter.numberToString(n, isFraction && i == str.length - 1));
                if (isFraction && i == str.length - 1) {
                    result.append((n == 1) ? " целая" : " целых");
                }
            }
        }
        // Дробная часть
        if (isFraction) {
            result.append(" ");
            if (fraction.length() <= 11) {
                long n = Long.parseLong(fraction);
                result.append(converter.numberToString(n, true));
                result.append(getFractionRus(fraction));
            } else {
                // Произносим по одной цифре
                result.append(" дробная часть ");
                for (char ch: fraction.toCharArray()) {
                    long n = Long.parseLong(ch+"");
                    result.append(converter.numberToString(n, false));
                }
                result.append(" конец дробной части");
            }
        }
        if (percent) {
            if (isFraction) {
                result.append(" процента");
            } else {
                int n = Integer.parseInt(StringUtils.right(number.replace("-",""),2));
                int n2 = n % 100;
                int n1 = n % 10;
                if (n1 == 1 && n2 != 11)
                    result.append("процент");
                else if ((n1 == 2 || n1 == 3 || n1 == 4) && (n2 != 12 && n2 != 13 && n2 != 14))
                    result.append("процента");
                else
                    result.append("процентов");
            }
        }
        return result.toString();
    }


    public static class Converter  {
        protected static final int MAX_EXTENT = 4;

        public String getZero() { return ""; }
        public String getMinus() { return ""; }
        public String getNumber(int number, int ext) { return ""; }
        public String getExtent(int number, int ext) { return ""; }

        public String numberToString(long number) {
            return numberToString(number, false);
        }

        public String numberToString(long number, boolean female) {
            StringBuilder result= new StringBuilder();

            if (number == 0)
                return getZero();

            if (number < 0) {
                result = new StringBuilder(getMinus() + " ");
                number = -number;
            }

            long div = 1;
            for (int ext = 0; ext < MAX_EXTENT; ext++)
                div *= 1000;

            for (int ext = MAX_EXTENT - 1; ext >= 0; ext--) {
                div /= 1000;
                long n = number / div;

                number %= div;
                if (n == 0)
                    continue;

                String str = getNumber((int)n, female && ext == 0 ? 1: ext);

                if (ext > 0) // тысячи/миллионы и т.д.
                    str += getExtent((int)n, ext);

                result.append(str);
            }


            return result.toString();
        }
    }



    public static class ConverterRus extends Converter
    {
        final String[][] extentRus =
                {
                        {"тысяча"   , "тысячи "   , "тысяч"      },  // 0
                        {"миллион"  , "миллиона"  , "миллионов"  },  // 1
                        {"миллиард" , "миллиарда" , "миллиардов" },  // 2
                };

        final String[] digitOneRus = 	{"", "один", "два",  "три", "четыре", "пять", "шесть", "семь", "восемь", "девять" };
        final String[] digitOneFemaleRus = 	{"", "одна", "две",  "три", "четыре", "пять", "шесть", "семь", "восемь", "девять" };
        final String[] digitTwoRus =   {"десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"};
        final String[] decimalRus =    {"", "десять", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"};
        final String[] hundredRus =    {"", "сто", "двести", "триста", "четыреста" ,"пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"};

        @Override
        public String getZero() {return "ноль ";}

        @Override
        public String getMinus() {return "минус";}

        @Override
        public String getNumber(int n, int ext) {
            String str = "";
            if (n >= 100) {// 100-900 : сотни
                str = str + hundredRus[n/100] + " ";
                n %= 100;
            }
            if (n >= 20) { // 20 - 90 : десятки
                str = str + decimalRus[n/10] + " ";
                n %= 10;
            }
            if (n >= 10)
                str = str + digitTwoRus[n-10] + " ";
            if (n >= 1 && n <= 9)
                str = str + ((ext == 1) ? digitOneFemaleRus[n] : digitOneRus[n]) + " "; // для тысяч - берем другой падеж

            return str;
        }

        @Override
        public String getExtent(int n, int ext) {
            int n2 = n % 100;
            int n1 = n % 10;
            String str = "";
            if (ext >= 1 && ext <= MAX_EXTENT) {
                if (n2 >= 10 && n2 < 19) {
                    str = str + extentRus[ext - 1][2] + " ";  // (много) тысяч, миллионов,..
                } else {
                    if (n1 == 1)
                        str = str + extentRus[ext - 1][0] + " ";  // (одна) тысяча,миллион,..
                    if (n1 >= 2 && n1 <= 4)
                        str = str + extentRus[ext - 1][1] + " ";  // (две-четыре) тысячи, миллиона,..
                    if (n1 > 4)
                        str = str + extentRus[ext - 1][2] + " ";  // (много) тысяч, миллионов,..
                }
            }
            return str;
        }


    }


    public static class ConverterEng extends Converter
    {
        final String[] extentEng =   {"thousand", "million", "billion"};
        final String[] digitOneEng = {"", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        final String[] digitTwoEng = {"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen,", "nineteen"};
        final String[] decimalEng =  {"", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};
        final String hundredEng = "hundred";

        @Override
        public String getZero() {return "null(zero)";}

        @Override
        public String getMinus() {return "minus";}

        @Override
        public String getNumber(int n, int ext) {
            String str = "";
            if (n >= 100) {// 100-900 : hundreds
                str = str + digitOneEng[n/100] + " " + hundredEng + " ";
                n %= 100;
            }
            if (n >= 20) { // 20 - 90
                str = str + decimalEng[n/10] + " ";
                n %= 10;
            }
            if (n >= 10)
                str = str + digitTwoEng[n-10] + " ";
            if (n >= 1 && n <= 9)
                str = str + digitOneEng[n] + " ";
            return str;
        }

        public String getExtent(int n, int ext)
        {
            String str = "";
            if (ext >= 1 && ext <= MAX_EXTENT) {
                str = str + extentEng[ext-1] + " ";
            }
            return str;
        }
    }

    public static void main(String[] args) {
        //long number = 999123675723l;
        long number = 322392112331L;
        //int number = -131;
        String str = digitRus("-4", "000", true);
        ConverterRus convertRus = new ConverterRus();
        ConverterEng convertEng = new ConverterEng();
        String strRus = convertRus.numberToString(number, true);
        String strEng = convertEng.numberToString(number, false);
        System.out.println(strRus);
        System.out.println(strEng);
    }

}
