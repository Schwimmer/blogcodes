package com.iclick.spark.buzzads.model;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JSlotInfo implements Serializable {

	private static final long serialVersionUID = -6034659347477968157L;
	
	public Map<String, Double> slotPrice = new HashMap<String, Double>();

	public Map<String, Double> getSlotPrice() {
		return slotPrice;
	}

	public void setSlotPrice(Map<String, Double> slotPrice) {
		this.slotPrice = slotPrice;
	}

	@Override
	public String toString() {
		return "JSlotInfo [slotPrice=" + slotPrice + "]";
	}

}