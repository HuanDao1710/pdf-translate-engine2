package org.example.lib.reader;

import org.apache.pdfbox.text.TextPosition;

import java.util.ArrayList;
import java.util.List;

public class LineBuilder {
	private final List<List<TextPosition>> listTextPositions = new ArrayList<>();
	private Rect shape;

	public void addTextPosition (List<TextPosition> textPositions, Rect shape){
		handleAddBox(shape);
		this.listTextPositions.add(textPositions);
	}

	public LineBuilder() {}

	private void handleAddBox(Rect bbox)  {
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

	public Rect getShape() {
		return this.shape;
	}

	public List<Line> extractLine() {
		return new ArrayList<>();
	}
}
