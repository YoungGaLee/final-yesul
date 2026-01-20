package com.yesul.community.service;

import com.yesul.util.ImageUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import com.yesul.community.model.entity.Post;
import com.yesul.community.model.entity.PostImage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService {

    private final ImageUpload imageUpload;

    @Override
    public String uploadImage(MultipartFile image) {
        return imageUpload.uploadAndGetUrl("community", image);
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            imageUpload.delete(imageUrl); // 올바른 순서: imageUrl, domain
            System.out.println("NCP에서 삭제 완료: " + imageUrl);
        } catch (Exception e) {
            System.err.println("NCP 이미지 삭제 실패: " + imageUrl + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public String extractFirstImageUrl(String contentHtml) {
        if (contentHtml == null || contentHtml.trim().isEmpty()) {
            return null;
        }

        try {
            Document doc = Jsoup.parse(contentHtml);
            Element img = doc.selectFirst("img");
            return img != null ? img.attr("src") : null;
        } catch (Exception e) {
            System.err.println("이미지 URL 추출 중 오류: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> getImageUrlsByPost(Post post) {
        if (post == null || post.getImages() == null || post.getImages().isEmpty()) {
            return new ArrayList<>();
        }

        return post.getImages().stream()
                .filter(image -> image != null && image.getImageUrl() != null)
                .map(PostImage::getImageUrl)
                .toList();
    }

    @Override
    public List<String> extractImageUrlsFromContent(String content) {
        List<String> urls = new ArrayList<>();
        Document doc = Jsoup.parse(content);
        for (Element img : doc.select("img")) {
            urls.add(img.attr("src"));
        }
        return urls;
    }
}