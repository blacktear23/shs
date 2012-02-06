package org.lyl.simplehttpserver.core;

import java.util.regex.Pattern;

public class FilterItem {
	public FilterItem() {}
	private Pattern pkey;
	private String processorBeanName;
	public String getProcessorBeanName() {
		return processorBeanName;
	}
	public void setProcessorBeanName(String processorBeanName) {
		this.processorBeanName = processorBeanName;
	}
	public void setKey(String key) {
		pkey = Pattern.compile(key);
	}
	
	public Pattern getPatternKey() {
		return pkey;
	}
}
