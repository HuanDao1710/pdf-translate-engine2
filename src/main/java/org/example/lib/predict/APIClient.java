package org.example.lib.predict;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class APIClient {

	public static List<float[]> augmentDataAttributes(List<List<Float>> data, float width, float height) {
		var newData = new ArrayList<>(data);
		while (newData.size() < 200) {
			newData.add(new ArrayList<>(List.of(0f, 0f, 0f, 0f))); // Khởi tạo một ArrayList mới có thể sửa đổi
		}
		List<float[]> finalData = new ArrayList<>();
		for(int i = 0; i < newData.size(); i ++) {
			var sample1 = newData.get(i);
			var x1 = sample1.get(0);
			var y1 = sample1.get(1);
			var x2 = sample1.get(2);
			var y2 = sample1.get(3);
			if(i < newData.size() - 1) {
				var sample2 = newData.get(i + 1);
				finalData.add(new float[]{x1,y1,x2,y2,x2 - x1, y2 - y1 ,
						x1 - sample2.get(0),y1 - sample2.get(1),  x2 - sample2.get(2), y1 - sample2.get(3)});
			} else  {
				finalData.add(new float[]{x1,y1,x2,y2,x2 - x1, y2 - y1 ,0,0,0, 0});
			}
		}
		return finalData;
	}

	public static List<List<List<Integer>>> call(List<List<List<Float>>> data, float width, float hieght) throws IOException {
		//sep
		List<List<List<Float>>> newData = new ArrayList<>();
		List<Integer> merger = new ArrayList<>();
		for(int i = 0; i < data.size(); i ++) {
			var arr = data.get(i);
			while (arr.size() > 200) {
				newData.add(arr.subList(0,200));
				merger.add(i);
				arr = arr.subList(200, arr.size());
			}
			newData.add(arr);
			merger.add(i);
		}

		Gson gson = new Gson();
		List<List<float[]>> d = new ArrayList<>();
		for(int i = 0; i < newData.size(); i ++) {
			d.add(augmentDataAttributes(newData.get(i), width, hieght));
		}
		String json = gson.toJson(d);
		String jsonInputString = "{\"input\": " + json + "}";
		URL url = new URL("http://localhost:5000/predict");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		}

		// Đọc phản hồi từ server
		StringBuilder response = new StringBuilder();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
			String responseLine;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
		}

		List<List<List<Integer>>> resultsExtracted = gson.fromJson(response.toString(),
				new TypeToken<List<List<List<Integer>>>>(){}.getType());

		List<List<List<Integer>>> finalResults = new ArrayList<>();
		int index = 0;
		List<List<Integer>> item = new ArrayList<>();
		for(int i = 0; i< resultsExtracted.size(); i++) {
			if(merger.get(i) != index) {
				index += 1;
				finalResults.add(item);
				item = resultsExtracted.get(i);
			} else {
				item.addAll(resultsExtracted.get(i));
			}
		}
		finalResults.add(item);
		return finalResults;
	}

}
