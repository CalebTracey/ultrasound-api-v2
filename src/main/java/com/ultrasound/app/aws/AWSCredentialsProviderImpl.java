//// TODO uncomment for prod
//package com.ultrasound.app.aws;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSCredentialsProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AWSCredentialsProviderImpl implements AWSCredentialsProvider {
//
//    @Autowired
//    private AWSCredentialsConfig credentialsConfig;
//
//    @Override
//    public AWSCredentials getCredentials() {
//        return credentialsConfig;
//    }
//
//    @Override
//    public void refresh() {
//
//    }
//}
