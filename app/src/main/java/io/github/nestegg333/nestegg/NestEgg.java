package io.github.nestegg333.nestegg;

import android.app.Application;

/**
 * Created by aqeelp on 5/7/16.
 */
public class NestEgg extends Application {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String username;
    private String password;
    private String token;
}
