package com.github.howieyoung91.farseer.pojo;

import lombok.Data;

@Data
public class MyPage {
    private final int page;
    private final int count;

    public MyPage(int page, int count) {
        this.page = page;
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }
}
