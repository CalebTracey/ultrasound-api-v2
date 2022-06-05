package com.ultrasound.app.service;

import com.ultrasound.app.payload.response.MessageResponse;

import java.util.List;

public interface SyncService {
    // Look at the S3 bucket and make any database changes needed
    MessageResponse synchronize(List<String> files);

}
