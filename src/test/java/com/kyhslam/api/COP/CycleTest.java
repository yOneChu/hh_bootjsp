package com.kyhslam.api.COP;

import com.kyhslam.service.OneCycleFunc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CycleTest {

    @Autowired
    private OneCycleFunc oneCycleFunc;

    @Test
    public void testCycle() {

        oneCycleFunc.runOneCycle("TEST-630231");


    }
}
