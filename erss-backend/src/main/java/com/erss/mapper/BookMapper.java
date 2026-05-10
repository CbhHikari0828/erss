package com.erss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erss.domain.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
