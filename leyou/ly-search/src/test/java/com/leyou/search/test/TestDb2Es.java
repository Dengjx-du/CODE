package com.leyou.search.test;

import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.SpuDTO;
import com.leyou.item.client.ItemClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDb2Es {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsRepository repository;

    /**
     * 把数据库的数据 导入到es中
     */
    @Test
    public void testDb2Es(){
        int page = 1;
        int size = 50;
        while (true){
//        获取spuDTO对象
            PageResult<SpuDTO> pageResult = itemClient.findSpuByPage(page, size, null, true);
            if(pageResult == null || CollectionUtils.isEmpty(pageResult.getItems())){
                break;
            }
            List<SpuDTO> spuDTOList = pageResult.getItems();

            List<Goods> goodsList = new ArrayList<>();
            for (SpuDTO spuDTO : spuDTOList) {
//        创建goods对象
                Goods goods = searchService.createGoods(spuDTO);
                goodsList.add(goods);
            }

//        批量 把goods对象写入es
            repository.saveAll(goodsList);
            if(spuDTOList.size() < size){
                break;
            }
            page ++;
        }

    }
}
