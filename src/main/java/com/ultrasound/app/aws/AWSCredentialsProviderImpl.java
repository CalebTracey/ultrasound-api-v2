package com.ultrasound.app.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Prod only - we set local AWS creds through Intellij Plugin for AWS

@Configuration
public class AWSCredentialsProviderImpl implements AWSCredentialsProvider {

    private final AWSCredentialsConfig credentialsConfig;

    @Autowired
    public AWSCredentialsProviderImpl (AWSCredentialsConfig credentialsConfig) {
        this.credentialsConfig = credentialsConfig;
    }
    @Override
    public AWSCredentials getCredentials() {
        return credentialsConfig;
    }

    @Override
    public void refresh() {

    }
}
