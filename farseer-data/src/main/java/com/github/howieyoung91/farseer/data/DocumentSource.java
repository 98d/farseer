package com.github.howieyoung91.farseer.data;

import com.github.howieyoung91.farseer.core.pojo.DocumentVo;

import java.util.List;

public interface DocumentSource<S> {
    List<DocumentVo> getDocuments();
}
