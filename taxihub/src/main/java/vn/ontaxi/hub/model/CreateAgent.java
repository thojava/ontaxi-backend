package vn.ontaxi.hub.model;

public class CreateAgent {

	public CreateAgent() {
	}

	public CreateAgent(String stringee_user_id, String name) {
		this.stringee_user_id = stringee_user_id;
		this.manual_status = "AVAILABLE";
		this.routing_type = 1;
		this.name = name;
	}

	private String stringee_user_id;
	private String manual_status;
	private int routing_type;
	private String name;

	public void setStringee_user_id(String stringee_user_id){
		this.stringee_user_id = stringee_user_id;
	}

	public String getStringee_user_id(){
		return stringee_user_id;
	}

	public void setManual_status(String manual_status){
		this.manual_status = manual_status;
	}

	public String getManual_status(){
		return manual_status;
	}

	public int getRouting_type() {
		return routing_type;
	}

	public void setRouting_type(int routing_type) {
		this.routing_type = routing_type;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}
