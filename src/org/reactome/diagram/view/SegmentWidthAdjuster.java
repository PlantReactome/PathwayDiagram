package org.reactome.diagram.view;

public class SegmentWidthAdjuster {
	private final double MIN_TOTAL_SEGMENT_PERCENTAGE = 0.1;
	private static SegmentWidthAdjuster segmentWidthAdjuster;
	
	private SegmentWidthAdjuster() {
		
	}
	
	public static SegmentWidthAdjuster getInstance() {
		if (segmentWidthAdjuster == null)
			segmentWidthAdjuster = new SegmentWidthAdjuster();
		return segmentWidthAdjuster;
	}
	
	public double getVisibleWidth(double nodeWidth, double segmentWidth, int numberOfSegments) {
		return getScaleFactor(nodeWidth, segmentWidth * numberOfSegments) * segmentWidth; 
	}
	
	private double getScaleFactor(double nodeWidth, double totalSegmentWidth) {
		return Math.max(MIN_TOTAL_SEGMENT_PERCENTAGE * nodeWidth, totalSegmentWidth) / totalSegmentWidth;
	}
}
