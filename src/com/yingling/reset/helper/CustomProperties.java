package com.yingling.reset.helper;

public class CustomProperties {
    public static void fix() {
        String key = "idea.ignore.disabled.plugins";
        System.clearProperty(key);
    }
}
