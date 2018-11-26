package com.bst.utility.testlib;

import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.springframework.test.context.TestContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;

public class SnapshotMatcher {
	private final static Logger LOGGER = Logger.getLogger(SnapshotMatcher.class.getName());
	private final AugmentedComparator comparator = new AugmentedComparator();
	private final ObjectMapper objectMapper = new ObjectMapper()
			.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
	private Object objectToMatch;
	private final JsonNode snapshots;
	private int snapshotSequence = -1;
	private boolean snapshotsUpdated = false;

	private String snapshotTestName;

	public SnapshotMatcher(final TestContext testContext) throws Exception {
		this.objectMapper.setSerializationInclusion(Include.NON_NULL);
		final String snapshotName = "src/test/java/" + testContext.getTestClass().getName().replaceAll("\\.", "/")
				+ ".snap";
		final File file = new File(snapshotName);
		this.snapshots = file.exists() ? this.objectMapper.readTree(file) : this.objectMapper.createObjectNode();
	}

	public void commitSnapshot(final TestContext testContext) throws Exception {
		if (this.snapshotsUpdated) {
			final String snapshotName = "src/test/java/" + testContext.getTestClass().getName().replaceAll("\\.", "/")
					+ ".snap";
			final File file = new File(snapshotName);

			final PrintWriter outputFile = new PrintWriter(file.getAbsolutePath());
			final String finalSnapshot = this.objectMapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(this.objectMapper.readTree(this.snapshots.toString()));
			outputFile.write(finalSnapshot);
			outputFile.close();
		}
	}

	public void commitTest(final TestContext testContext) {
	}

	public SnapshotMatcher matchObjectSnapshot(final Object obj) {
		this.objectToMatch = obj;
		return this;
	}

	public void startTest(final TestContext testContext) {
		this.snapshotSequence = -1;
		this.snapshotsUpdated = false;
		this.snapshotTestName = testContext.getTestMethod().getName();
	}

	public void toMatchSnapshot() throws Exception {
		++this.snapshotSequence;

		JsonNode objectTree = this.objectMapper.valueToTree(this.objectToMatch);
		if (this.objectToMatch == null) {
			objectTree = this.objectMapper.createObjectNode();
		}

		if (!this.snapshots.has(this.snapshotTestName)) {
			final JsonNode snapshotArray = this.objectMapper.createArrayNode();
			((ArrayNode) snapshotArray).add(objectTree);
			((ObjectNode) this.snapshots).set(this.snapshotTestName, snapshotArray);
			this.snapshotsUpdated = true;
			return;
		}

		JsonNode snapshotArray = this.snapshots.get(this.snapshotTestName);

		if (!snapshotArray.has(this.snapshotSequence)) {
			snapshotArray = ((ArrayNode) snapshotArray).add(objectTree);
			this.snapshotsUpdated = true;
			return;
		}

		final JsonNode compareTo = snapshotArray.get(this.snapshotSequence);
		if ((objectTree == compareTo) || !objectTree.equals(this.comparator, compareTo)) {
			this.snapshotsUpdated = false;
			SnapshotMatcher.LOGGER.severe("Snapshot mismatch");
			JsonNode patchNode = JsonDiff.asJson(compareTo, objectTree);
			SnapshotMatcher.LOGGER.severe(patchNode.toString());
			throw new Exception("Snapshot mismatch for " + this.snapshotTestName + " at " + this.snapshotSequence);
		}
	}
}
