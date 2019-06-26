package vn.ontaxi.hub.model;

public class CreateAgentResponse{
	private String R;
	private String agentID;
	private String project;
	private String message;

	public void setR(String R){
		this.R = R;
	}

	public String getR(){
		return R;
	}

	public void setAgentID(String agentID){
		this.agentID = agentID;
	}

	public String getAgentID(){
		return agentID;
	}

	public void setProject(String project){
		this.project = project;
	}

	public String getProject(){
		return project;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public boolean isCreated() {
		return "0".equalsIgnoreCase(R) || "6".equalsIgnoreCase(R); // 6 = exist
	}

	@Override
 	public String toString(){
		return 
			"CreateAgentResponse{" + 
			"r = '" + R + '\'' + 
			",agentID = '" + agentID + '\'' + 
			",project = '" + project + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}
