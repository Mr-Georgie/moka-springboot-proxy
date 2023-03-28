package com.flw.moka.security;

public class SecurityConstants {
    public static final String SECRET_KEY = "bQeThWmZq4t7w!z$C&F)J@NcRfUjXn2r5u8x/A?D*G-KaPdSgVkYp3s6v9y$B&E)"; // Your
                                                                                                                // //
                                                                                                                // signature.
    public static final int TOKEN_EXPIRATION = 14400000; // 7200000 milliseconds = 7200 seconds = 2 hours.
    public static final String BEARER = "Bearer "; // Authorization : "Bearer " + Token
    public static final String AUTHORIZATION = "Authorization"; // "Authorization" : Bearer Token
    public static final String AUTH_PATH = "/authenticate"; // Public path that clients can use to register.
}
