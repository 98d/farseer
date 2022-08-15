package com.github.howieyoung91.farseer.core.controller;

import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import com.github.howieyoung91.farseer.core.pojo.JsonResponse;

/**
 * @author Howie Young on <i>2022/08/14 14:57<i/>
 * @version 1.0
 * @since 1.0
 */
public interface IndexController {
    JsonResponse searchByQueryString(String query, Integer page, Integer size);

    JsonResponse searchBySentence(String sentence, Integer page, Integer size);

    JsonResponse searchByWord(String word, Integer page, Integer size);

    JsonResponse getIndices(String documentId, Integer page, Integer size);

    JsonResponse deleteIndices(String documentId);

    JsonResponse index(DocumentVo documentVo);
}
