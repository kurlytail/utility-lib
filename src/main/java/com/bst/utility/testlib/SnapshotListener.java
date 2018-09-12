package com.bst.utility.testlib;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class SnapshotListener extends AbstractTestExecutionListener {

	private static SnapshotMatcher matcher;

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		matcher = new SnapshotMatcher(testContext);
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		matcher.startTest(testContext);
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		matcher.commitTest(testContext);
	}

	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		matcher.commitSnapshot(testContext);
	}

	public static SnapshotMatcher expect(Object obj) {
		return matcher.matchObjectSnapshot(obj);
	}
}
