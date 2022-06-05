package com.ultrasound.app.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Configuration
@AllArgsConstructor
@NoArgsConstructor
public class S3ServiceImpl implements S3Service {

    private AmazonS3 s3Client;

    private Environment env;

    private String getAWSBucketName() {
        return env.getProperty("aws.bucket.name");
    }

    private @NotNull List<S3ObjectSummary> s3FileNames() {
        List<S3ObjectSummary> objectListing = s3ListObjects();
        log.info("Object listing count: {}", objectListing.size());
        return objectListing.stream()
                .filter(this::filterExtensions).collect(Collectors.toList());
    }

    // For files that get deleted out of S3, we need a mechanism to see if we also need
    // to delete them out of the database. This class keeps a flag so we can do a sweep at
    // the end of our processing to see if there are any entries that need deleting.

    @Override
    public List<String> getFileNames() {
        return s3FileNames().stream().map(S3ObjectSummary::getKey)     // keys = title of file
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull Optional<String> getPreSignedUrl(String link) {
        log.info("Getting pre-signed URL for: {}", link);
        LocalDateTime date = LocalDateTime.now().plusSeconds(100);
        String presignedUrl = null;
        try {
            presignedUrl = s3Client.generatePresignedUrl(getAWSBucketName(), link, date.toDate()).toString();
        } catch (AmazonS3Exception e) {
            log.error("AmazonS3Exception: {}", e.getErrorMessage());
            e.getStackTrace();
        }
        assert presignedUrl != null;
        return Optional.of(presignedUrl);
    }

    public List<S3ObjectSummary> s3ListObjects() {
        List<S3ObjectSummary> summaries = new ArrayList<>();
        try {
        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(getAWSBucketName());
        ListObjectsV2Result result = s3Client.listObjectsV2(request);
        summaries.addAll(result.getObjectSummaries());
        while (result.isTruncated()) {
            result = s3Client.listObjectsV2(request);
            summaries.addAll(result.getObjectSummaries());
            String token = result.getNextContinuationToken();
            request.setContinuationToken(token);
        }

        } catch (AmazonServiceException e) {
            log.error("AmazonServiceException: {}", e.getErrorMessage());
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            log.error("SdkClientException: {}", e.getLocalizedMessage());
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        return summaries;
    }

    private @NotNull Boolean filterExtensions(@NotNull S3ObjectSummary objectSummary) {
        String[] extensions = new String[]{"mp4", "*.mp4","jpg", "*.jpg", "jpeg", "*.jpeg", "gif", "*.gif", "png", "*.png"};
        return FilenameUtils.isExtension(objectSummary.getKey(), extensions);
    }
}

