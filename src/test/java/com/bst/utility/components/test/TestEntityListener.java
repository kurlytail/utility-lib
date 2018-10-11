package com.bst.utility.components.test;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestEntityListener {
	@Autowired
	private TestEntityMockableListener mockableListener;
	
	@PostLoad
	public void postLoad(final TestEntity object) throws Exception {
		mockableListener.postLoad(object);
	}

	@PostPersist
	public void postPersist(final TestEntity object) throws Exception {
		mockableListener.postPersist(object);
	}

	@PostRemove
	public void postRemove(final TestEntity object) throws Exception {
		mockableListener.postRemove(object);
	}

	@PostUpdate
	public void postUpdate(final TestEntity object) throws Exception {
		mockableListener.postUpdate(object);
	}

	@PrePersist
	public void prePersist(final TestEntity object) throws Exception {
		mockableListener.prePersist(object);
	}

	@PreRemove
	public void preRemove(final TestEntity object) throws Exception {
		mockableListener.preRemove(object);
	}

	@PreUpdate
	public void preUpdate(final TestEntity object) throws Exception {
		mockableListener.preUpdate(object);
	}
}
