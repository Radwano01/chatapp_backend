package com.project.chatApp.s3;

import com.project.chatApp.chat.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @Value("${aws.s3.bucket}")
    private String BUCKET_NAME;

    @PostMapping("/upload")
    public String uploadFileApi(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "FILE") MessageType type) {

        return s3Service.uploadFile(file, type);
    }

    @DeleteMapping("/delete")
    public void deleteFileApi(@RequestParam("fileName") String fileName) {
        s3Service.deleteFile(fileName);
    }
}

