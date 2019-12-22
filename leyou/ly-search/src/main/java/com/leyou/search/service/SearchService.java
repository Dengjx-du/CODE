package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.*;
import com.leyou.item.client.ItemClient;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    /**
     * 商品微服务的 feign接口
     */
    @Autowired
    private ItemClient itemClient;
    /**
     * 创建goods对象
     * @param spuDTO
     * @return
     */
    public Goods createGoods(SpuDTO spuDTO){
        Long spuId = spuDTO.getId();
//        存放全文检索的内容 name ， barandName ， categoryName
        String all = spuDTO.getName() +","+ spuDTO.getBrandName()+","+spuDTO.getCategoryName();
//        远程调用获取sku的集合
        List<SkuDTO> skuDTOList = itemClient.findSkuListBySpuId(spuId);
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        for (SkuDTO skuDTO : skuDTOList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",skuDTO.getId());
            map.put("price",skuDTO.getPrice());
            map.put("image", StringUtils.substringBefore(skuDTO.getImages(),","));
            map.put("title",skuDTO.getTitle());
            skuMapList.add(map);
        }
//        Set<Long> price = new HashSet<>();
//        for (SkuDTO skuDTO : skuDTOList) {
//            price.add(skuDTO.getPrice());
//        }
        Set<Long> price = skuDTOList.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
        String skuListJson = JsonUtils.toString(skuMapList);
//        获取规格参数的名字 集合
        List<SpecParamDTO> paramDTOList = itemClient.findParamList(null, spuDTO.getCid3(), true);
//         获取商品详情
        SpuDetailDTO spuDetailDTO = itemClient.findSpuDetailBySpuId(spuId);
//        获取通用的规格参数值,json字符串
        String genericSpec = spuDetailDTO.getGenericSpec();
        Map<Long, Object> genericMap = JsonUtils.toMap(genericSpec, Long.class, Object.class);
//        获取特殊的规格参数值
        String specialSpec = spuDetailDTO.getSpecialSpec();
        Map<Long,List<String>> specialMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<String>>>() {
        });
//        es中存储的规格参数的名字 和 值的对应
        Map<String,Object> specsMap = new HashMap<>();

//        循环 参数的名字
        for (SpecParamDTO specParamDTO : paramDTOList) {
            Long id = specParamDTO.getId();
            String key = specParamDTO.getName();
            Object val = null;
            if(specParamDTO.getGeneric()){
//                通用的规格 参数
                val = genericMap.get(id);
            }else{
//                特有的规格参数
                val = specialMap.get(id);
            }
//            把数字类型的 区间段写入spu的数据中
            if(specParamDTO.getIsNumeric()){
                val = this.chooseSegment(val,specParamDTO);
            }
            specsMap.put(key,val);
        }

        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setCategoryId(spuDTO.getCid3());
        goods.setBrandId(spuDTO.getBrandId());
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
        goods.setSubTitle(spuDTO.getSubTitle());
        goods.setAll(all);
        goods.setSkus(skuListJson);
        goods.setPrice(price);
        goods.setSpecs(specsMap);
        return goods;

    }

    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    @Autowired
    private ElasticsearchTemplate esTemplate;
    /**
     * 根据用户输的关键词进行查询
     * @param request
     * @return
     */
    public PageResult<GoodsDTO> search(SearchRequest request) {
//      用户输入的关键词
        String key = request.getKey();

//       创建原生查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//       添加结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
//        添加搜索关键字
        queryBuilder.withQuery(basicQuery(request));
//        添加分页信息
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
//        把查询内容发送到es的服务端
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
//        处理结果
        List<Goods> goodsList = aggregatedPage.getContent();
        if(CollectionUtils.isEmpty(goodsList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<GoodsDTO> goodsDTOList = BeanHelper.copyWithCollection(goodsList, GoodsDTO.class);
        return new PageResult<GoodsDTO>(goodsDTOList,
                aggregatedPage.getTotalElements(),
                Long.valueOf(String.valueOf(aggregatedPage.getTotalPages())));
    }

    /**
     * 查询过滤条件
     * es的聚合操作
     * @param request
     * @return
     */
    public Map<String, List<?>> searchFilter(SearchRequest request) {
        Map<String, List<?>> filterMap = new LinkedHashMap<>();
//        用户输入的关键词
        String key = request.getKey();

//        构建原生查询分析器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        添加 返回的结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
//        添加 关键词 查询
        queryBuilder.withQuery(basicQuery(request));
//        添加分页配置
        queryBuilder.withPageable(PageRequest.of(0,1));
//         添加 聚合配置
//        添加 分类的聚合配置
        String categoryAggName = "categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("categoryId"));
//         添加品牌的聚合配置
        String brandAggName="brandAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

//        发送查询请求，并获取返回值
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
//        获取 聚合的结果
        Aggregations aggregations = aggregatedPage.getAggregations();
//        分类的聚合结果
        LongTerms categoryAgg = aggregations.get(categoryAggName);
        List<Long> ids = handlerCategoryName(categoryAgg,filterMap);
//        品牌的聚合结果
        LongTerms brandAgg = aggregations.get(brandAggName);
        handlerBrandName(brandAgg,filterMap);
//        做规格参数的聚合，只有当分类的聚合结果只有1个时
        if(!CollectionUtils.isEmpty(ids) && ids.size()==1){
//            规格参数聚合
            handlerSpecAgg(ids.get(0),request,filterMap);
        }
        return filterMap;
    }

    /**
     * 获取规格参数的聚合结果
     * @param cid
     * @param request
     * @param filterMap
     */
    private void handlerSpecAgg(Long cid, SearchRequest request, Map<String, List<?>> filterMap) {
//        获取分类对应的规格参数 名字
        List<SpecParamDTO> paramList = itemClient.findParamList(null, cid, true);
//        创建原生查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        设置结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
//        设置关键词搜索
        String key = request.getKey();
        queryBuilder.withQuery(basicQuery(request));
//        设置分页
        queryBuilder.withPageable(PageRequest.of(0,1));
//        设置规格参数的聚合
        for (SpecParamDTO specParamDTO : paramList) {
            String name = specParamDTO.getName();
            String fieldName = "specs."+name;
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field(fieldName));
        }
//        发送查询请求
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
//        处理结果，得到聚合结果
        Aggregations aggregations = aggregatedPage.getAggregations();
        for (SpecParamDTO specParamDTO : paramList) {
            String name = specParamDTO.getName();
//            规格参数的 聚合 结果
             StringTerms specAgg = aggregations.get(name);
//             从结果中获取buckets
            List<String> specAggResult = specAgg.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
            filterMap.put(name,specAggResult);
        }
        
    }

    /**
     * 获取品牌的名字
     * @param brandAgg
     * @param filterMap
     */
    private void handlerBrandName(LongTerms brandAgg, Map<String, List<?>> filterMap) {
        List<LongTerms.Bucket> buckets = brandAgg.getBuckets();
        List<Long> brandIdList = buckets.stream().map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
        List<BrandDTO> brandDTOList = itemClient.findBrandListByIds(brandIdList);
        filterMap.put("品牌",brandDTOList);
    }

    /**
     * 获取 分类的名字
     * @param categoryAgg
     * @param filterMap
     */
    private List<Long> handlerCategoryName(LongTerms categoryAgg, Map<String, List<?>> filterMap) {
        List<Long> categoryIdList = new ArrayList<>();
        List<LongTerms.Bucket> buckets = categoryAgg.getBuckets();
        for (LongTerms.Bucket bucket : buckets) {
            long categoryId = bucket.getKeyAsNumber().longValue();
            categoryIdList.add(categoryId);
        }
        if(CollectionUtils.isEmpty(categoryIdList)){
            return null;
        }
//        获取分类对象信息
        List<CategoryDTO> categoryDTOList = itemClient.findCategoryListByIds(categoryIdList);
//        把分类信息集合放入 返回的map中
        filterMap.put("分类",categoryDTOList);
        return categoryIdList;
    }

    public QueryBuilder basicQuery(SearchRequest request){
        String key = request.getKey();
//        创建bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        must条件
        boolQuery.must(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
//        获取用户的过滤项
        Map<String, String> filter = request.getFilter();
        if(!CollectionUtils.isEmpty(filter)){
            for (String name : filter.keySet()) {
                String fieldName = "specs."+ name;
                if(name.equals("分类")){
                    fieldName = "categoryId";
                }else if(name.equals("品牌")){
                    fieldName = "brandId";
                }
                String val = filter.get(name);
                boolQuery.filter(QueryBuilders.termQuery(fieldName,val));
            }
        }
        return boolQuery;
    }
}
