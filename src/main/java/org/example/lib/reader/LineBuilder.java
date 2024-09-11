package org.example.lib.reader;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.TextPosition;
import org.example.lib.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LineBuilder {
	private final List<List<TextPosition>> listTextPositions = new ArrayList<>();
	private Rect shape;
	private static final int MAX_SPECIAL_CHARACTER = 10;
	private final  Map<Pair<Float, Float>, PDColor> colorMap;
	public LineBuilder(Map<Pair<Float, Float>, PDColor> colorMap) {
		this.colorMap = colorMap;
	}

	public void addTextPosition (List<TextPosition> textPositions, Rect shape){
		handleAddBox(shape);
		this.listTextPositions.add(textPositions);
	}

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

	public List<List<Line>> extractLine() {
		List<Line> results = new ArrayList<>();
		List<int[]> splits = new ArrayList<>();
		int countSpecialChar = 0;
		int[] array = new int[4];
		for (int i = 0 ; i< listTextPositions.size(); i ++) {
			var textPositions = listTextPositions.get(i);
			for(int j = 0 ; j < textPositions.size(); j ++) {
				var text = textPositions.get(j).getUnicode();
				if(Utils.isSpecialCharacterString(text)) {
					if(countSpecialChar == 0) {
						array[0] = i;
						array[1] = j;
					}
					countSpecialChar += text.length();
				} else {
					if(countSpecialChar == 0) {
						continue;
					}
					if(countSpecialChar >= MAX_SPECIAL_CHARACTER) {
						array[2] = i;
						array[3] = j;
						splits.add(array);
					}
					array = new int[] {0, 0 , 0, 0};
					countSpecialChar = 0;
				}
			}
		}

		if(splits.isEmpty())  {
			results.add(new Line(this.listTextPositions, this.shape, colorMap));
			return List.of(results, new ArrayList<>());
		}

		List<int[]> newCutPos = new ArrayList<>();
		for(var pos : splits) {
			newCutPos.add(new int[]{pos[0], pos[1]});
			newCutPos.add(new int[]{pos[2], pos[3]});
		}

		List<List<List<TextPosition>>> finalRe1 = new ArrayList<>();
		List<List<TextPosition>> re1 = new ArrayList<>();
		List<List<List<TextPosition>>> finalRe2 = new ArrayList<>();
		List<List<TextPosition>> re2 = new ArrayList<>();

		int from = 0;
		int cutIndex = 0;
		int maxCutIndex = newCutPos.size() -1;
		boolean isRe1 = true;
		int[] to = newCutPos.get(cutIndex);
		cutIndex ++;

		for (int lineIndex = 0; lineIndex < listTextPositions.size(); lineIndex ++) {
			if(lineIndex < to[0]) {
				if(isRe1) {
					re1.add(listTextPositions.get(lineIndex));
				} else {
					re2.add(listTextPositions.get(lineIndex));
				}
			} else {
				while (lineIndex == to[0] ) {
					if(isRe1) {
						if(from < to[1]) {
							re1.add(listTextPositions.get(lineIndex).subList(from, to[1]));
						}
						finalRe1.add(re1);
						re1 = new ArrayList<>();
					} else {
						if(from < to[1]) {
							re2.add(listTextPositions.get(lineIndex).subList(from, to[1]));
						}
						finalRe2.add(re2);
						re2 = new ArrayList<>();
					}
					isRe1 = !isRe1;
					from = to[1];
					if(cutIndex == maxCutIndex) {
						var last = listTextPositions.get(listTextPositions.size() - 1);
						to = new int[]{listTextPositions.size() - 1, last.size() - 1};
						break;
					}
					to = newCutPos.get(cutIndex);
					cutIndex ++;
				}
				if(from == listTextPositions.get(lineIndex).size()) continue;
				if(isRe1) {
					re1.add(listTextPositions.get(lineIndex).subList(from, listTextPositions.get(lineIndex).size()));
					finalRe1.add(re1);
				} else {
					re2.add(listTextPositions.get(lineIndex).subList(from,listTextPositions.get(lineIndex).size()));
				}
			}
		}
		return  List.of(finalRe1.stream().map(item -> new Line(item, colorMap)).toList(),
				finalRe2.stream().map(item -> new Line(item, colorMap)).toList());
	}
}
