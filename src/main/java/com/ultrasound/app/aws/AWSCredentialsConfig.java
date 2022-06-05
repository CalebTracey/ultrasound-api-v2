//// TODO uncomment for prod
package com.ultrasound.app.aws;

import com.amazonaws.auth.AWSCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

// Prod only - we set local AWS creds through Intellij Plugin for AWS

@Configuration
public class AWSCredentialsConfig implements AWSCredentials {
    private final Environment env;

    @Autowired
    public AWSCredentialsConfig (Environment env) {
        this.env = env;
    }
    @Override
    public String getAWSAccessKeyId() {
        return env.getProperty("aws.access.key.id");
    }

    @Override
    public String getAWSSecretKey() {
        return env.getProperty("aws.secret.key");
    }
}
