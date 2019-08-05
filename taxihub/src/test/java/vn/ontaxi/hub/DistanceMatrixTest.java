package vn.ontaxi.hub;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vn.ontaxi.common.service.ConfigurationService;
import vn.ontaxi.common.service.DistanceMatrixService;

@SpringBootTest(classes = {DistanceMatrixService.class, ConfigurationService.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class DistanceMatrixTest {
    @Autowired
    private DistanceMatrixService distanceMatrixService;

    @Test
    public void sendSingleRouteSMS() {
        double distance = distanceMatrixService.getDistance("Phạm Văn Bạch, Cầu Giấy, Hà Nội, Vietnam", "Thanh Lâm, Mê Linh, Hanoi, Vietnam");
        System.out.println(distance);
        Assert.assertTrue(distance > 0);
    }
}
