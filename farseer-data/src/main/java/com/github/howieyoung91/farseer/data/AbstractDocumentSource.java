package com.github.howieyoung91.farseer.data;

import com.github.howieyoung91.farseer.data.convert.DocumentVoConverter;

public abstract class AbstractDocumentSource<S> implements DocumentSource<S> {
    private DocumentVoConverter<S> converter;

    public AbstractDocumentSource(DocumentVoConverter<S> converter) {
        this.converter = converter;
    }

    protected DocumentVoConverter<S> getDocumentConverter() {
        return converter;
    }
}