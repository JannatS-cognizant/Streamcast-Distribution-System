package org.example.contractservice.client;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthHeaderConfig {

    @Bean
    public RequestInterceptor authHeaderForwardingInterceptor() {
        return template -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return;
            }
            HttpServletRequest request = attrs.getRequest();
            String email = request.getHeader("X-User-Email");
            String role  = request.getHeader("X-User-Role");
            if (email != null) {
                template.header("X-User-Email", email);
            }
            if (role != null) {
                template.header("X-User-Role", role);
            }
        };
    }
}
