package org.example.dsp.request;

import java.util.List;

public class TranslateRequest {
    private String from;
    private String to;
    private List<String> texts;

    public TranslateRequest() {
    }

    public TranslateRequest(String from, String to, List<String> texts) {
        this.from = from;
        this.to = to;
        this.texts = texts;
    }

    public static TranslateRequest of(String from, String to, List<String> texts) {
        return new TranslateRequest(from, to, texts);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}
