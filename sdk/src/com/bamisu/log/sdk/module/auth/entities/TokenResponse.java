package com.bamisu.log.sdk.module.auth.entities;

public class TokenResponse {
    public String access_token;
    public String token_type;
    public Long expires_in;
    public String refresh_token;
    public String id_token;

    public String error;
    public String error_description;
}
