package com.project.chatApp.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.chatApp.chat.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String BUCKET_NAME;

    public String uploadFile(MultipartFile file, MessageType type) {
        String fileName = "";
        try {
            // choose prefix based on type
            String prefix = switch (type) {
                case IMAGE -> "images/";
                case VIDEO -> "videos/";
                case VOICE -> "audio/";
                default -> "files/";
            };

            fileName = prefix + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // upload directly using InputStream
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    BUCKET_NAME,
                    fileName,
                    file.getInputStream(),
                    metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
