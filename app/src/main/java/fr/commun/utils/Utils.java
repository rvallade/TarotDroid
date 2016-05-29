package fr.commun.utils;

import java.util.List;

public class Utils {
    public static String buildMessage(String message, String... strings) {
        for (int i = 0; i < strings.length; i++) {
            message = message.replace("{" + i + "}", strings[i]);
        }
        return message;
    }

    public static boolean isListEmpty(List list) {
        return (list == null || list.size() == 0);
    }
}
