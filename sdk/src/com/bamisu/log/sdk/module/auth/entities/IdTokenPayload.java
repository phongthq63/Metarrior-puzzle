package com.bamisu.log.sdk.module.auth.entities;

public class IdTokenPayload {
    public String iss;
    public String aud;
    public Long exp;
    public Long iat;
    public String sub;//users unique id
    public String at_hash;
    public String email;
    public boolean email_verified;
    public Long auth_time;
    public boolean nonce_supported;
}
