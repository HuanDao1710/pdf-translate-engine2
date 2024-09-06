package org.example.lib.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Line {
	private Rect shape;
	private TextStyle textStyle;
	private float height;
	private final List<String> texts = new ArrayList<>();
	private final List<TextStyle> styles = new ArrayList<>();
	public Line(Rect shape) {
		this.shape = shape;
//		this.height = height;
	}

	public void addIntoLine (String text, Rect shape, TextStyle style){
		handleAddBbox(shape);
		texts.add(text);
		this.styles.add(style);
	}

	private void handleAddBbox(Rect bbox)  {
		if(bbox == null) return;
		if(shape == null) {
			shape = bbox;
			return;
		}
		try {
			if(shape.getX1() > bbox.getX1()) {
				shape.setX1(bbox.getX1());
			}
			if(shape.getX2() < bbox.getX2()) {
				shape.setX2(bbox.getX2());
			}
			if(shape.getY1() > bbox.getY1()) {
				shape.setY1(bbox.getY1());
			}
			if(shape.getY2() < bbox.getY2()) {
				shape.setY2(bbox.getY2());
			}
		} catch (Exception ex) {
			System.out.println("ERROR: " + bbox);
		}
	}

	private TextStyle findMostFrequentStyle() {
		Map<TextStyle, Integer> frequencyMap = new HashMap<>();
		TextStyle mostFrequentStyle = null;
		int maxFrequency = 0;

		for (int i = 0; i< styles.size(); i ++) {
			var style = styles.get(i);
			int length = texts.get(i).length();
			int frequency = frequencyMap.getOrDefault(style, 0) + length;
			frequencyMap.put(style, frequency);
			if (frequency > maxFrequency) {
				maxFrequency = frequency;
				mostFrequentStyle = style;
			}
		}
		return mostFrequentStyle;
	}

	public void endLine() {
		this.textStyle = findMostFrequentStyle();
	}

	public TextStyle getTextStyle() {
		return textStyle;
	}

	public Rect getShape() {
		return this.shape;
	}

	public String getTextString() {
		return String.join(" ", this.texts);
	}

	public int getSizeText() {
		return this.texts.size();
	}


}
