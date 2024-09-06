package org.example.lib.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ParagraphExtractor {

	public static List<Paragraph> extractParagraph (List<Line> lines) {
		if(lines.isEmpty()) return Collections.emptyList();
		List<Paragraph> paragraphs = new ArrayList<>();

		TextStyle currentTextStyle = lines.get(0).getTextStyle();
		Rect currentShape = lines.get(0).getShape();
		Paragraph paragraph = new Paragraph();
		for (var line : lines) {
			if(
					(!Utils.compareDifferenceWithinRange(currentShape.getX1(),line.getShape().getX1())
							&& !Utils.compareDifferenceWithinRange(currentShape.getX2() , line.getShape().getX2()))
					|| checkFontSize(currentTextStyle, line.getTextStyle(),currentShape, line.getShape())
			) {
				paragraph.endParagraph();
				paragraphs.add(paragraph);
				currentTextStyle = line.getTextStyle();
				paragraph = new Paragraph();
			}
			currentShape = line.getShape();
			paragraph.addIntoParagraph(line);
			}
		paragraph.endParagraph();
		paragraphs.add(paragraph);
		return paragraphs;
	}

	public static boolean checkFontSize(TextStyle currentTextStyle, TextStyle line, Rect currentShape, Rect lineShape ) {
		try {
			var style = currentTextStyle;
			if(Objects.isNull(style)) style = line;
			return style.getTextHeight() * 1.5 < Math.abs(currentShape.getY1() - lineShape.getY1());
		} catch (Exception e) {
			return false;
		}
	}
}
