package org.example.dsp;


import org.example.dsp.request.TranslateRequest;

import java.util.List;

public interface TranslateLongTextProvider {
    List<String> longTranslate(TranslateRequest request);
}
