package com.bst.utility.components.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bst.utility.components.AuditService;
import com.bst.utility.testlib.SnapshotListener;

@ExtendWith(SpringExtension.class)
@TestExecutionListeners(listeners = SnapshotListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
@DataJpaTest
@ContextConfiguration(classes = { ComponentTestApplication.class })
public class AuditListenerTest {

	@TestConfiguration
	@EntityScan("com.bst.utility.components.test")
	@EnableJpaRepositories("com.bst.utility.components.test")
	static class AuditListenerTestConfiguration {
		@Bean
		public AuditService getAuditService() {
			return new AuditService();
		}

		@Bean
		public TestEntityListener getTestEntityListener() {
			return new TestEntityListener();
		}
	}

	private static Logger LOG = LoggerFactory.getLogger(AuditListenerTest.class);

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private TestEntityRepository testRepository;

	@Autowired
	private TestEntity2Repository test2Repository;

	@Autowired
	private TestEntityListener entityListener;

	@MockBean
	private TestEntityMockableListener mockableListener;

	@Test
	public void testListener() throws Exception {
		TestEntity entity = new TestEntity();
		entity.setField0("field0");
		entity.setField1("field1");

		Assertions.assertNotNull(this.testRepository);
		entity = this.testRepository.save(entity);

		this.entityManager.flush();

		Mockito.verify(this.mockableListener, Mockito.times(1)).prePersist(entity);
		Mockito.verify(this.mockableListener, Mockito.times(1)).postPersist(entity);

		entity = this.entityManager.refresh(entity);
		Mockito.verify(this.mockableListener, Mockito.times(1)).postLoad(entity);

		entity.setField0("field2");
		entity = this.testRepository.save(entity);

		this.entityManager.flush();

		Mockito.verify(this.mockableListener, Mockito.times(1)).preUpdate(entity);
		Mockito.verify(this.mockableListener, Mockito.times(1)).postUpdate(entity);

		this.testRepository.delete(entity);
		this.entityManager.flush();

		Mockito.verify(this.mockableListener, Mockito.times(1)).preRemove(entity);
		Mockito.verify(this.mockableListener, Mockito.times(1)).postRemove(entity);

	}

	@Test
	public void testListener2() throws Exception {
		TestEntity2 entity = new TestEntity2();
		entity.setField0("field0");
		entity.setField1("field1");

		Assertions.assertNotNull(this.testRepository);
		entity = this.test2Repository.save(entity);

		this.entityManager.flush();

		Mockito.verify(this.mockableListener, Mockito.times(0)).prePersist(entity);
		Mockito.verify(this.mockableListener, Mockito.times(0)).postPersist(entity);

		entity = this.entityManager.refresh(entity);
		Mockito.verify(this.mockableListener, Mockito.times(0)).postLoad(entity);

		entity.setField0("field2");
		entity = this.test2Repository.save(entity);

		this.entityManager.flush();

		Mockito.verify(this.mockableListener, Mockito.times(0)).preUpdate(entity);
		Mockito.verify(this.mockableListener, Mockito.times(0)).postUpdate(entity);

		this.test2Repository.delete(entity);
		this.entityManager.flush();

		Mockito.verify(this.mockableListener, Mockito.times(0)).preRemove(entity);
		Mockito.verify(this.mockableListener, Mockito.times(0)).postRemove(entity);

	}
}
