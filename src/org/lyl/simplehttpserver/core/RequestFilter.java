package org.lyl.simplehttpserver.core;

import java.util.List;

import org.lyl.simplehttpserver.processor.RequestProcessor;

public class RequestFilter {
	private List<FilterItem> filterItems;
	private ServiceBusContext scontext = ServiceBusContext.getServiceBusContext();

	public List<FilterItem> getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(List<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	public RequestProcessor getRequestProcessor(String reqpath) {
		RequestProcessor reqp = null;
		for(FilterItem item : filterItems) {
			if(item.getPatternKey().matcher(reqpath).matches()) {
				reqp = (RequestProcessor)scontext.getBean(item.getProcessorBeanName());
				break;
			}
		}
		return reqp;
	}
}
