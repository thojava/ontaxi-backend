package vn.ontaxi.rest.payload.stringee;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class SCCO{

	@JsonProperty("action")
	private String action;

	@JsonProperty("from")
	private From from;

	@JsonProperty("customData")
	private String customData;

	@JsonProperty("to")
	private To to;

	public void setAction(String action){
		this.action = action;
	}

	public String getAction(){
		return action;
	}

	public void setFrom(From from){
		this.from = from;
	}

	public From getFrom(){
		return from;
	}

	public void setCustomData(String customData){
		this.customData = customData;
	}

	public String getCustomData(){
		return customData;
	}

	public void setTo(To to){
		this.to = to;
	}

	public To getTo(){
		return to;
	}

	@Override
 	public String toString(){
		return 
			"SCCO{" + 
			"action = '" + action + '\'' + 
			",from = '" + from + '\'' + 
			",customData = '" + customData + '\'' + 
			",to = '" + to + '\'' + 
			"}";
		}
}
