package com.bst.utility.testlib;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.test.context.TestContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SnapshotMatcher {
	private JSONObject snapshots;
	private Object objectToMatch;
	private boolean snapshotsUpdated = false;
	private int snapshotSequence = -1;
	private String snapshotTestName;
	private ObjectMapper objectMapper = new ObjectMapper();

	SnapshotMatcher(TestContext testContext) throws Exception {
		String snapshotName = "src/test/java/" + testContext.getTestClass().getName().replaceAll("\\.", "/") + ".snap";
		File file = new File(snapshotName);

		if (file.exists()) {
			String content = new String(Files.readAllBytes(Paths.get(snapshotName)));
			snapshots = new JSONObject(content);
		} else {
			snapshots = new JSONObject();
		}
	}

	public void toMatchSnapshot() throws Exception {
		String jsonString = objectMapper.writeValueAsString(objectToMatch);
		
		++snapshotSequence;

		if (!snapshots.has(snapshotTestName)) {
			JSONArray snapshotArray = new JSONArray();
			snapshotArray.put(jsonString);
			snapshots.accumulate(snapshotTestName, snapshotArray);
			snapshotsUpdated = true;
			return;
		}

		JSONArray snapshotArray = snapshots.getJSONArray(snapshotTestName);
		if (snapshotArray.isNull(snapshotSequence)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				snapshots.accumulate(snapshotTestName, jsonObject);
			} catch (Exception ex) {
				snapshots.accumulate(snapshotTestName, jsonString);
			}
			snapshotsUpdated = true;
			return;
		}

		final String toMatch = snapshots.getJSONArray(snapshotTestName).get(snapshotSequence).toString();
		final JsonNode tree1 = objectMapper.readTree(toMatch);
		final JsonNode tree2 = objectMapper.readTree(jsonString);
		if (!tree1.equals(tree2)) {
			snapshotsUpdated = false;
			throw new Exception("Snapshot mismatch for " + snapshotTestName + " at " + snapshotSequence);
		}
	}

	public void commitTest(TestContext testContext) {
	}

	public void commitSnapshot(TestContext testContext) throws Exception {
		if (snapshotsUpdated) {
			String snapshotName = "src/test/java/" + testContext.getTestClass().getName().replaceAll("\\.", "/") + ".snap";
			File file = new File(snapshotName);
			
			PrintWriter outputFile = new PrintWriter(file.getAbsolutePath());
			String finalSnapshot = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
					objectMapper.readTree(snapshots.toString()));
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
