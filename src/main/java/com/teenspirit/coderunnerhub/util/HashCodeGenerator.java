package com.teenspirit.coderunnerhub.util;

public class HashCodeGenerator {

    public static int getHashCode(String input) {
        int hash = 0;
        for (int i = 0; i < input.length(); i++) {
            hash += input.charAt(i);
        }
        return hash;
    }
}
