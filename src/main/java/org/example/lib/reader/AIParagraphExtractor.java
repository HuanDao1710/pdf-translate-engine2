package org.example.lib.reader;

import org.example.lib.predict.APIClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AIParagraphExtractor {
	public static List<Paragraph> extract(List<Line> lines) throws IOException {
		List<Paragraph> paragraphs = new ArrayList<>();
		List<float[]> coordinates = lines.stream()
				.map(line ->line.getShape().asVector()).collect(Collectors.toList());
		List<List<Integer>> breakLines= APIClient.call(coordinates);
		List<Line> subLines = new ArrayList<>();
		for(int i = 0; i < lines.size(); i ++) {
			subLines.add(lines.get(i));
			if(breakLines.get(i).get(0) == 1) {
				paragraphs.add(new Paragraph(subLines));
				subLines = new ArrayList<>();
			}
		}

		if(!subLines.isEmpty()) {
			paragraphs.add(new Paragraph(subLines));
		}

		return paragraphs;
	}

}
