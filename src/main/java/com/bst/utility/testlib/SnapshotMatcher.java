package com.bst.utility.testlib;

import java.io.File;
import java.io.PrintWriter;

import org.springframework.test.context.TestContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SnapshotMatcher {
	private JsonNode snapshots;
	private Object objectToMatch;
	private boolean snapshotsUpdated = false;
	private int snapshotSequence = -1;
	private String snapshotTestName;
	private ObjectMapper objectMapper = new ObjectMapper();

	SnapshotMatcher(TestContext testContext) throws Exception {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		String snapshotName = "src/test/java/" + testContext.getTestClass().getName().replaceAll("\\.", "/") + ".snap";
		File file = new File(snapshotName);
		snapshots = file.exists() ? objectMapper.readTree(file) : objectMapper.createObjectNode();
	}

	public void toMatchSnapshot() throws Exception {
		++snapshotSequence;

		JsonNode objectTree = objectMapper.valueToTree(objectToMatch);
		if (objectToMatch == null) {
			objectTree = objectMapper.createObjectNode();
		}

		if (!snapshots.has(snapshotTestName)) {
			JsonNode snapshotArray = objectMapper.createArrayNode();
			((ArrayNode) snapshotArray).add(objectTree);
			((ObjectNode) snapshots).set(snapshotTestName, snapshotArray);
			snapshotsUpdated = true;
			return;
		}

		JsonNode snapshotArray = snapshots.get(snapshotTestName);

		if (!snapshotArray.has(snapshotSequence)) {
			snapshotArray = ((ArrayNode) snapshotArray).add(objectTree);
			snapshotsUpdated = true;
			return;
		}

		JsonNode compareTo = snapshotArray.get(snapshotSequence);
		if (objectTree == compareTo || !objectTree.toString().equals(compareTo.toString())) {
			snapshotsUpdated = false;
			throw new Exception("Snapshot mismatch for " + snapshotTestName + " at " + snapshotSequence);
		}
	}

	public void commitTest(TestContext testContext) {
	}

	public void commitSnapshot(TestContext testContext) throws Exception {
		if (snapshotsUpdated) {
			String snapshotName = "src/test/java/" + testContext.getTestClass().getName().replaceAll("\\.", "/")
					+ ".snap";
			File file = new File(snapshotName);

			PrintWriter outputFile = new PrintWriter(file.getAbsolutePath());
			String finalSnapshot = objectMapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(objectMapper.readTree(snapshots.toString()));
			outputFile.write(finalSnapshot);
			outputFile.close();
		}
	}

	public SnapshotMatcher matchObjectSnapshot(Object obj) {
		objectToMatch = obj;
		return this;
	}

	public void startTest(TestContext testContext) {
		snapshotSequence = -1;
		snapshotsUpdated = false;
		snapshotTestName = testContext.getTestMethod().getName();
	}
}
