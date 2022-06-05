package com.ultrasound.app.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
class S3RepositoryTest {

    @Mock
    private AmazonS3Client config;
    @Mock
    private AutoCloseable autoCloseable;
    @Mock
    private S3ServiceImpl s3ServiceTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        s3ServiceTest = new S3ServiceImpl();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private

    @Test
    void shouldParseFileNamesToMap() {


    }


}