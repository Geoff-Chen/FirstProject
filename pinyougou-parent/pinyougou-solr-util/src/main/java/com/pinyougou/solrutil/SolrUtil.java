package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData() {

        TbItemExample examle = new TbItemExample();
        TbItemExample.Criteria criteria = examle.createCriteria();
        criteria.andStatusEqualTo("1");
        //查询激活状态为1的所有商品信息
        List<TbItem> items = itemMapper.selectByExample(examle);

        System.out.println("----------商品列表开始展示-----------");
        for (TbItem item : items) {
            System.out.println(item.getId() + "" + item.getTitle() + "" + item.getPrice());
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);//给带注解的字段赋值
        }
        //将数据保存到solr中
        solrTemplate.saveBeans(items);
        //提交保存数据事务
        solrTemplate.commit();
        System.out.println("-----------商品列表展示结束----------------");
    }

    /**
     * 添加数据到solr中
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();
    }



}
