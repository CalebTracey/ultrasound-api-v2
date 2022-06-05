package com.ultrasound.app.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
public class S3Config {
    private final Environment env;

    private final AWSCredentialsProviderImpl credentialsProvider;

    @Autowired
    public S3Config(AWSCredentialsProviderImpl credentialsProvider, Environment env) {
        this.credentialsProvider = credentialsProvider;
        this.env = env;
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();

        List<CORSRule.AllowedMethods> rule1AM = new ArrayList<>();
        rule1AM.add(CORSRule.AllowedMethods.PUT);
        rule1AM.add(CORSRule.AllowedMethods.POST);
        rule1AM.add(CORSRule.AllowedMethods.DELETE);
        CORSRule rule1 = new CORSRule().withId("CORSRule1").withAllowedMethods(rule1AM)
                .withAllowedOrigins(Arrays.asList(Objects.requireNonNull(env.getProperty("aws.corsrules.allowedorigins")).split(",")));
        List<CORSRule> rules = new ArrayList<>();
        rules.add(rule1);
        configuration.setRules(rules);
        AmazonS3 s3Client = null;
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(credentialsProvider)
                    .build();

            String bucketName = env.getProperty("aws.bucket.name");
            s3Client.setBucketCrossOriginConfiguration(bucketName, configuration);

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
        return s3Client;
    }
}
