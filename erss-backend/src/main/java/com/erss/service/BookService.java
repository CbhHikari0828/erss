package com.erss.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.erss.common.BizException;
import com.erss.domain.entity.Book;
import com.erss.domain.entity.BookImage;
import com.erss.domain.entity.UserAccount;
import com.erss.dto.CreateBookRequest;
import com.erss.dto.UpdateBookRequest;
import com.erss.mapper.BookImageMapper;
import com.erss.mapper.BookMapper;
import com.erss.mapper.UserAccountMapper;
import com.erss.util.TextLists;
import com.erss.vo.BookVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
  private final BookMapper bookMapper;
  private final BookImageMapper bookImageMapper;
  private final UserAccountMapper userAccountMapper;

  public BookService(BookMapper bookMapper, BookImageMapper bookImageMapper, UserAccountMapper userAccountMapper) {
    this.bookMapper = bookMapper;
    this.bookImageMapper = bookImageMapper;
    this.userAccountMapper = userAccountMapper;
  }

  public List<BookVO> listOnSale(String keyword, String category, String subcategory, String campus) {
    return bookMapper.selectList(Wrappers.<Book>lambdaQuery()
        .eq(Book::getStatus, "ON_SALE")
        .eq(category != null && !category.isBlank(), Book::getCategory, category)
        .eq(subcategory != null && !subcategory.isBlank(), Book::getSubcategory, subcategory)
        .eq(campus != null && !campus.isBlank(), Book::getCampus, campus)
        .and(keyword != null && !keyword.isBlank(), query -> query
          .like(Book::getTitle, keyword)
          .or()
          .like(Book::getCategory, keyword)
          .or()
          .like(Book::getSubcategory, keyword)
          .or()
          .like(Book::getSummary, keyword))
        .orderByDesc(Book::getCreatedAt))
      .stream()
      .map(this::toBookVO)
      .toList();
  }

  public BookVO getDetail(Long id) {
    Book book = bookMapper.selectById(id);
    if (book == null) {
      throw new BizException("书籍不存在");
    }
    return toBookVO(book);
  }

  public List<BookVO> listMine() {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    return bookMapper.selectList(Wrappers.<Book>lambdaQuery()
        .eq(Book::getSellerId, userId)
        .orderByDesc(Book::getCreatedAt))
      .stream()
      .map(this::toBookVO)
      .toList();
  }

  @Transactional
  public BookVO createBook(CreateBookRequest request) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    UserAccount user = userAccountMapper.selectById(userId);
    if (user == null || !Boolean.TRUE.equals(user.getShopRegistered())) {
      throw new BizException("请先注册商铺");
    }

    LocalDateTime now = LocalDateTime.now();
    boolean rentalEnabled = Boolean.TRUE.equals(request.rentalEnabled());
    validateRentalPricing(rentalEnabled, request.rentPrice(), request.rentalDeposit());

    Book book = new Book();
    book.setSellerId(userId);
    book.setTitle(request.title());
    book.setCategory(request.category());
    book.setSubcategory(request.subcategory());
    book.setPrice(request.price());
    book.setOriginalPrice(request.originalPrice());
    book.setRentalEnabled(rentalEnabled);
    book.setRentPrice(rentalEnabled ? request.rentPrice() : BigDecimal.ZERO);
    book.setRentalDeposit(rentalEnabled ? request.rentalDeposit() : BigDecimal.ZERO);
    book.setConditionText(request.condition());
    book.setCampus(request.campus());
    book.setCoverUrl(request.cover());
    book.setSummary(request.summary());
    book.setDescription(request.description());
    book.setTags(TextLists.join(request.tags()));
    book.setStatus("ON_SALE");
    book.setCreatedAt(now);
    book.setUpdatedAt(now);
    bookMapper.insert(book);

    if (request.images() != null) {
      int index = 0;
      for (String image : request.images()) {
        if (image == null || image.isBlank()) {
          continue;
        }
        BookImage bookImage = new BookImage();
        bookImage.setBookId(book.getId());
        bookImage.setImageUrl(image);
        bookImage.setSortNo(index++);
        bookImageMapper.insert(bookImage);
      }
    }

    return toBookVO(book);
  }

  @Transactional
  public BookVO updateBook(Long id, UpdateBookRequest request) {
    Long userId = Long.valueOf(String.valueOf(StpUtil.getLoginId()));
    Book book = bookMapper.selectById(id);
    if (book == null) {
      throw new BizException("书籍不存在");
    }
    if (!book.getSellerId().equals(userId)) {
      throw new BizException("无权修改该书籍");
    }

    if (request.title() != null) book.setTitle(request.title());
    if (request.category() != null) book.setCategory(request.category());
    if (request.subcategory() != null) book.setSubcategory(request.subcategory());
    if (request.price() != null) book.setPrice(request.price());
    if (request.originalPrice() != null) book.setOriginalPrice(request.originalPrice());
    if (request.rentalEnabled() != null) book.setRentalEnabled(request.rentalEnabled());
    if (request.rentPrice() != null) book.setRentPrice(request.rentPrice());
    if (request.rentalDeposit() != null) book.setRentalDeposit(request.rentalDeposit());
    validateRentalPricing(Boolean.TRUE.equals(book.getRentalEnabled()), book.getRentPrice(), book.getRentalDeposit());
    if (request.condition() != null) book.setConditionText(request.condition());
    if (request.campus() != null) book.setCampus(request.campus());
    if (request.cover() != null) book.setCoverUrl(request.cover());
    if (request.summary() != null) book.setSummary(request.summary());
    if (request.description() != null) book.setDescription(request.description());
    if (request.tags() != null) book.setTags(TextLists.join(request.tags()));
    if (request.status() != null) book.setStatus(request.status());
    book.setUpdatedAt(LocalDateTime.now());
    bookMapper.updateById(book);

    if (request.images() != null) {
      bookImageMapper.delete(Wrappers.<com.erss.domain.entity.BookImage>lambdaQuery()
        .eq(com.erss.domain.entity.BookImage::getBookId, book.getId()));
      int index = 0;
      for (String image : request.images()) {
        if (image == null || image.isBlank()) {
          continue;
        }
        com.erss.domain.entity.BookImage bookImage = new com.erss.domain.entity.BookImage();
        bookImage.setBookId(book.getId());
        bookImage.setImageUrl(image);
        bookImage.setSortNo(index++);
        bookImageMapper.insert(bookImage);
      }
    }

    return toBookVO(book);
  }

  private BookVO toBookVO(Book book) {
    UserAccount seller = userAccountMapper.selectById(book.getSellerId());
    List<String> images = bookImageMapper.selectList(Wrappers.<BookImage>lambdaQuery()
        .eq(BookImage::getBookId, book.getId())
        .orderByAsc(BookImage::getSortNo))
      .stream()
      .map(BookImage::getImageUrl)
      .toList();
    if (images.isEmpty() && book.getCoverUrl() != null && !book.getCoverUrl().isBlank()) {
      images = List.of(book.getCoverUrl());
    }

    return new BookVO(
      book.getId(),
      book.getTitle(),
      book.getCategory(),
      book.getSubcategory(),
      book.getPrice(),
      book.getOriginalPrice(),
      Boolean.TRUE.equals(book.getRentalEnabled()),
      normalizeAmount(book.getRentPrice()),
      normalizeAmount(book.getRentalDeposit()),
      book.getConditionText(),
      book.getCampus(),
      seller == null ? "" : seller.getUsername(),
      seller == null ? "" : seller.getDepartment(),
      new BigDecimal("4.9"),
      images,
      book.getCoverUrl(),
      book.getSummary(),
      TextLists.split(book.getTags()),
      book.getDescription(),
      book.getStatus(),
      book.getCreatedAt(),
      book.getSellerId()
    );
  }

  private void validateRentalPricing(boolean rentalEnabled, BigDecimal rentPrice, BigDecimal rentalDeposit) {
    if (!rentalEnabled) {
      return;
    }
    if (rentPrice == null || rentPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BizException("请填写有效的租金");
    }
    if (rentalDeposit == null || rentalDeposit.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BizException("请填写有效的押金");
    }
  }

  private BigDecimal normalizeAmount(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }
}
