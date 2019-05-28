package vn.ontaxi.hub.component;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AutocompletePrediction;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.model.map.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.ontaxi.common.jpa.entity.Address;
import vn.ontaxi.common.jpa.entity.Behavior;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.repository.AddressRepository;
import vn.ontaxi.common.jpa.repository.BehaviorRepository;
import vn.ontaxi.common.jpa.repository.CustomerBehaviorRepository;
import vn.ontaxi.common.jpa.repository.CustomerRepository;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ManagedBean
@Component
@Scope("view")
public class CustomerDetailComponent implements Serializable {

    private Customer currentCustomer;
    private List<MapModel> lstGeoModels;
    private List<Behavior> lstBehaviors;
    private final Environment env;
    private String GOOGLE_MAP_PLACE_API_KEY;

    private final CustomerRepository customerRepository;
    private final BehaviorRepository behaviorRepository;
    private final CustomerBehaviorRepository customerBehaviorRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CustomerDetailComponent(Environment env, CustomerRepository customerRepository, BehaviorRepository behaviorRepository, CustomerBehaviorRepository customerBehaviorRepository, AddressRepository addressRepository) {
        this.env = env;
        this.customerRepository = customerRepository;
        this.behaviorRepository = behaviorRepository;
        this.customerBehaviorRepository = customerBehaviorRepository;
        this.addressRepository = addressRepository;
    }

    @PostConstruct
    public void init() {
        lstBehaviors = behaviorRepository.findAll(new Sort(Sort.Direction.ASC, "name"));

        GOOGLE_MAP_PLACE_API_KEY = env.getProperty("google_map_place_recommendation_key");

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String customerId = params.get("id");
        if (StringUtils.isNotEmpty(customerId) && NumberUtils.isDigits(customerId)) {
            currentCustomer = customerRepository.findOne(Long.parseLong(customerId));
        }
    }

    private Address getAddressByName(String address) {
        if (!CollectionUtils.isEmpty(currentCustomer.getAddresses())) {
            for (Address ads : currentCustomer.getAddresses())
                if (address.equalsIgnoreCase(ads.getAddress()))
                    return ads;
        }

        return null;
    }

    public List<String> recommendPlaces(String address) throws InterruptedException, ApiException, IOException {

        GeoApiContext geoApiContext = new GeoApiContext.Builder().apiKey(GOOGLE_MAP_PLACE_API_KEY)
                .build();

        AutocompletePrediction[] autocompletePredictions = PlacesApi.placeAutocomplete(geoApiContext, address, new PlaceAutocompleteRequest.SessionToken()).language("vi_VN").await();
        return Stream.of(autocompletePredictions).map(autocompletePrediction -> autocompletePrediction.description).collect(Collectors.toList());
    }

    public void findLocation(GeocodeEvent event) {
        Address currentAddress = getAddressByName(event.getQuery().trim());
        if (currentAddress == null)
            return;
        List<GeocodeResult> results = event.getResults();
        if (results != null && !results.isEmpty()) {
            GeocodeResult result = results.get(0);
            currentAddress.setLat(result.getLatLng().getLat());
            currentAddress.setLng(result.getLatLng().getLng());

            int indexOf = currentCustomer.getAddresses().indexOf(currentAddress);
            lstGeoModels.get(indexOf).getMarkers().clear();
            lstGeoModels.get(indexOf).addOverlay(new Marker(result.getLatLng(), result.getAddress()));
            lstGeoModels.get(indexOf).getMarkers().get(0).setDraggable(true);
        }
    }

    public void onMarkerDrag(MarkerDragEvent event) {

        for (int i = 0; i < lstGeoModels.size(); i++) {
            if (lstGeoModels.get(i).getMarkers().get(0) == event.getMarker()) {
                Address address = currentCustomer.getAddresses().get(i);
                address.setLat(event.getMarker().getLatlng().getLat());
                address.setLng(event.getMarker().getLatlng().getLng());

                if (StringUtils.isEmpty(address.getAddress()))
                    address.setAddress(event.getMarker().getTitle());
                break;
            }
        }

    }

    public void deleteAddress(Address address) {
        addressRepository.delete(address);
        currentCustomer.getAddresses().remove(address);
        lstGeoModels = null;
    }

    @Transactional
    public String saveCustomerInfo() {

        List<Customer> findUserByPhones = customerRepository.findByPhone(currentCustomer.getPhone());
        if (CollectionUtils.isNotEmpty(findUserByPhones)) {
            if (!(currentCustomer.getId() != null && findUserByPhones.get(0).getId().equals(currentCustomer.getId()))) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", String.format("Số điện thoại %s đã tồn tại trong hệ thống", currentCustomer.getPhone())));
                return null;
            }
        }

        Customer savedCustomer = customerRepository.save(currentCustomer);
        for (Address address : currentCustomer.getAddresses()) {
            if (address.getCustomer() == null)
                address.setCustomer(savedCustomer);
        }

        return "customer_summary.jsf?faces-redirect=true";
    }

    public boolean canAddNewAddress() {
        return CollectionUtils.isEmpty(currentCustomer.getAddresses())
                || currentCustomer.getAddresses().stream().allMatch(address -> address.getAddressType() != null);
    }

    public void addNewAddress() {
        Address newAddress = new Address();
        newAddress.setCustomer(currentCustomer);
        currentCustomer.getAddresses().add(newAddress);

        addressRepository.save(newAddress);
        lstGeoModels = null;
    }

    @Transactional
    public void saveBehaviours() {
        customerBehaviorRepository.deleteByCustomerId(this.currentCustomer.getId());
        customerBehaviorRepository.save(this.currentCustomer.getCustomerBehaviors());
    }

    public List<Behavior> getLstBehaviors() {
        return lstBehaviors;
    }

    public void setLstBehaviors(List<Behavior> lstBehaviors) {
        this.lstBehaviors = lstBehaviors;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public List<MapModel> getLstGeoModels() {
        if (lstGeoModels == null) {
            lstGeoModels = new ArrayList<>();
            currentCustomer.getAddresses().forEach(address -> {
                MapModel defaultMapModel = new DefaultMapModel();
                Marker marker = new Marker(new LatLng(address.getLat(), address.getLng()), address.getAddress());
                marker.setDraggable(true);
                defaultMapModel.addOverlay(marker);
                lstGeoModels.add(defaultMapModel);
            });
        }
        return lstGeoModels;
    }
}
