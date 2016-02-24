package com.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test place holder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleAppApplication.class)
public class SampleAppApplicationTests {

    @Autowired
    private Echo echo;

    @Test
    public void contextLoads() {
        Word word = this.echo.echo();
        Assert.assertNotNull(word);
    }

}
