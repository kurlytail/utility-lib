package com.bst.utility.components;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.springframework.beans.factory.annotation.Autowired;

public class AuditListener {

	@Autowired
	private AuditService auditService;

	private void checkAuditService() throws Exception {
		if (this.auditService == null) {
			AuditService.autowire(this);
			if (this.auditService == null) {
				throw new Exception("Audit service not initialized");
			}
		}
	}

	@PostLoad
	public void postLoad(final Object object) throws Exception {
		this.checkAuditService();
		this.auditService.postLoad(object);
	}

	@PostPersist
	public void postPersist(final Object object) throws Exception {
		this.checkAuditService();
		this.auditService.postPersist(object);
	}

	@PostRemove
	public void postRemove(final Object object) throws Exception {
		this.checkAuditService();
		this.auditService.postRemove(object);
	}

	@PostUpdate
	public void postUpdate(final Object object) throws Exception {
		this.checkAuditService();
		this.auditService.postUpdate(object);
	}

	@PrePersist
	public void prePersist(final Object object) throws Exception {
		this.checkAuditService();
		this.auditService.prePersist(object);
	}

	@PreRemove
	public void preRemove(final Object object) throws Exception {
		this.checkAuditService();
		this.auditService.preRemove(object);
	}

	@PreUpdate
	public void preUpdate(final Object object) throws Exception {
		this.checkAuditService();
		this.auditService.preUpdate(object);
	}

}
