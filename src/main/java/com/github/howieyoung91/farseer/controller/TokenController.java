package com.github.howieyoung91.farseer.controller;

import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.pojo.JsonResponse;
import com.github.howieyoung91.farseer.service.TokenService;
import com.github.howieyoung91.farseer.util.Factory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/farseer")
public class TokenController {
    @Resource
    private TokenService tokenService;

    @GetMapping("/token")
    public JsonResponse getToken(@RequestParam("tokenIds") List<String> tokenIds) {
        List<Token> tokens = tokenService.selectTokensById(tokenIds);
        return JsonResponse.SUCCESSFUL(tokens);
    }

    @GetMapping("/token/startswith/{prefix}")
    public JsonResponse getTokensStartWith(@PathVariable String prefix, Integer page, Integer size) {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }

        List<String> words = tokenService.selectWordsStartsWith(prefix, Factory.createPage(page, size));
        return JsonResponse.SUCCESSFUL(words);
    }
}
