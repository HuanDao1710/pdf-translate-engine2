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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PDFResolver {
	private static PDFont regularFont;
	private static PDFont italicFont;
	private static PDFont boldFont;

	public void resolve () throws IOException {
		String path3 =  "D:\\CurrentProject\\PDFTranslateSolution\\sample\\index.pdf";
		String path2 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\pdf-sample.pdf";
		String filePath = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\Salt and Sodium -Sample.pdf";
		String path4= "D:\\CurrentProject\\PDFTranslateSolution\\sample\\somatosensory.pdf";
		String path5 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\pdf-sample.pdf";
		String path6 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\sample2.pdf";
		String path7 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\sample3.pdf";
		String path8 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\sample4.pdf";
		String path9 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\sample5.pdf";
		String path10 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\eng.pdf";
		String path11 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\arabic2.pdf";
		String path12 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\sample6.pdf";
		String path13 = "D:\\CurrentProject\\PDFTranslateSolution\\sample\\arabic3.pdf";
		List<List<Paragraph>> pageParagraphs = new ArrayList<>();

		PDDocument document = PDDocument.load(new File(path4));
		LineExtractor lineExtractor = new LineExtractor(document);

		var pages = document.getPages();

		for(int i = 0; i < pages.getCount(); i ++) {
			var lines = lineExtractor.extract( i + 1);
			var paragraphs = AIParagraphExtractor.extract(lines);
			pageParagraphs.add(paragraphs);
			for(var para : paragraphs) {
				System.out.println("-----------------------------------");
				System.out.println(para.getTextString());

				System.out.println(para.getShape().getX1() + "|" + para.getShape().getX2() + "|" + para.getShape().getY1() + "|" + para.getShape().getY2() );
				if(Objects.nonNull(para.getStyle()))	{System.out.println(para.getStyle().getColor());
					System.out.println(para.getStyle().getFont());}
			}
			lineExtractor.resetLine();
		}
		var dsp = DSPTranslator.getInstance();
	var paraTexTranslate = pageParagraphs.parallelStream()
				.map(paragraphs -> dsp.longTranslate(new TranslateRequest("ar", "vi", paragraphs
						.stream()
						.map(i -> i.getTextString()).collect(Collectors.toList()))))
				.collect(Collectors.toList());
		TextRemover.removeAllTextFromPDF(document);

		for(int i = 0; i< pages.getCount(); i ++) {
			var pagePara = pageParagraphs.get(i);
			var translatedTexts = paraTexTranslate.get(i);
			var page = pages.get(i);
			PDPageContentStream contentStreamWriter = new PDPageContentStream(document, page,
					PDPageContentStream.AppendMode.APPEND, true, true);
//			transformPageIfNeedTo(contentStreamWriter, page);
			for(int j  = 0; j < pagePara.size(); j ++) {
				var para = pagePara.get(j);
				var translatedText = translatedTexts.get(j);
				var font = getFont(document, para.getStyle());

				ContentWriter.write(contentStreamWriter, para, translatedText, font);
			}
			contentStreamWriter.close();
		}
		document.save("D:\\CurrentProject\\PDFTranslateSolution\\output\\result.pdf");
		document.close();
	}

	public static PDFont getFont(PDDocument document, TextStyle style) throws
			IOException {
		if(Objects.isNull(regularFont)) {
			regularFont =  PDType0Font.load(document, new File("D:\\CurrentProject\\PDFTranslateSolution\\src\\main\\java\\org\\example\\fonts\\arial-unicode-ms.ttf"));
		}

		if(Objects.isNull(italicFont)) italicFont = PDType0Font.load(document, new File("D:\\CurrentProject\\PDFTranslateSolution\\src\\main\\java\\org\\example\\fonts\\Arial-Unicode-Italic.ttf"));
		if(Objects.isNull(boldFont)) boldFont = PDType0Font.load(document, new File("D:\\CurrentProject\\PDFTranslateSolution\\src\\main\\java\\org\\example\\fonts\\Arial-Unicode-Bold.ttf"));
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
