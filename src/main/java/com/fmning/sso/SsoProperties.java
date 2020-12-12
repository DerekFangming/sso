package com.fmning.sso;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sso")
public class SsoProperties {
    private String production;

    public boolean isProduction() {
        return Boolean.parseBoolean(production);
    }
}
