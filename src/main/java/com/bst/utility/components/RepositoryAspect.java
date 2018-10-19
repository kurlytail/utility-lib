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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Aspect
public class RepositoryAspect {

	private final Map<Method, Object> postLoadMethods = new HashMap<>();

	private final Map<Method, Object> postPersistMethods = new HashMap<>();

	private final Map<Method, Object> postRemoveMethods = new HashMap<>();

	private final Map<Method, Object> postUpdateMethods = new HashMap<>();

	private final Map<Method, Object> prePersistMethods = new HashMap<>();

	private final Map<Method, Object> preRemoveMethods = new HashMap<>();
	private final Map<Method, Object> preUpdateMethods = new HashMap<>();

	@EventListener
	public void handleContextRefresh(final ContextRefreshedEvent event) {
		final String[] beanNames = event.getApplicationContext().getBeanDefinitionNames();
		for (final String beanName : beanNames) {
			final Object bean = event.getApplicationContext().getBean(beanName);
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
		if ((typeParameters.length == 1) && typeParameters[0].isInstance(object)) {
			method.invoke(bean, object);
		}
	}

	@Pointcut("execution(* javax.persistence.EntityManager.merge(..)) && args(entity)")
	public void mergeMethod(final Object entity) {
	}

	@Pointcut("execution(* javax.persistence.EntityManager.persist(..)) && args(entity)")
	public void persistMethod(final Object entity) {
	}

	@After("persistMethod(entity)")
	public void postPersist(final JoinPoint jp, final Object entity) throws Throwable {
		for (final Method method : this.postPersistMethods.keySet()) {
			this.invokeMethod(method, this.postPersistMethods.get(method), entity);
		}
	}

	@After("removeMethod(entity)")
	public void postRemove(final JoinPoint jp, final Object entity) throws Throwable {
		for (final Method method : this.postRemoveMethods.keySet()) {
			this.invokeMethod(method, this.postRemoveMethods.get(method), entity);
		}
	}

	@After("mergeMethod(entity)")
	public void postUpdate(final JoinPoint jp, final Object entity) throws Throwable {
		for (final Method method : this.postUpdateMethods.keySet()) {
			this.invokeMethod(method, this.postUpdateMethods.get(method), entity);
		}
	}

	@Before("persistMethod(entity)")
	public void prePersist(final JoinPoint jp, final Object entity) throws Throwable {
		for (final Method method : this.prePersistMethods.keySet()) {
			this.invokeMethod(method, this.prePersistMethods.get(method), entity);
		}
	}

	@Before("removeMethod(entity)")
	public void preRemove(final JoinPoint jp, final Object entity) throws Throwable {
		for (final Method method : this.preRemoveMethods.keySet()) {
			this.invokeMethod(method, this.preRemoveMethods.get(method), entity);
		}
	}

	@Before("mergeMethod(entity)")
	public void preUpdate(final JoinPoint jp, final Object entity) throws Throwable {
		for (final Method method : this.preUpdateMethods.keySet()) {
			this.invokeMethod(method, this.preUpdateMethods.get(method), entity);
		}
	}

	@Pointcut("execution(* javax.persistence.EntityManager.remove(..)) && args(entity)")
	public void removeMethod(final Object entity) {
	}
}
