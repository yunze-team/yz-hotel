package com.yzly.api;

import com.yzly.api.service.jielv.PushAvailabilityAndInventoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test2Xml {

    @Autowired
    private PushAvailabilityAndInventoryService pushAvailabilityAndInventoryService;

    @Test
    public  void test2XML(){
        pushAvailabilityAndInventoryService.PushAvailabilityAndInventory();
    }
}
