package vn.ontaxi.hub.component;

import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.AbstractEntity;
import vn.ontaxi.common.jpa.entity.CustomerGroup;
import vn.ontaxi.common.jpa.repository.CustomerGroupRepository;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@Component
@Scope("view")
public class CustomerGroupComponent {

    private final CustomerGroupRepository customerGroupRepository;
    private List<CustomerGroup> lstCustomerGroups;
    private CustomerGroup currentCustomerGroup;

    public CustomerGroupComponent(CustomerGroupRepository customerGroupRepository) {
        this.customerGroupRepository = customerGroupRepository;
    }

    @PostConstruct
    public void init() {
        lstCustomerGroups = customerGroupRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public void createNewGroup() {
        currentCustomerGroup = new CustomerGroup();
    }

    public void saveGroup() {
        customerGroupRepository.save(currentCustomerGroup);
        lstCustomerGroups = null;
    }

    public List<CustomerGroup> getLstCustomerGroups() {
        if (lstCustomerGroups == null)
            lstCustomerGroups = customerGroupRepository.findAll(new Sort(Sort.Direction.ASC, "name"));

        return lstCustomerGroups;
    }

    public void deleteCustomerGroup() {
        try {
            List<CustomerGroup> deleteGroups = lstCustomerGroups.stream().filter(AbstractEntity::isBeanSelected).collect(Collectors.toList());
            customerGroupRepository.delete(deleteGroups);
            lstCustomerGroups = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", String.format("Đã xóa %s nhóm", deleteGroups.size())));
        } catch (DataIntegrityViolationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Lỗi tham chiếu trong sql"));
        }
    }

    public void setLstCustomerGroups(List<CustomerGroup> lstCustomerGroups) {
        this.lstCustomerGroups = lstCustomerGroups;
    }

    public CustomerGroup getCurrentCustomerGroup() {
        return currentCustomerGroup;
    }

    public void setCurrentCustomerGroup(CustomerGroup currentCustomerGroup) {
        this.currentCustomerGroup = currentCustomerGroup;
    }
}
