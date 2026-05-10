package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.domain.entity.Shop;
import com.erss.domain.entity.UserAccount;
import com.erss.dto.RegisterShopRequest;
import com.erss.mapper.ShopMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.vo.ShopVO;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {
  private final ShopMapper shopMapper;
  private final UserAccountMapper userAccountMapper;

  public ShopService(ShopMapper shopMapper, UserAccountMapper userAccountMapper) {
    this.shopMapper = shopMapper;
    this.userAccountMapper = userAccountMapper;
  }

  @Transactional
  public ShopVO register(RegisterShopRequest request) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    LocalDateTime now = LocalDateTime.now();

    Shop shop = shopMapper.selectOne(Wrappers.<Shop>lambdaQuery()
      .eq(Shop::getUserId, userId)
      .last("limit 1"));

    if (shop == null) {
      shop = new Shop();
      shop.setUserId(userId);
      shop.setCreatedAt(now);
    }
    shop.setShopName(request.shopName());
    shop.setShopCampus(request.shopCampus());
    shop.setShopIntro(request.shopIntro());
    shop.setUpdatedAt(now);

    if (shop.getId() == null) {
      shopMapper.insert(shop);
    }
    else {
      shopMapper.updateById(shop);
    }

    UserAccount user = userAccountMapper.selectById(userId);
    user.setShopRegistered(true);
    user.setRole("seller");
    user.setUpdatedAt(now);
    userAccountMapper.updateById(user);

    return new ShopVO(shop.getId(), shop.getUserId(), shop.getShopName(), shop.getShopCampus(), shop.getShopIntro());
  }
}
