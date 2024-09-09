package org.example.lib.writer;

import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextRemover {
	public static void removeAllTextFromPDF(PDDocument document, int limit) throws
			IOException {
		document.setAllSecurityToBeRemoved(true);
		var pages = document.getPages();

		for (int i = 0; i < limit; i++ )
		{
			var page = pages.get(i);
			List<Object> newTokens = createTokensWithoutText(page);
			PDStream newContents = new PDStream(document);
			writeTokensToStream(newContents, newTokens);
			page.setContents(newContents);
			processResources(page.getResources());
		}
	}


	private static void processResources(PDResources resources) throws IOException
	{
		if(Objects.nonNull(resources)) {
			for (COSName name : resources.getXObjectNames())
			{
				PDXObject xobject = resources.getXObject(name);
				if (xobject instanceof PDFormXObject)
				{
					PDFormXObject formXObject = (PDFormXObject) xobject;
					writeTokensToStream(formXObject.getContentStream(),
							createTokensWithoutText(formXObject));
					processResources(formXObject.getResources());
				}
			}
			for (COSName name : resources.getPatternNames())
			{
				PDAbstractPattern pattern = resources.getPattern(name);
				if (pattern instanceof PDTilingPattern)
				{
					PDTilingPattern tilingPattern = (PDTilingPattern) pattern;
					writeTokensToStream(tilingPattern.getContentStream(),
							createTokensWithoutText(tilingPattern));
					processResources(tilingPattern.getResources());
				}
			}
		}

	}

	private static void writeTokensToStream(PDStream newContents, List<Object> newTokens) throws IOException
	{
		try (OutputStream out = newContents.createOutputStream(COSName.FLATE_DECODE))
		{
			ContentStreamWriter writer = new ContentStreamWriter(out);
			writer.writeTokens(newTokens);
		}
	}

	private static List<Object> createTokensWithoutText(PDContentStream contentStream) throws IOException
	{
		PDFStreamParser parser = new PDFStreamParser(contentStream);
		Object token = parser.parseNextToken();
		List<Object> newTokens = new ArrayList<>();
		while (token != null)
		{
			if (token instanceof Operator)
			{
				Operator op = (Operator) token;
				String opName = op.getName();
				if (OperatorName.SHOW_TEXT_ADJUSTED.equals(opName)
						|| OperatorName.SHOW_TEXT.equals(opName)
						|| OperatorName.SHOW_TEXT_LINE.equals(opName))
				{
					// remove the argument to this operator
					newTokens.remove(newTokens.size() - 1);

					token = parser.parseNextToken();
					continue;
				}
				else if (OperatorName.SHOW_TEXT_LINE_AND_SPACE.equals(opName))
				{
					// remove the 3 arguments to this operator
					newTokens.remove(newTokens.size() - 1);
					newTokens.remove(newTokens.size() - 1);
					newTokens.remove(newTokens.size() - 1);

					token = parser.parseNextToken();
					continue;
				}
			}
			newTokens.add(token);
			token = parser.parseNextToken();
		}
		return newTokens;
	}

}
