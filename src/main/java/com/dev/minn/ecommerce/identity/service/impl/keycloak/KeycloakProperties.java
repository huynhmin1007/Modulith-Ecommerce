//package com.dev.minn.ecommerce.identity.service.impl.keycloak;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "app.keycloak")
//@Getter
//@Setter
//public class KeycloakProperties {
//    public String clientId;
//    public String clientSecret;
//    public String serverUrl;
//    public String realm;
//    public String redirectUri;
//
//    public String getTokenUrl() {
//        return String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
//    }
//
//    public String getAuthUrl() {
//        return String.format(
//                "%s/realms/%s/protocol/openid-connect/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=openid profile email",
//                serverUrl,
//                realm,
//                clientId,
//                redirectUri
//        );
//    }
//
//    public String getLogoutUrl() {
//        return String.format("%s/realms/%s/protocol/openid-connect/logout", serverUrl, realm);
//    }
//}
