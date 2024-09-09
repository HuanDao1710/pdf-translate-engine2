package org.example.lib.reader;

import org.example.lib.predict.APIClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AIParagraphExtractor {
	public static List<List<Paragraph>>  extract(List<List<Line>> pageLines, float width, float height) throws IOException {

		List<List<List<Float>>> coordinates = pageLines.stream()
				.map(lines -> lines.stream().map(Line::getShape)
						.map(Rect::asVector).toList()).toList();
		List<List<List<Integer>>> listBreakLines= APIClient.call(coordinates, width, height);

		List<List<Paragraph>>  pageParagraphs = new ArrayList<>();

		for(int i = 0; i < pageLines.size(); i++) {
			var lines = pageLines.get(i);
			var breakLines = listBreakLines.get(i);
			var paragraphs = new ArrayList<Paragraph>();
			List<Line> subLines = new ArrayList<>();
			for(int j = 0; j < lines.size(); j ++) {
				subLines.add(lines.get(j));
				if(breakLines.get(j).get(0) == 1) {
					paragraphs.add(new Paragraph(subLines));
					subLines = new ArrayList<>();
				}
			}
			if(!subLines.isEmpty()) {
				paragraphs.add(new Paragraph(subLines));
			}
			pageParagraphs.add(paragraphs);
		}

		return pageParagraphs;
		}

}
