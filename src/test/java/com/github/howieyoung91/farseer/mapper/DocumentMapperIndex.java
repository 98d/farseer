package com.github.howieyoung91.farseer.mapper;

import com.github.howieyoung91.farseer.entity.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;

@SpringBootTest
public class DocumentMapperIndex {
    @Resource
    private DocumentMapper documentMapper;

    @Test
    void test() {
        Document document = new Document();
        document.setId("1");
        document.setText("{'temp':1}");
        System.out.println(documentMapper.insert(document));
    }

    @Test
    void testInsertBatch() {
        Document document  = new Document();
        Document document1 = new Document();
        document.setContent("{}");
        document1.setContent("{}");
        ArrayList<Document> documents = new ArrayList<>() {{
            add(document);
            add(document1);
        }};

        System.out.println(documentMapper.insertBatch(documents));
        System.out.println(document);
        System.out.println(document1);
    }
}
