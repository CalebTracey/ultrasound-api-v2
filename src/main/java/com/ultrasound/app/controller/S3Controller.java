package com.ultrasound.app.controller;

import com.ultrasound.app.aws.S3ServiceImpl;
import com.ultrasound.app.exceptions.PresignedUrlException;
import com.ultrasound.app.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@CrossOrigin()
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/S3")
public class S3Controller {

    private final S3ServiceImpl s3Service;
    private final SyncService synchService;

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
        return ResponseEntity.created(uri).body(synchService.synchronize(s3Service.getFileNames()));
    }
}
