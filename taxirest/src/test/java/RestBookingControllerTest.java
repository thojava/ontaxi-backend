import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.ontaxi.common.constant.CarType;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.repository.BookingRepository;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;
import vn.ontaxi.common.jpa.repository.ViewPriceRepository;
import vn.ontaxi.common.service.DistanceMatrixService;
import vn.ontaxi.common.service.FeeCalculator;
import vn.ontaxi.common.service.PriceCalculator;
import vn.ontaxi.common.utils.PriceUtils;
import vn.ontaxi.rest.app.Application;
import vn.ontaxi.rest.controller.RestBookingController;
import vn.ontaxi.rest.payload.dto.request.BookingCalculatePriceRequestDTO;
import vn.ontaxi.rest.payload.dto.request.PostBookingRequestDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
@PropertySource(value = {"classpath:application.properties"})
public class RestBookingControllerTest extends AbstractControllerTest {

    private MockMvc mockMvc;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ViewPriceRepository viewPriceRepository;
    @Mock
    private PromotionPlanRepository promotionPlanRepository;
    @Mock
    private PriceCalculator priceCalculator;
    @Mock
    private DistanceMatrixService distanceMatrixService;
    @Mock
    private FeeCalculator feeCalculator;
    @Mock
    private PriceUtils priceUtils;
    @Autowired
    private RestBookingController restBookingController;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private Faker faker;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(restBookingController, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(restBookingController, "viewPriceRepository", viewPriceRepository);
        ReflectionTestUtils.setField(restBookingController, "promotionPlanRepository", promotionPlanRepository);
        ReflectionTestUtils.setField(restBookingController, "priceCalculator", priceCalculator);
        ReflectionTestUtils.setField(restBookingController, "distanceMatrixService", distanceMatrixService);
        ReflectionTestUtils.setField(restBookingController, "priceUtils", priceUtils);
        ReflectionTestUtils.setField(restBookingController, "feeCalculator", feeCalculator);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        faker = new Faker();
    }

    @Test
    public void postBookingFromWebsite_success() throws Exception {
        PostBookingRequestDTO bookingDTO = new PostBookingRequestDTO();
        bookingDTO.setEmail(faker.bothify("????##@gmail.com"));
        bookingDTO.setIs_round_trip("N");
        bookingDTO.setMobile(faker.phoneNumber());
        bookingDTO.setName(faker.name());
        bookingDTO.setNote(faker.name());

        when(priceCalculator.getPricePerKm(any())).thenReturn(10.0);
        when(promotionPlanRepository.findAll()).thenReturn(new ArrayList<>());
        when(distanceMatrixService.getDistance(anyString(), anyString())).thenReturn(80.0);
        when(priceUtils.calculateDriverFee(anyDouble(), anyDouble(), anyDouble())).thenReturn(0.0);
        when(feeCalculator.getDefaultFeePercentage()).thenReturn(15D);
        Booking savedBooking = new Booking();
        savedBooking.setId(1);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(savedBooking);

        mockMvc.perform(post("/booking/postBookingFromWebsite").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void getBookingDetail_success() throws Exception {
        Booking booking = new Booking();
        booking.setId(10);
        booking.setEmail("hopnv.1611@gmail.com");
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        mockMvc.perform(get("/booking/detail/DT00000010/hopnv.1611@gmail.com").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    public void calculateDistanceAndPrice_success() throws Exception {
        BookingCalculatePriceRequestDTO bookingCalculatePriceRequestDTO = new BookingCalculatePriceRequestDTO();
        bookingCalculatePriceRequestDTO.setCar_type(CarType.N4);
        bookingCalculatePriceRequestDTO.setFrom_location("Thạch Đà Mê Linh Hà Nội");
        bookingCalculatePriceRequestDTO.setTo_location("144 Xuân Thủy Cầu Giấy Hà Nội");
        bookingCalculatePriceRequestDTO.setIs_round_trip("N");
        bookingCalculatePriceRequestDTO.setDeparture_time(Calendar.getInstance().getTime());

        when(priceCalculator.getPricePerKm(any())).thenReturn(10.0);
        when(promotionPlanRepository.findAll()).thenReturn(new ArrayList<>());
        when(distanceMatrixService.getDistance(anyString(), anyString())).thenReturn(80.D * 1000);
        when(priceUtils.calculateDriverFee(anyDouble(), anyDouble(), anyDouble())).thenReturn(0.0);
        when(viewPriceRepository.saveAndFlush(any())).thenReturn(null);


        mockMvc.perform(post("/booking/calculateDistanceAndPrice").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapToJson(bookingCalculatePriceRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_distance").value(80))
                .andExpect(jsonPath("$.total_price").isNumber());
    }

}
