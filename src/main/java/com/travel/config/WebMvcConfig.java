package com.travel.config;

import com.travel.converter.WebTimeConverter;
import com.travel.interceptor.AdminInterceptor;
import com.travel.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig extends WebMvcConfigurationSupport {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 管理员拦截器：守卫 /api/admin/**，但放行管理员登录
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns(
                        "/api/admin/auth/**",
                        "/doc.html", "/webjars/**", "/swagger-resources/**",
                        "/v3/api-docs/**", "/favicon.ico"
                );

        // 用户拦截器：守卫需要登录的用户端接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                        "/api/user/**",
                        "/api/cart/**",
                        "/api/order/**",
                        "/api/favorite/**",
                        "/api/coupon/grab",
                        "/api/coupon/mine",
                        "/api/note/publish",
                        "/api/note/mine",
                        "/api/comment/submit",
                        "/api/message/**",
                        "/api/history/**"
                )
                .excludePathPatterns(
                        "/doc.html", "/webjars/**", "/swagger-resources/**",
                        "/v3/api-docs/**", "/favicon.ico"
                );
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建一个新的消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 需要为消息转换器设置一个新的对象映射器，对象转换器可以将Java对象转换为JSON格式的数据
        converter.setObjectMapper(new WebTimeConverter());
        // 将自己的消息转换器加入到容器中
        converters.add(1, converter);
    }
}

