package com.github.howieyoung91.farseer.data;

import com.github.howieyoung91.farseer.core.pojo.DocumentVo;
import com.github.howieyoung91.farseer.core.pojo.JsonResponse;
import com.github.howieyoung91.farseer.data.remote.RemoteIndexController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/14 12:16]
 */
@SpringBootTest
public class RemoteIndexTest {
    @Autowired
    RemoteIndexController indexer;

    @Test
    void testIndex() {
        DocumentVo document = new DocumentVo();
        document.setText("Java是一门面向对象的编程语言，不仅吸收了C++语言的各种优点，还摒弃了C++里难以理解的多继承、指针等概念，因此Java语言具有功能强大和简单易用两个特征。Java语言作为静态面向对象编程语言的代表，极好地实现了面向对象理论，允许程序员以优雅的思维方式进行复杂的编程\n" +
                         "Java具有简单性、面向对象、分布式、健壮性、安全性、平台独立与可移植性、多线程、动态性等特点。Java可以编写桌面应用程序、Web应用程序、分布式系统和嵌入式系统应用程序等");
        document.setContent("{}");
        document.setHighlightPrefix("[");
        document.setHighlightSuffix("]");
        indexer.index(document);
    }

    @Test
    void testSearchByWord() {
        JsonResponse java = indexer.searchByWord("java", 1, 1000);
        System.out.println(java);
    }

    @Test
    void searchBySentence() {
        JsonResponse java = indexer.searchBySentence("编写桌面应用程序、Web应用程序、分布式系统和嵌入式系统应用程序等", 1, 20);
        System.out.println(java);
    }

    @Test
    void testSearchByQuery() {
        JsonResponse java = indexer.searchByQueryString("java", 1, 20);
        System.out.println(java);
    }

    @Test
    void testgetIndices() {
        JsonResponse resp = indexer.getIndices("1555822110155280386", 1, 20);
        System.out.println(resp);
    }


    @Test
    void testDeleteIndices() {
        indexer.deleteIndices("1558719015860424705");
    }
}
