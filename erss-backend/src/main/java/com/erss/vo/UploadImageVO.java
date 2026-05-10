package com.erss.vo;

public record UploadImageVO(
  String url,
  String objectName,
  String bucket,
  String contentType,
  long size
) {
}
