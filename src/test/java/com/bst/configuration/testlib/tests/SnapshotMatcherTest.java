package com.bst.configuration.testlib.tests;

import static com.bst.utility.testlib.SnapshotListener.expect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bst.utility.testlib.SnapshotListener;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = SnapshotListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class SnapshotMatcherTest {
	
	public class TestClass {
		public Long value = (long) 10;
	}
	
	@Mock
	private TestContext testContext;
	
	@Test
	public void useLongTest() throws Exception {
		expect(new TestClass()).toMatchSnapshot();
	}

}
