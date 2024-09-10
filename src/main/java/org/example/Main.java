package org.example;
import java.io.*;
import java.nio.file.Path;


public class Main {
	public static void main(String[] args) throws IOException {

		String path3 =  "sample/index.pdf";
		String path2 = "sample/pdf-sample.pdf";
		String filePath = "sample/Salt and Sodium -Sample.pdf";
		String path4= "sample/somatosensory.pdf";
		String path5 = "sample/pdf-sample.pdf";
		String path6 = "sample/sample2.pdf";
		String path8 = "sample/sample4.pdf";
		String path9 = "sample/sample5.pdf";
		String path10 = "sample/eng.pdf";
		String path11 = "sample/arabic2.pdf";
		String path12 = "sample/sample6.pdf";
		String path13 = "sample/arabic3.pdf";
		String path14 = "sample/sample7.pdf";
		String longSamplePath = "sample/long_sample.pdf";

		InputStream inputStream = new FileInputStream(path14);

		PDFResolver pdfResolver = new PDFResolver(inputStream, "en", "vi");
		var outPath = Path.of("output/result2.pdf");
		try (ByteArrayOutputStream outputStream = pdfResolver.resolve(-1, 0, "");) {
//			Files.copy(new ByteArrayInputStream(outputStream.toByteArray()), Files.createFile(outPath), StandardCopyOption.REPLACE_EXISTING);
		}

	}

}