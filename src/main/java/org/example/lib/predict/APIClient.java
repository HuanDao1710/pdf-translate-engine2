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
	public static List<float[]> createSampleData () {
		List<float[]> data = new ArrayList<>();
		for(int i = 0; i < 200; i ++) {
			data.add(new float[]{0f, 0, 0, 0});
		}
		return data;
	}

	public static List<float[]> standardizeData(List<float[]> data) {
		var newData = data;
		while (newData.size() < 200) {
			newData.add(new float[]{0,0,0,0});
		}
		return newData;
	}

	public static List<float[]> standardizeData2(List<List<Float>> data) {
		int div = 1;
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
				finalData.add(new float[]{x1 / div, y1 / div, x2 / div, y2 / div,
						y2 - y1 ,x1 - sample2.get(0),y1 - sample2.get(1),  x2 - sample2.get(2)});
			} else  {
				finalData.add(new float[]{x1 / div, y1 / div, x2 / div, y2/ div, y2 - y1 ,0,0,0});
			}
		}
		return finalData;
	}

	public static List<List<List<Integer>>> call(List<List<List<Float>>> data) throws IOException {
		Gson gson = new Gson();
		List<List<float[]>> d = new ArrayList<>();
		for(int i = 0; i < data.size(); i ++) {
			d.add(standardizeData2(data.get(i)));
		}

		String json = gson.toJson(d);
//		System.out.println(json);
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

//		System.out.println("Response from server: " + response.toString());
		return gson.fromJson(response.toString(),
				new TypeToken<List<List<List<Integer>>>>(){}.getType());
	}

}
