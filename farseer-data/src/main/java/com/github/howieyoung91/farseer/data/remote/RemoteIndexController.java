package com.github.howieyoung91.farseer.data.remote;

import com.github.howieyoung91.farseer.core.controller.IndexController;
import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import com.github.howieyoung91.farseer.core.pojo.JsonResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/14 13:10]
 */
@Component
public class RemoteIndexController implements IndexController {
    @Resource
    private RestTemplate template;
    private String       server = "http://localhost:8080";

    @Override
    public JsonResponse searchByQueryString(String query, Integer page, Integer size) {
        return template.getForObject(server + "/api/farseer/document/search/query/" + query, JsonResponse.class, page, size);
    }

    @Override
    public JsonResponse searchBySentence(String sentence, Integer page, Integer size) {
        return template.getForObject(server + "/api/farseer/document/search/sentence/" + sentence, JsonResponse.class, page, size);
    }

    @Override
    public JsonResponse searchByWord(String word, Integer page, Integer size) {
        return template.getForObject(server + "/api/farseer/document/search/word/" + word, JsonResponse.class, page, size);
    }

    @Override
    public JsonResponse getIndices(String documentId, Integer page, Integer size) {
        return template.getForObject(server + "/api/farseer/document/" + documentId + "/index", JsonResponse.class);
    }

    @Override
    public JsonResponse deleteIndices(String documentId) {
        template.delete(server + "/api/farseer/document/" + documentId + "/index");
        return null;
    }

    @Override
    public JsonResponse index(DocumentVo documentVo) {
        template.put(server + "/api/farseer/index", documentVo);
        return null;
    }
}
