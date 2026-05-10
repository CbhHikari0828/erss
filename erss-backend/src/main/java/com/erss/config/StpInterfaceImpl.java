package com.erss.config;

import cn.dev33.satoken.stp.StpInterface;
import com.erss.domain.entity.UserAccount;
import com.erss.mapper.UserAccountMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StpInterfaceImpl implements StpInterface {
  private final UserAccountMapper userAccountMapper;

  public StpInterfaceImpl(UserAccountMapper userAccountMapper) {
    this.userAccountMapper = userAccountMapper;
  }

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return Collections.emptyList();
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    UserAccount user = userAccountMapper.selectById(Long.valueOf(String.valueOf(loginId)));
    if (user == null || user.getRole() == null) {
      return Collections.emptyList();
    }
    return List.of(user.getRole());
  }
}
