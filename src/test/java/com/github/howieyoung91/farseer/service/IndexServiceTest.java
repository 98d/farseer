package com.github.howieyoung91.farseer.service;

import com.github.howieyoung91.farseer.entity.Document;
import com.github.howieyoung91.farseer.service.support.DefaultIndexService;
import com.github.howieyoung91.farseer.util.Factory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class IndexServiceTest {
    @Autowired
    private DefaultIndexService indexService;

    @Test
    void testSearchQueryWords() {
        System.out.println(indexService.searchQueryWords("台湾 -美国", Factory.createPage(1, 20)));
    }

    @Test
    void testIndex() {
        ArrayList<Document> documents = new ArrayList<>();

        Document document = new Document();
        document.setText("Java是一门面向对象的编程语言，不仅吸收了C++语言的各种优点，还摒弃了C++里难以理解的多继承、指针等概念，因此Java语言具有功能强大和简单易用两个特征。Java语言作为静态面向对象编程语言的代表，极好地实现了面向对象理论，允许程序员以优雅的思维方式进行复杂的编程\n" +
                         "Java具有简单性、面向对象、分布式、健壮性、安全性、平台独立与可移植性、多线程、动态性等特点。Java可以编写桌面应用程序、Web应用程序、分布式系统和嵌入式系统应用程序等");
        document.setContent("{}");
        documents.add(document);

        Document document1 = new Document();
        document1.setText("C语言是一门面向过程的、抽象化的通用程序设计语言，广泛应用于底层开发。C语言能以简易的方式编译、处理低级存储器。C语言是仅产生少量的机器语言以及不需要任何运行环境支持便能运行的高效率程序设计语言。尽管C语言提供了许多低级处理的功能，但仍然保持着跨平台的特性，以一个标准规格写出的C语言程序可在包括类似嵌入式处理器以及超级计算机等作业平台的许多计算机平台上进行编译。");
        document1.setContent("{}");

        Document document2 = new Document();
        document2.setText("C++是一种计算机高级程序设计语言，由C语言扩展升级而产生，最早于1979年由本贾尼·斯特劳斯特卢普在AT&T贝尔工作室研发。\n" +
                          "C++既可以进行C语言的过程化程序设计，又可以进行以抽象数据类型为特点的基于对象的程序设计，还可以进行以继承和多态为特点的面向对象的程序设计。C++擅长面向对象程序设计的同时，还可以进行基于过程的程序设计。\n" +
                          "C++拥有计算机运行的实用性特征，同时还致力于提高大规模程序的编程质量与程序设计语言的问题描述能力。");
        document2.setContent("{}");
        documents.add(document2);

        Document document3 = new Document();
        document3.setText("JavaScript（简称“JS”） 是一种具有函数优先的轻量级，解释型或即时编译型的编程语言。虽然它是作为开发Web页面的脚本语言而出名，但是它也被用到了很多非浏览器环境中，JavaScript 基于原型编程、多范式的动态脚本语言，并且支持面向对象、命令式、声明式、函数式编程范式。");
        document3.setContent("{}");
        documents.add(document3);
        
        Document document4 = new Document();
        document4.setText("Go（又称 Golang）是 Google 的 Robert Griesemer，Rob Pike 及 Ken Thompson 开发的一种静态强类型、编译型语言。Go 语言语法与 C 相近，但功能上有：内存安全，GC（垃圾回收），结构形态及 CSP-style 并发计算。");
        document4.setContent("{}");
        documents.add(document4);

        System.out.println(indexService.index(documents));
    }

    @Test
    void testSearch() {
        System.out.println(indexService.searchSingleWord("Collection", Factory.createPage(1, 1000)));
    }
}
