package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;
import org.example.dsp.DSPTranslator;
import org.example.dsp.request.TranslateRequest;
import org.example.lib.reader.*;
import org.example.lib.writer.ContentWriter;
import org.example.lib.writer.TextRemover;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class PDFResolver {
	private static PDFont regularFont;
	private static PDFont italicFont;
	private static PDFont boldFont;
	private final InputStream inputStream;
	private final String from;
	private final String to;

	public PDFResolver(InputStream inputStream, String from, String to) {
		this.inputStream = inputStream;
		this.from = from;
		this.to = to;
	}

	public ByteArrayOutputStream resolve( int limit, int numPageTranslateWithGPT, String gptAPIKey) throws IOException {

		PDDocument document = PDDocument.load(inputStream);
		var pages = document.getPages();
		if(limit == -1) {
			limit = pages.getCount();
		}

//		var pageLines = IntStream.range(0, pages.getCount()).boxed().parallel()
//				.map(index -> {
//					LineExtractor lineExtractor = new LineExtractor(document);
//					List<Line> lines ;
//					try {
//						lines = lineExtractor.extract( index + 1);
//					} catch (IOException e) {
//						return  new ArrayList<Line>();
//					}
//					return lines;
//				}).toList();
		List<List<Line>> pageLines = new ArrayList<>();
		for (int index = 0; index< limit; index ++) {
			LineExtractor lineExtractor = new LineExtractor(document);
			List<Line> lines = null;
			try {
				lines = lineExtractor.extract( index + 1);
			} catch (IOException e) {

			}
			pageLines.add(lines);
		}
		var cropBox = pages.get(0).getCropBox();
		List<List<Paragraph>> pageParagraphs = AIParagraphExtractor.extract(pageLines, cropBox.getWidth(), cropBox.getHeight());
		var dsp = DSPTranslator.getInstance();
		var paraTexTranslate = pageParagraphs.parallelStream()
					.map(paragraphs -> dsp.longTranslate(new TranslateRequest(this.from, this.to, paragraphs
							.stream()
							.map(Paragraph::getTextString).toList())))
					.toList();
		TextRemover.removeAllTextFromPDF(document, limit);

		for(int i = 0; i < limit; i ++) {
			handleWritePage(document, pageParagraphs.get(i),paraTexTranslate.get(i), pages.get(i));
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		document.save(outputStream);
		document.save("output/result1.pdf");
		document.close();
		return outputStream;
	}

	private void handleWritePage (PDDocument document ,List<Paragraph> pagePara, List<String> translatedTexts, PDPage page) throws
			IOException {
		PDPageContentStream contentStreamWriter = new PDPageContentStream(document, page,
				PDPageContentStream.AppendMode.APPEND, 	true, true);
//			transformPageIfNeedTo(contentStreamWriter, page);
		for(int j  = 0; j < pagePara.size(); j ++) {
			var para = pagePara.get(j);
			var translatedText = translatedTexts.get(j);
			var font = getFont(document, para.getStyle());

			ContentWriter.write(contentStreamWriter, para, translatedText, font);
		}
		contentStreamWriter.close();
	}

	public static PDFont getFont(PDDocument document, TextStyle style) throws
			IOException {
		if(Objects.isNull(regularFont)) {
			regularFont =  PDType0Font.load(document, new File("src/main/java/org/example/fonts/arial-unicode-ms.ttf"));
		}
		if(Objects.isNull(italicFont)) italicFont = PDType0Font.load(document, new File("src/main/java/org/example/fonts/Arial-Unicode-Italic.ttf"));
		if(Objects.isNull(boldFont)) boldFont = PDType0Font.load(document, new File("src/main/java/org/example/fonts/Arial-Unicode-Bold.ttf"));
		try {
			var base = style.getFont().getFontDescriptor().getFontName();
			if(base.contains("Italic")) {
				return italicFont;
			} else if (base.contains("Bold")) {
				return boldFont;
			}
			return regularFont;
		} catch (Exception e) {
			return regularFont;
		}

	}


	private static void transformPageIfNeedTo(PDPageContentStream pdPageContentStream, PDPage pdPage) throws
			IOException {
		int rotation = ((pdPage.getRotation() % 360) + 360) % 360;
		if (rotation % 360 == 0) return;
		PDRectangle cropBox = pdPage.getCropBox();
		float tx = (cropBox.getLowerLeftX() + cropBox.getUpperRightX()) / 2;
		float ty = (cropBox.getLowerLeftY() + cropBox.getUpperRightY()) / 2;

		if (rotation % 180 == 0) {
			pdPageContentStream.transform(Matrix.getTranslateInstance(tx, ty));
			pdPageContentStream.transform(Matrix.getRotateInstance(Math.toRadians(rotation), 0, 0));
			pdPageContentStream.transform(Matrix.getTranslateInstance(-tx, -ty));
			return;
		}

		if (rotation % 90 == 0) {
			pdPageContentStream.transform(Matrix.getTranslateInstance(tx, ty));
			pdPageContentStream.transform(Matrix.getRotateInstance(Math.toRadians(rotation), 0, 0));
			pdPageContentStream.transform(Matrix.getTranslateInstance(-tx, -ty));
			pdPageContentStream.transform(Matrix.getTranslateInstance((cropBox.getWidth() - cropBox.getHeight()) / 2,
					(cropBox.getHeight() - cropBox.getWidth()) / 2));
		}
	}

}
