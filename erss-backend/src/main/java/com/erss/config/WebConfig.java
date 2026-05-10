package com.erss.config;

import cn.dev33.satoken.stp.StpUtil;
import com.erss.common.BizException;
import com.erss.domain.entity.UserAccount;
import com.erss.mapper.UserAccountMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private static final Set<String> PUBLIC_API_PREFIXES = Set.of(
    "/api/login",
    "/api/logout",
    "/api/books",
    "/api/health"
  );

  private final UserAccountMapper userAccountMapper;

  public WebConfig(UserAccountMapper userAccountMapper) {
    this.userAccountMapper = userAccountMapper;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new HandlerInterceptor() {
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
          return true;
        }

        String uri = request.getRequestURI();
        if ("/error".equals(uri) || !uri.startsWith("/api/")) {
          return true;
        }

        for (String prefix : PUBLIC_API_PREFIXES) {
          if (uri.equals(prefix) || uri.startsWith(prefix + "/")) {
            return true;
          }
        }

        StpUtil.checkLogin();
        Long loginId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
        UserAccount user = userAccountMapper.selectOne(Wrappers.<UserAccount>lambdaQuery()
          .select(UserAccount::getId, UserAccount::getBanned)
          .eq(UserAccount::getId, loginId)
          .last("limit 1"));
        if (user != null && Boolean.TRUE.equals(user.getBanned())) {
          throw new BizException(403, "账号已被封禁，请联系管理员");
        }
        return true;
      }
    }).addPathPatterns("/**");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOriginPatterns("*")
      .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
      .allowedHeaders("*")
      .allowCredentials(true)
      .maxAge(3600);
  }
}
