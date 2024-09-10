package org.example.lib.reader;

import org.apache.pdfbox.text.TextPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Line {
	private Rect shape;
	private TextStyle textStyle;
	private String text;
	private final List<TextStyle> styles = new ArrayList<>();
	public Line(List<List<TextPosition>> textPositions) {

		calculateStyle();
	}

//	public void addIntoLine (List<TextPosition> textPositions){
//		handleAddBbox(shape);
//		this.listTextPositions.add(textPositions);
//		this.styles.add(style);
//	}

//	private void handleAddBbox(Rect bbox)  {
//		if(bbox == null) return;
//		if(shape == null) {
//			shape = bbox;
//			return;
//		}
//		try {
//			if(shape.getX1() > bbox.getX1()) {
//				shape.setX1(bbox.getX1());
//			}
//			if(shape.getX2() < bbox.getX2()) {
//				shape.setX2(bbox.getX2());
//			}
//			if(shape.getY1() > bbox.getY1()) {
//				shape.setY1(bbox.getY1());
//			}
//			if(shape.getY2() < bbox.getY2()) {
//				shape.setY2(bbox.getY2());
//			}
//		} catch (Exception ex) {
//			System.out.println("ERROR: " + bbox);
//		}
//	}

	private void calculateShape() {}
	private void calculateStyle(){}
	private void calculateTextString(){}

	private TextStyle findMostFrequentStyle() {
		Map<TextStyle, Integer> frequencyMap = new HashMap<>();
		TextStyle mostFrequentStyle = null;
//		int maxFrequency = 0;
//
//		for (int i = 0; i< styles.size(); i ++) {
//			var style = styles.get(i);
//			int length = texts.get(i).length();
//			int frequency = frequencyMap.getOrDefault(style, 0) + length;
//			frequencyMap.put(style, frequency);
//			if (frequency > maxFrequency) {
//				maxFrequency = frequency;
//				mostFrequentStyle = style;
//			}
//		}
		return mostFrequentStyle;
	}
	public TextStyle getTextStyle() {
		return textStyle;
	}

	public Rect getShape() {
		return this.shape;
	}

	public String getTextString() {
		return this.text;
	}

}
