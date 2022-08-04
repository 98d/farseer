package com.github.howieyoung91.farseer.controller;

import com.github.howieyoung91.farseer.entity.Token;
import com.github.howieyoung91.farseer.pojo.JsonResponse;
import com.github.howieyoung91.farseer.service.TokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
