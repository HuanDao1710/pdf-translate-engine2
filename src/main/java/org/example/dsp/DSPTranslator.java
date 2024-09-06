package org.example.dsp;


import com.fasterxml.jackson.core.type.TypeReference;
import org.example.dsp.request.DSPRequestDTO;
import org.example.dsp.request.DSPResponseDTO;
import org.example.dsp.request.TranslateDSPInput;
import org.example.dsp.request.TranslateRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DSPTranslator implements TranslationProvider {
    private final String apiKey;
    private final String dspId;
    private static DSPTranslator dspTranslator = null;
    private static final Object mutex = new Object();

    private DSPTranslator(String apiKey, String dspId) {
        this.apiKey = apiKey;
        this.dspId = dspId;
    }

    public static DSPTranslator getInstance() {
        if (Objects.isNull(dspTranslator)) {
            synchronized (mutex) {
                if (Objects.isNull(dspTranslator)) {
                    dspTranslator = new DSPTranslator("ab945be6b992e486a961751693eff6b601e98195d53ddf0dc59b4d2863b6a80a", "65950d9c358cbf7bd0ed1e87");
                }
            }
        }

        return dspTranslator;
    }

    @Override
    public List<String> translate(TranslateRequest request) {
        var textQuery = request.getTexts().stream().map(item -> {
            try {
                return String.format("q=%s", URLEncoder.encode(item, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return "";
            }
				}).collect(Collectors.joining("&"));
        DSPRequestDTO requestDTO = new DSPRequestDTO();
        requestDTO.setDcsId(dspId);
        requestDTO.setInput(new TranslateDSPInput(request.getFrom(), request.getTo(), textQuery));
        requestDTO.setReceiveResponse(true);
        requestDTO.setPreferHighSpeed(false);
        try {
            List<String> result = callApi(requestDTO);
            // recall
            if (result.size() != request.getTexts().size()) {
                result = callApi(requestDTO);
            }
            // recall
            if (result.size() != request.getTexts().size()) {
                result = callApi(requestDTO);
            }
            if (result.size() != request.getTexts().size()) {
                return request.getTexts();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private List<String> callApi(DSPRequestDTO requestDTO) throws IOException {
        try {
            return new RequestHelper<List<DSPResponseDTO<List<String>>>, DSPRequestDTO>()
                    .withURI("https://dsp.data4game.com/dcs-api/dcs/execute")
                    .withHeader("api-key", apiKey)
                    .withBody(requestDTO)
                    .post(new TypeReference<>() {
                    }).stream().map(DSPResponseDTO::getResult)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    public List<String> longTranslate(TranslateRequest request) {
        List<TranslateRequest> requests = splitRequest(request, 2500);
        if (requests.size() == 1) {
            return  translate(request);
        }
        var dsp = DSPTranslator.getInstance();
        return requests.stream().parallel()
            .map(dsp::translate)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    public List<TranslateRequest> splitRequest(TranslateRequest request, int maxChars) {
        List<TranslateRequest> splitRequests = new ArrayList<>();
        List<String> texts = request.getTexts();
        List<String> currentBatch = new ArrayList<>();
        int currentBatchSize = 0;

        for (String text : texts) {
            int textSize = text.length();
            if (currentBatchSize + textSize > maxChars && !currentBatch.isEmpty()) {
                // Create a new TranslateRequest with the current batch and reset
                splitRequests.add(TranslateRequest.of(request.getFrom(), request.getTo(),
                    currentBatch));
                currentBatch = new ArrayList<>();
                currentBatchSize = 0;
            }
            currentBatch.add(text);
            currentBatchSize += textSize;
        }

        if (!currentBatch.isEmpty()) {
            splitRequests.add(TranslateRequest.of(request.getFrom(), request.getTo(), currentBatch));
        }

        return splitRequests;
    }


}
