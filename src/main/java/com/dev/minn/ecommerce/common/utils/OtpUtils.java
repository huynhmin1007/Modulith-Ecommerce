package com.dev.minn.ecommerce.common.utils;

import java.security.SecureRandom;

public class OtpUtils {

    public static String generateSecureOtp() {
        SecureRandom secureRandom = new SecureRandom();
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }
}
