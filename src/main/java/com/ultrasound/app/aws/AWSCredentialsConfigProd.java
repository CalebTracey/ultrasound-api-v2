//// TODO uncomment for prod
package com.ultrasound.app.aws;

import com.amazonaws.auth.AWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Prod only - we set local AWS creds through Intellij Plugin for AWS

@Configuration
@Profile("prod")
public class AWSCredentialsConfigProd implements AWSCredentials {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String awsAccessKey;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String awsSecretKey;

    @Override
    public String getAWSAccessKeyId() {
        return awsAccessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return awsSecretKey;
    }
}
