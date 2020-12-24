/*
 *
 */
package cc.admin.common.util;

import java.text.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NameConvert {
    public static String underlineToHump(String para) {
        String[] arrstring;
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : arrstring = para.split("_")) {
            if (!para.contains("_")) {
                stringBuilder.append(string);
                continue;
            }
            if (stringBuilder.length() == 0) {
                stringBuilder.append(string.toLowerCase());
                continue;
            }
            stringBuilder.append(string.substring(0, 1).toUpperCase());
            stringBuilder.append(string.substring(1).toLowerCase());
        }
        return stringBuilder.toString();
    }

    public static String humpToUnderline(String para) {
        StringBuilder stringBuilder = new StringBuilder(para);
        int n = 0;
        if (!para.contains("_")) {
            for (int i = 0; i < para.length(); ++i) {
				if (!Character.isUpperCase(para.charAt(i))) {

					continue;
				}
                stringBuilder.insert(i + n, "_");
                ++n;
            }
        }
        if (stringBuilder.toString().toLowerCase().startsWith("_")) {
            return stringBuilder.toString().toLowerCase().substring(1);
        }
        return stringBuilder.toString().toLowerCase();
    }

    public static String humpToShortbar(String para) {
        StringBuilder stringBuilder = new StringBuilder(para);
        int n = 0;
        if (!para.contains("-")) {
            for (int i = 0; i < para.length(); ++i) {
				if (!Character.isUpperCase(para.charAt(i))) {

					continue;
				}
                stringBuilder.insert(i + n, "-");
                ++n;
            }
        }
        if (stringBuilder.toString().toLowerCase().startsWith("-")) {
            return stringBuilder.toString().toLowerCase().substring(1);
        }
        return stringBuilder.toString().toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(humpToShortbar("ccDemo"));
    }

    public String number(Object obj) {
        Object object = obj = obj == null || obj.toString().length() == 0 ? Integer.valueOf(0) : obj;
        if (obj.toString().equalsIgnoreCase("NaN")) {
            return "NaN";
        }
        return new DecimalFormat("0.00").format(Double.parseDouble(obj.toString()));
    }

    public String number(Object obj, String pattern) {
        Object object = obj = obj == null || obj.toString().length() == 0 ? Integer.valueOf(0) : obj;
        if (obj.toString().equalsIgnoreCase("NaN")) {
            return "NaN";
        }
        return new DecimalFormat(pattern).format(Double.parseDouble(obj.toString()));
    }

    public String round(Object obj) {
        Object object = obj = obj == null || obj.toString().length() == 0 ? Integer.valueOf(0) : obj;
        if (obj.toString().equalsIgnoreCase("NaN")) {
            return "NaN";
        }
        return new DecimalFormat("0").format(Double.parseDouble(obj.toString()));
    }

    public String currency(Object obj) {
        obj = obj == null || obj.toString().length() == 0 ? Integer.valueOf(0) : obj;
        return NumberFormat.getCurrencyInstance(Locale.CHINA).format(obj);
    }

    public String timestampToString(Object obj, String pattern) {
        if (obj == null) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM月 -yy");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(obj.toString());
        }
        catch (ParseException parseException) {
            parseException.printStackTrace();
            return "error";
        }
        return simpleDateFormat2.format(date);
    }

    public String percent(Object obj) {
        Object object = obj = obj == null || obj.toString().length() == 0 ? Integer.valueOf(0) : obj;
        if (obj.toString().equalsIgnoreCase("NaN")) {
            return "";
        }
        return NumberFormat.getPercentInstance(Locale.CHINA).format(obj);
    }

    public String date(Object obj, String pattern) {
        if (obj == null) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(obj);
    }

    public String date(Object obj) {
        if (obj == null) {
            return "";
        }
        return DateFormat.getDateInstance(1, Locale.CHINA).format(obj);
    }

    public String time(Object obj) {
        if (obj == null) {
            return "";
        }
        return DateFormat.getTimeInstance(3, Locale.CHINA).format(obj);
    }

    public String datetime(Object obj) {
        if (obj == null) {
            return "";
        }
        return DateFormat.getDateTimeInstance(1, 3, Locale.CHINA).format(obj);
    }

    public String getInStrs(List<String> params) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String string : params) {
            stringBuffer.append("'" + string + "',");
        }
        String object = stringBuffer.toString();
        if (!"".equals(object) || ((String)object).endsWith(",")) {
            object = ((String)object).substring(0, ((String)object).length() - 1);
            return object;
        }
        return null;
    }
}

