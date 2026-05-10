package com.erss.service;

import com.erss.common.BizException;
import com.erss.vo.UploadImageVO;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
  private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
  private static final Set<String> SUPPORTED_IMAGE_TYPES = Set.of(
    "image/jpeg",
    "image/png",
    "image/webp",
    "image/gif"
  );

  private final MinioClient minioClient;
  private final String bucket;
  private final String endpoint;
  private final String publicBaseUrl;
  private final boolean publicRead;
  private final String imagePrefix;

  public StorageService(
    MinioClient minioClient,
    @Value("${app.minio.bucket}") String bucket,
    @Value("${app.minio.endpoint}") String endpoint,
    @Value("${app.minio.public-base-url:}") String publicBaseUrl,
    @Value("${app.minio.public-read:true}") boolean publicRead,
    @Value("${app.minio.image-prefix}") String imagePrefix
  ) {
    this.minioClient = minioClient;
    this.bucket = bucket;
    this.endpoint = endpoint;
    this.publicBaseUrl = publicBaseUrl;
    this.publicRead = publicRead;
    this.imagePrefix = imagePrefix;
  }

  public UploadImageVO uploadImage(MultipartFile file, String folder) {
    validateImage(file);
    try {
      ensureBucket();
      String contentType = normalizeContentType(file.getContentType());
      String objectName = createObjectName(file.getOriginalFilename(), folder, contentType);
      minioClient.putObject(PutObjectArgs.builder()
        .bucket(bucket)
        .object(objectName)
        .stream(file.getInputStream(), file.getSize(), -1)
        .contentType(contentType)
        .build());
      return new UploadImageVO(publicUrl(objectName), objectName, bucket, contentType, file.getSize());
    }
    catch (BizException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw new BizException(500, "图片上传失败");
    }
  }

  private void ensureBucket() throws Exception {
    boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
      .bucket(bucket)
      .build());
    if (!exists) {
      minioClient.makeBucket(MakeBucketArgs.builder()
        .bucket(bucket)
        .build());
    }
    if (publicRead) {
      minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
        .bucket(bucket)
        .config(publicReadPolicy())
        .build());
    }
  }

  private String publicReadPolicy() {
    return """
      {
        "Version": "2012-10-17",
        "Statement": [
          {
            "Effect": "Allow",
            "Principal": {"AWS": ["*"]},
            "Action": ["s3:GetObject"],
            "Resource": ["arn:aws:s3:::%s/*"]
          }
        ]
      }
      """.formatted(bucket);
  }

  private void validateImage(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BizException("请选择图片");
    }
    if (file.getSize() > MAX_IMAGE_SIZE) {
      throw new BizException("图片不能超过 10MB");
    }
    String contentType = normalizeContentType(file.getContentType());
    if (!SUPPORTED_IMAGE_TYPES.contains(contentType)) {
      throw new BizException("仅支持 JPG、PNG、WebP 或 GIF 图片");
    }
  }

  private String createObjectName(String originalFilename, String folder, String contentType) {
    LocalDate today = LocalDate.now();
    String normalizedFolder = normalizeFolder(folder);
    return String.join("/",
      trimSlashes(imagePrefix),
      normalizedFolder,
      String.valueOf(today.getYear()),
      String.format("%02d", today.getMonthValue()),
      String.format("%02d", today.getDayOfMonth()),
      UUID.randomUUID() + extension(originalFilename, contentType)
    );
  }

  private String normalizeFolder(String folder) {
    if (!StringUtils.hasText(folder)) {
      return "misc";
    }
    String normalized = folder.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "-");
    normalized = normalized.replaceAll("-+", "-");
    return StringUtils.hasText(normalized) ? normalized : "misc";
  }

  private String extension(String originalFilename, String contentType) {
    if (StringUtils.hasText(originalFilename)) {
      String name = originalFilename.trim();
      int index = name.lastIndexOf('.');
      if (index >= 0 && index < name.length() - 1) {
        String ext = name.substring(index).toLowerCase(Locale.ROOT);
        if (ext.matches("\\.[a-z0-9]{1,8}")) {
          return ext;
        }
      }
    }
    return switch (contentType) {
      case "image/png" -> ".png";
      case "image/webp" -> ".webp";
      case "image/gif" -> ".gif";
      default -> ".jpg";
    };
  }

  private String publicUrl(String objectName) {
    String base = StringUtils.hasText(publicBaseUrl)
      ? publicBaseUrl
      : endpoint + "/" + bucket;
    return trimTrailingSlash(base) + "/" + objectName;
  }

  private String normalizeContentType(String contentType) {
    return StringUtils.hasText(contentType)
      ? contentType.trim().toLowerCase(Locale.ROOT)
      : "application/octet-stream";
  }

  private String trimSlashes(String value) {
    if (!StringUtils.hasText(value)) {
      return "images";
    }
    return value.replaceAll("^/+", "").replaceAll("/+$", "");
  }

  private String trimTrailingSlash(String value) {
    return value.replaceAll("/+$", "");
  }
}
