package com.bst.utility.testlib;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class SnapshotListener extends AbstractTestExecutionListener {

	private static SnapshotMatcher matcher;

	public static SnapshotMatcher expect(final Object obj) {
		return SnapshotListener.matcher.matchObjectSnapshot(obj);
	}

	@Override
	public void afterTestClass(final TestContext testContext) throws Exception {
		SnapshotListener.matcher.commitSnapshot(testContext);
	}

	@Override
	public void afterTestMethod(final TestContext testContext) throws Exception {
		SnapshotListener.matcher.commitTest(testContext);
	}

	@Override
	public void beforeTestClass(final TestContext testContext) throws Exception {
		SnapshotListener.matcher = new SnapshotMatcher(testContext);
	}

	@Override
	public void beforeTestMethod(final TestContext testContext) throws Exception {
		SnapshotListener.matcher.startTest(testContext);
	}
}
