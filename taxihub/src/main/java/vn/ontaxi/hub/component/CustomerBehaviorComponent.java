package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Behavior;
import vn.ontaxi.common.jpa.repository.BehaviorRepository;

import java.util.List;

@Component
@Scope("view")
public class CustomerBehaviorComponent {
    @Autowired
    private BehaviorRepository behaviorRepository;
    private String name;

    public  List<Behavior> getBehaviors() {
        return behaviorRepository.findAll();
    }

    public void newBehavior() {
        Behavior currentNewBehavior = new Behavior();
        currentNewBehavior.setName(name);
        behaviorRepository.save(currentNewBehavior);

        name = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
