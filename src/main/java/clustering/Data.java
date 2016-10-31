package clustering;

import java.util.Date;

public class Data {
	
	private String indicator;
	private String layer;
	private String value;
	private Date time;
	private String service;
	private String method;
	
	public Data(String indicator, String layer, String value, Date time, String service, String method) {
		super();
		this.indicator = indicator;
		this.layer = layer;
		this.value = value;
		this.time = time;
		this.service = service;
		this.method = method;
	}

	public String getIndicator() {
		return indicator;
	}

	public String getLayer() {
		return layer;
	}

	public String getValue() {
		return value;
	}

	public String getService() {
		return service;
	}

	public String getMethod() {
		return method;
	}
	
	
	

}
