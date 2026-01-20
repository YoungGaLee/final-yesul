package com.yesul.util;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageUpload {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public String uploadAndGetUrl(String domain, MultipartFile file) {
        String key = String.format("%s/image/%s",
                domain,
                file.getOriginalFilename());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return getUrl(key);

        } catch (IOException e) {
            throw new RuntimeException("AWS S3 이미지 업로드 실패", e);
        }
    }

    private String getUrl(String key) {
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucket,
                region,
                key
        );
    }

    public void delete(String imageUrl) {
        try {
            String baseUrl = String.format(
                    "https://%s.s3.%s.amazonaws.com/",
                    bucket,
                    region
            );

            if (!imageUrl.startsWith(baseUrl)) {
                throw new RuntimeException("잘못된 이미지 URL입니다: " + imageUrl);
            }

            String key = imageUrl.substring(baseUrl.length());

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);

        } catch (Exception e) {
            throw new RuntimeException("AWS S3 이미지 삭제 실패: " + imageUrl, e);
        }
    }
}