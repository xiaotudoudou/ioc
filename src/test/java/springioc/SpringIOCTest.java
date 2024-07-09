package springioc;


import springioc.entity.TestController;
import org.junit.Test;
import springioc.entity.TestController;
import springioc.ioc.SpringIOC;

import java.io.FileNotFoundException;

public class SpringIOCTest {
    @Test
    public void testScan() throws FileNotFoundException {

        SpringIOC springIOC = new SpringIOC();
        springIOC.initBeans();
        TestController instance = (TestController)springIOC.getInstance(TestController.class.getName());
        instance.test();

    }
}
