package com.erss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erss.domain.entity.WalletTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {
}
