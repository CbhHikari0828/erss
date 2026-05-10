package com.erss.controller;

import com.erss.common.ApiResponse;
import com.erss.service.StorageService;
import com.erss.vo.UploadImageVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
  private final StorageService storageService;

  public UploadController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PostMapping("/images")
  public ApiResponse<UploadImageVO> uploadImage(
    @RequestParam("file") MultipartFile file,
    @RequestParam(required = false, defaultValue = "misc") String folder
  ) {
    return ApiResponse.ok(storageService.uploadImage(file, folder));
  }
}
