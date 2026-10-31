package com.kyhslam.api.COP;

import com.kyhslam.service.cop.CopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class batchTest {

    @Autowired
    private CopService copService;

    @Test
    public void testRunBatch() {
        copService.runBatch();
    }

}
