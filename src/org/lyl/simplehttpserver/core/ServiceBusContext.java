package org.lyl.simplehttpserver.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lyl.simplehttpserver.Config;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.springframework.core.io.FileSystemResource;

public class ServiceBusContext {
	private static ExecutorService exec = null;
	private final BeanFactory context;
	static ServiceBusContext scontext = null;
	
	private ServiceBusContext(int poolsize, String configFileName) {
		if(exec == null)
			exec = Executors.newFixedThreadPool(poolsize);
		
		context = new XmlBeanFactory(new FileSystemResource(configFileName));
	}
	
	public static ServiceBusContext getServiceBusContext() {
		if(scontext == null) {
			Config conf = Config.getConfig();
			scontext = new ServiceBusContext(conf.getThreadPoolSize(), conf.getBeanConfigFileName());
		}
		return scontext;
	}
	
	public void runService(Service service){
		exec.execute(service);
	}
	
	void shutdownServices(){
//		if(!exec.isShutdown())
//			exec.shutdown();
	}
	
	static void clearContext() {
		scontext = null;
	}
	
	public BeanFactory getBeanFactory() {
		return context;
	}
	
	public Object getBean(String name) {
		return context.getBean(name);
	}
	
	public Service getService(String serviceName) {
		return (Service) context.getBean(serviceName, Service.class);
	}
}
