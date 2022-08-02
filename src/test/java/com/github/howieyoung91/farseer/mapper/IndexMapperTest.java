package com.github.howieyoung91.farseer.mapper;

import com.github.howieyoung91.farseer.entity.Index;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IndexMapperTest {
    @Autowired
    private IndexMapper indexMapper;

    @Test
    void test() {
        Index index = new Index();
        index.setId("1");
        index.setTokenId("test");
        index.setDocumentId("1");
        index.setCount(1);
        System.out.println(indexMapper.insert(index));
    }
}

