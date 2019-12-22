package com.leyou.page.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.DTO.*;
import com.leyou.item.client.ItemClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;
    /**
     * 获取静态页需要的数据
     * @param spuId
     */
    public Map<String,Object> loadData(Long spuId) {
        Map<String,Object> map = new HashMap<>();
//         获取spu的信息
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
//        categories 分类的集合
        List<CategoryDTO> categories = itemClient.findCategoryListByIds(spuDTO.getCategoryIds());
//        brand 品牌对象
        BrandDTO brand = itemClient.findBrandById(spuDTO.getBrandId());
//        spuName
        String spuName = spuDTO.getName();
//        subTitle
        String subTitle = spuDTO.getSubTitle();
//        detail   spuDetail
        SpuDetailDTO detail = itemClient.findSpuDetailBySpuId(spuId);
//        skus
        List<SkuDTO> skus = itemClient.findSkuListBySpuId(spuId);
//        specs
        List<SpecGroupDTO> groupDTOList = itemClient.findSpecsByCid(spuDTO.getCid3());
        map.put("categories",categories);
        map.put("brand",brand);
        map.put("spuName",spuName);
        map.put("subTitle",subTitle);
        map.put("detail",detail);
        map.put("skus",skus);
        map.put("specs",groupDTOList);
        return map;
    }

    @Autowired
    private TemplateEngine templateEngine;
    String filePath = "C:\\workspace\\heima-jee109\\nginx-1.12.2\\html\\item";
    /**
     * 生成静态页
     */
    public void createHtml(Long spuId){
//      获取动态数据
        Map<String, Object> data = this.loadData(spuId);
//        context 上下文
        Context context = new Context();
//        存放动态数据
        context.setVariables(data);
//        模板解析器,不需要设置，自动配置好了
//        templateEngine 引擎
        File dir = new File(filePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,spuId+".html");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
//            生成静态页面
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("生成静态页面失败，spuid:{}",spuId);
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }

    }

    /**
     * 删除静态页
     * @param spuId
     */
    public void removeHtml(Long spuId) {
        File dir = new File(filePath);
        if(dir.exists()){
            File file = new File(dir, spuId + ".html");
            file.delete();
        }
    }
}
