package com.resnal.Enterprise_ai_resume_analyzer.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

@Service
public class ResumeStorageService {

    private final Storage storage;

    @Value("${gcp.storage.bucket-name:enterprise-resume-bucket}")
    private String bucketName;

    public ResumeStorageService(Storage storage) {
        this.storage = storage;
    }

    /**
     * Uploads the original PDF resume to a Google Cloud Storage bucket
     * with a unique UUID to prevent file collisions.
     */
    public String uploadResume(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file to GCS");
        }

        String uniqueFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uniqueFileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return String.format("gs://%s/%s", bucketName, uniqueFileName);
    }
}