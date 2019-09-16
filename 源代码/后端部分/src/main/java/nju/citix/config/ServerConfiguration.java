package nju.citix.config;

import org.springframework.stereotype.Component;

/**
 * @author DW
 * @date 2019/8/15
 */
@Component
public class ServerConfiguration {
    public String getUrl() {
        return AlipayConfig.URL;
    }
}