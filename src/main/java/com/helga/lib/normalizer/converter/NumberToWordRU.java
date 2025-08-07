package com.helga.lib.normalizer.converter;

import com.ibm.icu.text.RuleBasedNumberFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NumberToWordRU implements NumberToWord {

    private final RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"),
            RuleBasedNumberFormat.SPELLOUT);


    @Override
    public String convertToWord(String number, boolean percent) {
        String rsl = "";
        boolean isFraction = false;
        if (number.contains(",") || number.contains(".")) {
            number = number.replace(",", ".");
            double doubleValue = Double.parseDouble(number);
            rsl = nf.format(doubleValue);
            if (number.startsWith("0")) {
                rsl = "ноль целых " + rsl;
            }
            rsl = rsl.replace("целый", "целая");
            isFraction = true;
        } else {
            long longValue = Long.parseLong(number);
            rsl = nf.format(longValue);
        }
        if (percent) {
            if (isFraction) {
                rsl = rsl + " процента";
            } else {
                String[] nums = number.split("[.,]+");
                int n = Integer.parseInt(StringUtils.right(nums[0].replace("-", ""), 2));
                int n2 = n % 100;
                int n1 = n % 10;
                if (n1 == 1 && n2 != 11)
                    rsl = rsl + " процент";
                else if ((n1 == 2 || n1 == 3 || n1 == 4) && (n2 != 12 && n2 != 13 && n2 != 14))
                    rsl = rsl + " процента";
                else
                    rsl = rsl + " процентов";
            }
        }
        return rsl;
    }
}
