package org.example.lib.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Paragraph {
	private List<TextStyle> styles = new ArrayList<>();
	private List<String> texts = new ArrayList<>();
	private List<Line> lines = new ArrayList<>();
	private Rect shape;
	private TextStyle textStyle;

	public Paragraph() {
	}

	public Paragraph(List<Line> lines) {
		this.lines = lines;
		this.texts = lines.stream()
				.map(Line::getTextString).collect(Collectors.toList());
		this.styles = lines.stream()
				.map(Line::getTextStyle).collect(Collectors.toList());
		this.shape = getBoundingRect(lines.stream()
				.map(Line::getShape).toList());
		this.findMostFrequentStyle();
	}

	public static Rect getBoundingRect(List<Rect> rects) {
		if (rects == null || rects.isEmpty()) {
			throw new IllegalArgumentException("List of Rects must not be null or empty");
		}

		float minX1 = rects.get(0).getX1();
		float minY1 = rects.get(0).getY1();
		float maxX2 = rects.get(0).getX2();
		float maxY2 = rects.get(0).getY2();

		for (Rect rect : rects) {
			if (rect.getX1() < minX1) {
				minX1 = rect.getX1();
			}
			if (rect.getY1() < minY1) {
				minY1 = rect.getY1();
			}
			if (rect.getX2() > maxX2) {
				maxX2 = rect.getX2();
			}
			if (rect.getY2() > maxY2) {
				maxY2 = rect.getY2();
			}
		}

		// Tạo một Rect mới đại diện cho Rect bao quanh
		return new Rect(minX1, minY1, maxX2, maxY2);
	}

	public void addIntoParagraph(Line line) {
		handleAddBbox(line.getShape());
		texts.add(line.getTextString());
		this.styles.add(line.getTextStyle());
		lines.add(line);
		System.out.println(lines.get(0).getShape().getY1());
	}

	private void handleAddBbox(Rect bbox)  {
		if(bbox == null) return;
		if(shape == null) {
			shape = new Rect(bbox.getX1(), bbox.getY1(), bbox.getX2(),bbox.getY2());
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

	public List<Line> getLines () {
		return this.lines;
	}

	public void endParagraph () {
		findMostFrequentStyle();
	}

	private void findMostFrequentStyle() {
		Map<TextStyle, Integer> frequencyMap = new HashMap<>();
		TextStyle mostFrequentStyle = styles.get(0);
		int maxFrequency = 0;

		for (TextStyle style : styles) {
			int frequency = frequencyMap.getOrDefault(style, 0) + 1;
			frequencyMap.put(style, frequency);
			if (frequency > maxFrequency) {
				maxFrequency = frequency;
				mostFrequentStyle = style;
			}
		}
		this.textStyle =  mostFrequentStyle;
	}

	public String getTextString() {
		return String.join(" ", this.texts);
	}


	public Rect getShape() {
		return  this.shape;
	}

	public TextStyle getStyle() {
		return this.textStyle;
	}


}
