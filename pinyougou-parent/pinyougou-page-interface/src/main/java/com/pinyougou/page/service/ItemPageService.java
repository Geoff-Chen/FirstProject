package com.pinyougou.page.service;

import java.util.Map;

public interface ItemPageService {

    /**
     * 根据ID生成对应的静态页面
     * @param goodsId
     * @return
     */
    public boolean genItemHtml(Long goodsId);


    /**
     * 根据id删除对应静态页面
     * @param goodsIds
     * @return
     */
    public boolean deleteItemHtml(Long[] goodsIds);



}
