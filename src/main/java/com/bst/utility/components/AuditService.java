package com.bst.utility.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuditService implements ApplicationContextAware {

	public static ApplicationContext applicationContext = null;

	public static void autowire(final Object classToAutowire) {
		AuditService.applicationContext.getAutowireCapableBeanFactory().autowireBean(classToAutowire);
	}

	private final Map<Method, Object> prePersistMethods = new HashMap<>();
	private final Map<Method, Object> postPersistMethods = new HashMap<>();
	private final Map<Method, Object> preUpdateMethods = new HashMap<>();
	private final Map<Method, Object> postUpdateMethods = new HashMap<>();
	private final Map<Method, Object> preRemoveMethods = new HashMap<>();
	private final Map<Method, Object> postRemoveMethods = new HashMap<>();

	private final Map<Method, Object> postLoadMethods = new HashMap<>();

	public ApplicationContext getApplicationContext() {
		return AuditService.applicationContext;
	}

	@EventListener
	public void handleContextRefresh(final ContextRefreshedEvent event) {
		final String[] beanNames = AuditService.applicationContext.getBeanDefinitionNames();
		for (final String beanName : beanNames) {
			final Object bean = AuditService.applicationContext.getBean(beanName);
			for (final Method method : bean.getClass().getMethods()) {
				if (method.isAnnotationPresent(PrePersist.class)) {
					this.prePersistMethods.put(method, bean);
				}
				if (method.isAnnotationPresent(PostPersist.class)) {
					this.postPersistMethods.put(method, bean);
				}
				if (method.isAnnotationPresent(PreRemove.class)) {
					this.preRemoveMethods.put(method, bean);
				}
				if (method.isAnnotationPresent(PostRemove.class)) {
					this.postRemoveMethods.put(method, bean);
				}
				if (method.isAnnotationPresent(PreUpdate.class)) {
					this.preUpdateMethods.put(method, bean);
				}
				if (method.isAnnotationPresent(PostUpdate.class)) {
					this.postUpdateMethods.put(method, bean);
				}
				if (method.isAnnotationPresent(PostLoad.class)) {
					this.postLoadMethods.put(method, bean);
				}
			}
		}
	}

	private void invokeMethod(final Method method, final Object bean, final Object object)
			throws IllegalAccessException, InvocationTargetException {
		final Class<?>[] typeParameters = method.getParameterTypes();
		if (typeParameters.length == 1 && typeParameters[0].isInstance(object)) {
			method.invoke(bean, object);
		}
	}

	public void postLoad(final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : this.postLoadMethods.keySet()) {
			this.invokeMethod(method, this.postLoadMethods.get(method), object);
		}
	}

	public void postPersist(final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : this.postPersistMethods.keySet()) {
			this.invokeMethod(method, this.postPersistMethods.get(method), object);
		}

	}

	public void postRemove(final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : this.postRemoveMethods.keySet()) {
			this.invokeMethod(method, this.postRemoveMethods.get(method), object);
		}

	}

	public void postUpdate(final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : this.postUpdateMethods.keySet()) {
			this.invokeMethod(method, this.postUpdateMethods.get(method), object);
		}

	}

	public void prePersist(final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : this.prePersistMethods.keySet()) {
			this.invokeMethod(method, this.prePersistMethods.get(method), object);
		}

	}

	public void preRemove(final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : this.preRemoveMethods.keySet()) {
			this.invokeMethod(method, this.preRemoveMethods.get(method), object);
		}
	}

	public void preUpdate(final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : this.preUpdateMethods.keySet()) {
			this.invokeMethod(method, this.preUpdateMethods.get(method), object);
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext ac) throws BeansException {
		AuditService.applicationContext = ac;
	}

}
