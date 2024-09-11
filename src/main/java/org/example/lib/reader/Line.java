package org.example.lib.reader;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.TextPosition;
import org.example.lib.utils.Pair;
import org.tensorflow.ndarray.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Line {
	private  Rect shape;
	private TextStyle textStyle;
	private  String text;
	public Line(String text, Rect shape, TextStyle textStyle) {
		this.text = text;
		this.shape = shape;
		this.textStyle = textStyle;
	}

	public Line(List<List<TextPosition>> listTextPositions, Map<Pair<Float, Float>, PDColor> colorMap) {
		StringBuilder stringBuilder = new StringBuilder();
		List<TextStyle> textStyles = new ArrayList<>();
		for(var textPositions : listTextPositions) {
			for(var pos : textPositions) {
				stringBuilder.append(pos.getUnicode());
				float x = pos.getTextMatrix().getTranslateX();
				float y = pos.getTextMatrix().getTranslateY();
				var style = new TextStyle(
						pos.getYScale(),
						pos.getFont(),
						pos.getRotation(),
						pos.getDir(),
						colorMap.get(new Pair<>(x, y))
				);
				textStyles.add(style);
			}
			stringBuilder.append(" ");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		this.text = stringBuilder.toString();
		this.textStyle = findMostFrequentStyle(textStyles);

		var firsPos = listTextPositions.get(0).get(0);
		float x1 = firsPos.getTextMatrix().getTranslateX();
		float y1 = firsPos.getTextMatrix().getTranslateY();
		var lastList =  listTextPositions.get(listTextPositions.size() - 1);
		var lastPos = lastList.get(lastList.size() - 1);
		float x2 = lastPos.getTextMatrix().getTranslateX() + lastPos.getWidth();
		float y2 = y1 + firsPos.getYScale();
		this.shape = new Rect(x1, y1, x2, y2);
	}

	public Line(List<List<TextPosition>> listTextPositions, Rect shape, Map<Pair<Float, Float>, PDColor> colorMap ) {
		StringBuilder stringBuilder = new StringBuilder();
		List<TextStyle> textStyles = new ArrayList<>();
		for(var textPositions : listTextPositions) {
			for(var pos : textPositions) {
				stringBuilder.append(pos.getUnicode());
				float x = pos.getTextMatrix().getTranslateX();
				float y = pos.getTextMatrix().getTranslateY();
				var style = new TextStyle(
						pos.getYScale(),
						pos.getFont(),
						pos.getRotation(),
						pos.getDir(),
						colorMap.get(new Pair<>(x, y))
				);
				textStyles.add(style);
			}
			stringBuilder.append(" ");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		this.text = stringBuilder.toString();
		this.textStyle = findMostFrequentStyle(textStyles);
		this.shape = shape;
	}

	public static TextStyle findMostFrequentStyle(List<TextStyle> styles) {
		Map<TextStyle, Integer> frequencyMap = new HashMap<>();
		TextStyle mostFrequentStyle = null;
		int maxFrequency = 0;

		for (int i = 0; i< styles.size(); i ++) {
			var style = styles.get(i);
			int length = 1;
			int frequency = frequencyMap.getOrDefault(style, 0) + length;
			frequencyMap.put(style, frequency);
			if (frequency > maxFrequency) {
				maxFrequency = frequency;
				mostFrequentStyle = style;
			}
		}
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
