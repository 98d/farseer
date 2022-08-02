package com.github.howieyoung91.farseer.service;

import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.util.Factory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;

@SpringBootTest
public class IndexServiceTest {
    @Resource
    private IndexService indexService;

    @Test
    void testIndex() {
        ArrayList<Document> documents = new ArrayList<>();

        Document document = new Document();
        document.setText("其实在ArrayList中有一个构造器可以用构造器来接受Collection");
        // document.setText("Collection");
        document.setContent("{}");
        documents.add(document);

        Document document1 = new Document();
        document1.setText("返回类型是Collection");
        // document1.setText("Collection");
        document1.setContent("{}");
        documents.add(document1);

        System.out.println(indexService.index(documents));
    }

    @Test
    void testSearch() {
        System.out.println(indexService.search("Collection", Factory.createPage(1, 1000)));
    }
}
