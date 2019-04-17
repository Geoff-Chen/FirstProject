package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 全局搜索的方法
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 导入数据到solr中
     * @param list
     */
    public void importList(List list);

    /**
     * 删除商品信息从solr中
     * @param goodsIdList
     */
    public void deleteByGoodsIds(List goodsIdList);
}
