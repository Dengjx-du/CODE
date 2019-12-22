package com.leyou.page.test;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestHtml {

    @Autowired
    private PageService pageService;
    @Test
    public void testCreateHtml(){
        List<Long> ids = Arrays.asList(3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        for (Long id : ids) {
            pageService.createHtml(id);
        }

    }
}
