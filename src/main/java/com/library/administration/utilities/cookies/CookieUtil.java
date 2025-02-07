package com.library.administration.utilities.cookies;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CookieUtil {

    public Cookie createCookie(String name, String value, boolean isRefresh) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        if (isRefresh) {
            cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(7));
        } else {
            cookie.setMaxAge((int) TimeUnit.MINUTES.toSeconds(60));
        }

        return cookie;
    }
}
