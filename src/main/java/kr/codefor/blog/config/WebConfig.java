package kr.codefor.blog.config;

import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://blog.codefor.kr");
    }

    @Bean
    public TomcatConnectorCustomizer http2ProtocolCustomizer() {
        return (connector) -> {
            for (UpgradeProtocol upgradeProtocol: connector.findUpgradeProtocols()) {
                if (upgradeProtocol instanceof Http2Protocol) {
                    Http2Protocol http2Protocol = (Http2Protocol)upgradeProtocol;
                    http2Protocol.setOverheadContinuationThreshold(0);
                    http2Protocol.setOverheadDataThreshold(0);
                    http2Protocol.setOverheadWindowUpdateThreshold(0);
                }
            }
        };
    }
}
