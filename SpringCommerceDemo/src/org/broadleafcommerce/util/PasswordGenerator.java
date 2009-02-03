package org.broadleafcommerce.util;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PasswordGenerator {

    protected static final Log logger = LogFactory.getLog(PasswordGenerator.class);

    public static String generate(int requiredLength) {
        String[] good = new String[] { "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        while (builder.length() < requiredLength) {
            builder.append(good[random.nextInt(good.length)]);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        logger.info("Generated password = " + PasswordGenerator.generate(Integer.parseInt(args[0])));
    }
}
