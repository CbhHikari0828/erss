package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.dto.CreateBookRequest;
import com.erss.dto.UpdateBookRequest;
import com.erss.service.BookService;
import com.erss.vo.BookVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping
  public ApiResponse<List<BookVO>> list(
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String category,
    @RequestParam(required = false) String subcategory,
    @RequestParam(required = false) String campus
  ) {
    return ApiResponse.ok(bookService.listOnSale(keyword, category, subcategory, campus));
  }

  @GetMapping("/mine")
  public ApiResponse<List<BookVO>> listMine() {
    return ApiResponse.ok(bookService.listMine());
  }

  @GetMapping("/{id}")
  public ApiResponse<BookVO> detail(@PathVariable Long id) {
    return ApiResponse.ok(bookService.getDetail(id));
  }

  @PostMapping
  public ApiResponse<BookVO> create(@Valid @RequestBody CreateBookRequest request) {
    return ApiResponse.ok(bookService.createBook(request));
  }

  @PostMapping("/{id}")
  public ApiResponse<BookVO> update(@PathVariable Long id, @RequestBody UpdateBookRequest request) {
    return ApiResponse.ok(bookService.updateBook(id, request));
  }
}
