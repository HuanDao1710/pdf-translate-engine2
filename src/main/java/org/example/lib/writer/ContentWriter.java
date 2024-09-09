package org.example.lib.writer;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.example.lib.reader.Line;
import org.example.lib.reader.Paragraph;
import org.example.lib.reader.Rect;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class ContentWriter {

	public static void write(PDPageContentStream contentStreamWriter, Paragraph content,
			String translatedText, PDFont font) throws IOException {
		int start = 0;
		int end = 0;

		var lines = content.getLines();

		int lineIndex = 0;
		Line block = lines.get(lineIndex);
		Rect rect = block.getShape();
		assert rect != null;
		float lineWidth = Math.abs(rect.getX2() - rect.getX1());
		float height =Math.abs(rect.getY2() - rect.getY1());

		float y = rect.getY1();
		float x = rect.getX1();

		var text = normalizeTextNoGlyph(font, translatedText);

		contentStreamWriter.setStrokingColor(0.0f);
//	 for(var line : content.getLines()) {
//		 contentStreamWriter.addRect(line.getShape().getX1(), line.getShape().getY1()
//				 , Math.abs(line.getShape().getX1() - line.getShape().getX2()), Math.abs(line.getShape().getY1() - line.getShape().getY2()));
//	 }


		contentStreamWriter.addRect(content.getShape().getX1(), content.getShape().getY1()
				, Math.abs(content.getShape().getX1() - content.getShape().getX2()), Math.abs(content.getShape().getY1() - content.getShape().getY2()));
		contentStreamWriter.stroke();


		try {
			contentStreamWriter.setNonStrokingColor(content.getStyle().getColor());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (lines.size() == 1) {
			float fontSize = 0.95f * height;
			float width = getWidthString(text, font, fontSize);
			while (width > lineWidth) {
				fontSize = fontSize - 0.05f;
				width = getWidthString(text, font, fontSize);
			}
			contentStreamWriter.beginText();

			contentStreamWriter.setFont(font, fontSize);
			contentStreamWriter.newLineAtOffset(x, y);
			try {
				contentStreamWriter.showText(text);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			contentStreamWriter.endText();
			return;
		}

		float fontSize = calculateFontSize(lines, text, font);

		for (int i : possibleWrapPoints(text)) {
			float width = font.getStringWidth(text.substring(start, i)) / 1000 * fontSize;
			if (start < end && width >= lineWidth) {
				contentStreamWriter.beginText();

				contentStreamWriter.setFont(font, fontSize);
				contentStreamWriter.newLineAtOffset(x, y);
				try {
					contentStreamWriter.showText(text.substring(start, end));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				contentStreamWriter.endText();
				if (lineIndex + 1 >= lines.size()) {
					x = x + getWidthString(text.substring(start, end), font, fontSize);
					start = end;
					continue;
				}
				start = end;
				lineIndex = lineIndex + 1;
				block = lines.get(lineIndex);
				rect = block.getShape();
				assert rect != null;
				lineWidth = Math.abs(rect.getX2() - rect.getX1());
				x = rect.getX1();
				y = rect.getY1();
			}
			end = i;
		}
		contentStreamWriter.beginText();

		contentStreamWriter.setFont(font, fontSize);
		contentStreamWriter.newLineAtOffset(x, y);
		try {
			contentStreamWriter.showText(text.substring(start, end));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		contentStreamWriter.endText();
	}

	static float getWidthString(String text, PDFont font, float fontSize) throws RuntimeException {
		try {
			return font.getStringWidth(text) / 1000 * fontSize;
		} catch (Exception ex) {
			throw new RuntimeException();
		}
	}

	private static float calculateFontSize(List<Line> lines, String text, PDFont font) {
		float width = 0;
		float height = 0;

		for (Line line : lines) {
			Rect rect = line.getShape();
			assert rect != null;
			width = width + Math.abs(rect.getX2() - rect.getX1());
			height = height + Math.abs(rect.getY2() - rect.getY1());
		}

		float fontSize = (0.95f * (height ) / lines.size());

		float totalWidthString = getWidthString(text, font, fontSize);
		float totalWidthBlock = width ;
		while ((totalWidthString / totalWidthBlock) > 0.95f ) {
			fontSize -= 0.05f;
			totalWidthString = getWidthString(text, font, fontSize);
		}
		return fontSize;
	}

	static int[] possibleWrapPoints(String text) {
		String regex = "((?<=.))";
		if (Pattern.compile(".*[a-zA-Z ].*").matcher(text).matches()) {
			regex = "(?<=[\s.,;:\"'!#。、])";
		}
		String[] split = text.split(regex);
		int[] ret = new int[split.length];
		ret[0] = split[0].length();
		for (int i = 1; i < split.length; i++)
			ret[i] = ret[i - 1] + split[i].length();
		return ret;
	}

	private static String normalizeTextNoGlyph(PDFont font, String text) {
		try {
			font.encode(text);
			return text;
		} catch (Exception ignored) {
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			try {
				font.encode(String.valueOf(text.charAt(i)));
				stringBuilder.append(text.charAt(i));
			} catch (Exception ex) {
				stringBuilder.append(" ");
			}
		}

		return stringBuilder.toString();
	}


}
