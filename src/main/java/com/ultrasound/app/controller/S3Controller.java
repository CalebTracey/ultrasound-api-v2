package com.ultrasound.app.controller;

import com.ultrasound.app.aws.S3ServiceImpl;
import com.ultrasound.app.aws.util.S3UtilsImpl;
import com.ultrasound.app.exceptions.PresignedUrlException;
import com.ultrasound.app.service.ClassificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@CrossOrigin()
@RestController
@RequestMapping("/api/S3")
public class S3Controller {

    @Autowired
    private S3ServiceImpl s3Service;
    @Autowired
    private S3UtilsImpl s3Utils;
    @Autowired
    private ClassificationServiceImpl classificationService;

    @GetMapping("/list")
    public ResponseEntity<?> getPreSignedUrl() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/S3/list").toUriString());
        return ResponseEntity.created(uri).body(s3Service.getFileNames());
    }

    @GetMapping("/link/{link}")
    public ResponseEntity<?> getPreSignedUrl(@PathVariable String link) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/S3/link/{link}").toUriString());
        return ResponseEntity.created(uri).body(s3Service.getPreSignedUrl(link)
                .orElseThrow(()-> new PresignedUrlException(link)));
    }

    @PostMapping("/synch")
    public ResponseEntity<?> synch() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/S3/synch/").toUriString());
        return ResponseEntity.created(uri).body(s3Service.synchronize());
    }

//    @PostMapping("/update")
//    public ResponseEntity<?> updateBucket() {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/S3/update/").toUriString());
//       return ResponseEntity.created(uri).body(s3Service.initializeMongoDatabase());
//    }
//
//    @PostMapping("/update/newData")
//    public ResponseEntity<?> updateBucketNewData() {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/S3/update/newData").toUriString());
//        return ResponseEntity.created(uri).body(s3Service.updateMongoDatabase());
//    }

    // DPT took out - leaving all filenames intact. Manual cleaning only
//    @PostMapping("/clean-extensions")
//    public ResponseEntity<?> cleanUnwantedS3Extensions() {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/S3/clean-extensions").toUriString());
//        return ResponseEntity.created(uri).body(s3Utils.cleanBucketFileExtensions());
//    }
//
//    @PostMapping("/export")
//    public  ResponseEntity<?> exportCurrentData() {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/S3/export").toUriString());
//        return ResponseEntity.created(uri).body(s3Utils.updateFileNamesAndExport());
//    }
}
