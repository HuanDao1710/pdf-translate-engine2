package org.example.dsp;

import org.example.dsp.request.TranslateRequest;

import java.util.List;

public interface TranslationProvider {
    List<String> translate(TranslateRequest request);
}
