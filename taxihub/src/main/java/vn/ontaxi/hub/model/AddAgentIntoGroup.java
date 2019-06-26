package vn.ontaxi.hub.model;

public class AddAgentIntoGroup {

    private String agent_id;
    private String group_id;

    public AddAgentIntoGroup() {
    }

    public AddAgentIntoGroup(String agent_id, String group_id) {
        this.agent_id = agent_id;
        this.group_id = group_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
