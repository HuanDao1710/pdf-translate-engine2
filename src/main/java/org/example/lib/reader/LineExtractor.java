package org.example.lib.reader;

import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.example.lib.utils.Pair;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class LineExtractor {
	List<Line> lines = new ArrayList<>();
	private TextStyle globalStyle = null;
	private Line currentLine = null;
	private Rect currentRect = null;
	private final PDDocument document;
	public LineExtractor(PDDocument pdDocument) {
		this.document = pdDocument;
	}

	public List<Line> extract( int pageIndex) throws IOException {
//		Matrix pageMatrix = page.getMatrix();
		PDFTextStripper textStripper = new PDFTextStripperSuper() {
			final Map<Pair<Float, Float>, PDColor> colorMap = new HashMap<>();
			@Override
			protected void processTextPosition(TextPosition text) {
				super.processTextPosition(text);
				var textMatrix = text.getTextMatrix();
				colorMap.put(new Pair<>(textMatrix.getTranslateX(), textMatrix.getTranslateY()
				), getGraphicsState().getNonStrokingColor());
//				colorMap.put(new Pair<>(text.getX(), text.getY()), getGraphicsState().getNonStrokingColor());
			}


			@Override
			protected void writeString(String text, List<TextPosition> textPositions) throws
					IOException {
				if(textPositions.isEmpty()) return;
				TextPosition textPosition = textPositions.get(0);
//				int i = 0;
//				for(var pos : textPositions) {
//					if(Utils.isContainsText(pos.getUnicode())) {
//						textPosition = pos;
//						break;
//					}
//					i++;
//				}
				if(Objects.isNull(textPosition)) return;

//				StringBuilder text = new StringBuilder();
//				//getText
//				for(int j = i; j < textPositions.size(); j ++) {
//					text.append(textPositions.get(j).getUnicode());
//				}
				var font = textPosition.getFont();
				var lastPosition = textPositions.get(textPositions.size() - 1);
				var direction = textPosition.getDir();

				float x1 = textPosition.getTextMatrix().getTranslateX();
				float y1 = textPosition.getTextMatrix().getTranslateY();
				var scale = textPosition.getYScale();
				float y2 = y1 + scale;
				float x2 = lastPosition.getTextMatrix().getTranslateX() + lastPosition.getWidth();
				float[] sw = swap(x1, x2);
				x1 = sw[0];
				x2 = sw[1];
				System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA: " + direction + " " + textPosition.getRotation() + "|" + text);

				var style = new TextStyle(
						scale,
						font,
						textPosition.getRotation(),
						direction,
						colorMap.get(new Pair<>(x1, y1))
				);
				//init
				if(Objects.isNull(globalStyle)) {
					globalStyle = style;
				}
				if(Objects.isNull(currentRect)) {
					currentRect = new Rect(x1, y1, x2, y2);
				}
				if(Objects.isNull(currentLine)) {
					currentLine = new Line(new Rect(x1, y1, x2, y2));
				}
				//Add line to lines
				if(currentRect.getY1() != y1 || (Math.abs(currentRect.getX2() - x1) > scale * 2) ) {
					currentLine.endLine();
					lines.add(currentLine);
					currentLine = new Line(new Rect(x1, y1, x2, y2));
					globalStyle = style;
				}
				//add string to line
				currentLine.addIntoLine(text, new Rect(x1, y1, x2, y2), style);
				currentRect = currentLine.getShape();
				super.writeString(text, textPositions);
			}
		};

		textStripper.setSortByPosition(false);
		textStripper.setStartPage(pageIndex);
		textStripper.setEndPage(pageIndex);
		textStripper.getText(document);
		if(Objects.nonNull(currentLine)) {
			currentLine.endLine();
			lines.add(currentLine);
		}
		return lines;
	}
	public void cleanPage () {
		this.lines = new ArrayList<>();
		this.globalStyle = null;
		this.currentLine = null;
		this.currentRect = null;
	}

	public void resetLine() {
		lines = new ArrayList<>();
		this.globalStyle = null;
		this.currentLine = null;
		this.currentRect = null;
	}

	public static class PDFTextStripperSuper extends PDFTextStripper {
		boolean newLine = true;

		public PDFTextStripperSuper() throws IOException {
			addOperator(new SetStrokingColorSpace());
			addOperator(new SetNonStrokingColorSpace());
			addOperator(new SetStrokingDeviceCMYKColor());
			addOperator(new SetNonStrokingDeviceCMYKColor());
			addOperator(new SetNonStrokingDeviceRGBColor());
			addOperator(new SetStrokingDeviceRGBColor());
			addOperator(new SetNonStrokingDeviceGrayColor());
			addOperator(new SetStrokingDeviceGrayColor());
			addOperator(new SetStrokingColor());
			addOperator(new SetStrokingColorN());
			addOperator(new SetNonStrokingColor());
			addOperator(new SetNonStrokingColorN());
		}

		@Override
		protected void startPage(PDPage page) throws IOException {
			newLine = true;
			super.startPage(page);
		}

		@Override
		protected void writeLineSeparator() throws IOException {
			newLine = true;
			super.writeLineSeparator();
		}
	}

	public static float[] swap(float a, float b) {
		if (a > b) {
			float temp = a;
			a = b;
			b = temp;
		}
		return new float[]{a, b};
	}

}
