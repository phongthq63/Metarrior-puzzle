//package com.bamisu.log.sdk.module.auth;
//
//import com.bamisu.gamelib.utils.Utils;
//import com.bamisu.log.sdk.module.auth.entities.IdTokenPayload;
//import com.bamisu.log.sdk.module.auth.entities.TokenResponse;
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.Unirest;
//import com.smartfoxserver.v2.entities.data.ISFSObject;
//import com.smartfoxserver.v2.entities.data.SFSObject;
//import io.jsonwebtoken.JwsHeader;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
//import org.bouncycastle.openssl.PEMParser;
//import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
//
//import java.io.FileReader;
//import java.security.PrivateKey;
//import java.util.Base64;
//import java.util.Date;
//
//public class AppleLoginUtils {
//    private static final String APPLE_AUTH_URL = "https://appleid.apple.com/auth/token";
//
//    private static String KEY_ID;
//    private static String TEAM_ID;
//    private static String CLIENT_ID;
//    private static PrivateKey pKey;
//
//
//    private static AppleLoginUtils ourInstance = new AppleLoginUtils();
//    public static AppleLoginUtils getInstance() {
//        return ourInstance;
//    }
//    private AppleLoginUtils() {
//        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/config/Apple IOS Developer.json"));
//
//        KEY_ID = config.getUtfString("key_id");
//        TEAM_ID = config.getUtfString("team_id");
//        CLIENT_ID = config.getUtfString("client_id");
//    }
//
//
//
//    /*------------------------------------------------------------------------------------------------------*/
//    /*------------------------------------------------------------------------------------------------------*/
//    private PrivateKey getPrivateKey() throws Exception {
//        String path = System.getProperty("user.dir") + "/config/AuthKey_R23H8GH6KD.p8";
//        System.out.println(path);
//
//        PEMParser pemParser = new PEMParser(new FileReader(path));
//        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
//        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
//        PrivateKey pKey = converter.getPrivateKey(object);
//
//        return pKey;
//    }
//
//    private String generateJWT() throws Exception {
//        if (pKey == null) pKey = getPrivateKey();
//
//        String token = Jwts.builder()
//                .setHeaderParam(JwsHeader.KEY_ID, KEY_ID)
//                .setIssuer(TEAM_ID)
//                .setAudience("https://appleid.apple.com")
//                .setSubject(CLIENT_ID)
//                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)))
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .signWith(SignatureAlgorithm.ES256, pKey)
//                .compact();
//
//        return token;
//    }
//
//    /*
//     * Returns unique user id from apple
//     * */
//    public String appleAuth(String authorizationCode) throws Exception {
//
//        String token = generateJWT();
//
//        HttpResponse<String> response = Unirest.post(APPLE_AUTH_URL)
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .field("client_id", CLIENT_ID)
//                .field("client_secret", token)
//                .field("grant_type", "authorization_code")
//                .field("code", authorizationCode)
//                .asString();
//
//        System.out.println(response.getBody());
//
//        TokenResponse tokenResponse= Utils.fromJson(response.getBody(),TokenResponse.class);
//        if(tokenResponse.error != null) return null;
//
//        String idToken = tokenResponse.id_token;
//        String payload = idToken.split("\\.")[1];//0 is header we ignore it for now
//        String decoded = new String(Base64.getDecoder().decode(payload));
//
//        IdTokenPayload idTokenPayload = Utils.fromJson(decoded,IdTokenPayload.class);
//        return idTokenPayload.sub;
//    }
//}
