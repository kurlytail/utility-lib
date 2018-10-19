package com.bst.utility.testlib;

import java.util.Comparator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;

// This class is being used but is not doing anything, added here because it will be needed
// in the future
public class AugmentedComparator implements Comparator<JsonNode> {
	@Override
	public int compare(final JsonNode o1, final JsonNode o2) {
		if (o1.equals(o2)) {
			return 0;
		}
		if ((o1 instanceof NumericNode) && (o2 instanceof NumericNode)) {
			final double d1 = ((NumericNode) o1).asDouble();
			final double d2 = ((NumericNode) o2).asDouble();
			if (d1 == d2) { // strictly equals because it's integral value
				return 1;
			}
		}
		return 0;
	}
}
