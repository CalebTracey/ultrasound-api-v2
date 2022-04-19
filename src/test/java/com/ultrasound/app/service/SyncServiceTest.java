package com.ultrasound.app.service;

import com.amazonaws.services.s3.AmazonS3;
import com.ultrasound.app.payload.response.MessageResponse;
import com.ultrasound.app.service.SynchService;
import com.ultrasound.app.service.SynchServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

//@SpringJUnitConfig
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Slf4j
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles("local")
@SpringBootTest
@ActiveProfiles("local")
public class SyncServiceTest {
//
//    @Autowired
//    SynchService synchService;
//
//    @BeforeEach
//    void setUp() {
//
//    }
//
//    @Test
//    public void emptyList() {
//        MessageResponse resp = synchService.synchronize(new ArrayList<>());
//        assert(resp.equals(""));
//    }
}
