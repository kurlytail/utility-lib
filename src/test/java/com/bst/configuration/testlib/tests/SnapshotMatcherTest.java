package com.bst.configuration.testlib.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bst.utility.testlib.SnapshotListener;

@ExtendWith(SpringExtension.class)
@TestExecutionListeners(listeners = SnapshotListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class SnapshotMatcherTest {

	public class TestClass {
		public Long value = (long) 20;
	}

	@Mock
	private TestContext testContext;

	@Test
	public void testUseLong() throws Exception {
		SnapshotListener.expect(new TestClass()).toMatchSnapshot();
	}

}
