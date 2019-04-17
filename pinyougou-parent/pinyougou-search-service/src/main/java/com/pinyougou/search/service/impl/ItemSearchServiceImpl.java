package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        //创建返回结果的map集合
        Map map = new HashMap();
        //添加商品结果集到map集合中
        map.putAll(searchList(searchMap));
        //添加分类结果集到map集合中
        map.put("categoryList", searchCategoryList(searchMap));
        //添加品牌及规格到map集合中
        String category = (String) searchMap.get("category");
        if (!"".equals(category)){//如果有分类信息
            map.putAll(searchSpecList(category));
        }else {//如果没有分类信息
            map.putAll(searchSpecList(searchCategoryList(searchMap).get(0)));
        }
        return map;
    }


    /**
     * 查询关键字搜索结果，并进行高亮显示的结果集
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map mapList = new HashMap();
         /*//查询所有数据列表
        Query query = new SimpleQuery("*:*");
        //获取参数中的数据，并在item_keywords中查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //将查询条件添加到query中
        query.addCriteria(criteria);
        //在solr中查询结果，并返回结果集
        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);
        //将返回的结果集添加到map集合中
        map.put("rows",items.getContent());
        //返回结果集*/

        //高亮显示条件查询
        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮所在的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮显示前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置高亮显示的后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮的条件封装到query对象中
        query.setHighlightOptions(highlightOptions);

        //1.将关键字查询的条件封装到query对象中
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //2.按照分类进行筛选，条件查询
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //3.按照品牌进行筛选，条件查询
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //4.按照价格进行筛选查询
        if(!"".equals(searchMap.get("price"))){
            String price = (String) searchMap.get("price");
            String[] prices = price.split("-");//将价格区间切割为数组
            //价格起始点判断设置
            if(!prices[0].equals("0")){
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //设置价格终点
            if(!prices[1].equals("*")){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //5.按照规格进行筛选，条件查询
        if (searchMap.get("spec") != null) {
            Map<String, String> map = (Map<String, String>) searchMap.get("spec");
            for (String key : map.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(map.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //6.排序查询
        String sortValue = (String) searchMap.get("sort");//ASC升序  DESC降序
        String sortField = (String) searchMap.get("sortField");//排序查询域
        if(sortValue != null && !sortValue.equals("")){
            //升序查询的方法
            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            //降序查询的方法
            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        //7.分页查询
        Integer pageNomber = (Integer) searchMap.get("pageNomber");
        //如果当前页值为空，则赋值为1
        if(pageNomber == null){
            pageNomber = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        //如果每页记录数为空，则赋值为20
        if (pageSize == null){
            pageSize =20;
        }
        query.setOffset((pageNomber-1)*pageSize);//设置分页查询起始位置
        query.setRows(pageSize);


        /*---------------------------  高亮查询方法    -------------------------------------*/
        //调用方法查询高亮显示对象
        HighlightPage<TbItem> items = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取高亮入口条件对象
        List<HighlightEntry<TbItem>> highlighted = items.getHighlighted();
        //循环遍历高亮结果集
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            //高亮结果列表（高亮域的个数）
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                //获取原生的结果对象
                TbItem entity = highlightEntry.getEntity();
                //用高亮结果集替换原来的结果对象
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }

        List<TbItem> rowsList = items.getContent();
        //返回高亮替换的商品信息集合
        mapList.put("rows",rowsList);
        //返回总页数
        int totalPages = items.getTotalPages();
        mapList.put("totalPage",totalPages);
        //返回总记录数
        long totalElements = items.getTotalElements();
        mapList.put("total",totalElements);
        return mapList;

    }

    /**
     * 查询关键字搜索分类结果集
     *
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery("*:*");
        //根据关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions options = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(options);
        //调用方法查询获取结果集
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取对应的分组结果集对象
        GroupResult<TbItem> itemCategory = groupPage.getGroupResult("item_category");
        //获取分页入口页
        Page<GroupEntry<TbItem>> groupEntries = itemCategory.getGroupEntries();
        //获取对应的结果集对象集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> itemGroupEntry : content) {
            list.add(itemGroupEntry.getGroupValue());
        }
        return list;
    }


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询搜索商品的品牌以及对应的规格属性
     *
     * @param category
     * @return
     */
    private Map searchSpecList(String category) {
        Map map = new HashMap();
        //获取缓存中搜索商品对应的模板ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null) {
            //从缓存中取出品牌列表，并添加到返回集合中
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);

            //从缓存中获取对应的规格列表，添加到返回结合中
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }

    /**
     * 导入数据到solr中
     * @param list
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除商品信息
     * @param goodsIdList
     */
    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品 ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
