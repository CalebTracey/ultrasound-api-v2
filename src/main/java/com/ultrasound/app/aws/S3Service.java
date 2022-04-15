package com.ultrasound.app.aws;

import com.ultrasound.app.payload.response.MessageResponse;

import java.util.List;
import java.util.Optional;

public interface S3Service {
    Optional<String> getPreSignedUrl(String key);
    List<String> getFileNames();
}
