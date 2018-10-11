package com.bst.utility.components.test;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.bst.utility.components.AuditListener;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@EntityListeners(AuditListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String field0;
	private String field1;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final TestEntity other = (TestEntity) obj;
		if (this.field0 == null) {
			if (other.field0 != null) {
				return false;
			}
		} else if (!this.field0.equals(other.field0)) {
			return false;
		}
		if (this.field1 == null) {
			if (other.field1 != null) {
				return false;
			}
		} else if (!this.field1.equals(other.field1)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public String getField0() {
		return this.field0;
	}

	public String getField1() {
		return this.field1;
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.field0 == null) ? 0 : this.field0.hashCode());
		result = prime * result + ((this.field1 == null) ? 0 : this.field1.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public void setField0(final String field0) {
		this.field0 = field0;
	}

	public void setField1(final String field1) {
		this.field1 = field1;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "TestEntity [id=" + this.id + ", field0=" + this.field0 + ", field1=" + this.field1 + "]";
	}
}
