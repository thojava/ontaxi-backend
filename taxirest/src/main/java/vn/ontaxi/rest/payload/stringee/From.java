package vn.ontaxi.rest.payload.stringee;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class From{

	@JsonProperty("number")
	private String number;

	@JsonProperty("alias")
	private String alias;

	@JsonProperty("type")
	private String type;

	public void setNumber(String number){
		this.number = number;
	}

	public String getNumber(){
		return number;
	}

	public void setAlias(String alias){
		this.alias = alias;
	}

	public String getAlias(){
		return alias;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"From{" + 
			"number = '" + number + '\'' + 
			",alias = '" + alias + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}
