package com.pinyougou.solrutil;

import com.pinyougou.pojo.TbItem;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations="classpath:spring/applicationContext-solr.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class Test {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 删除solr中所有信息
     */
    @org.junit.Test

    public void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 根据主键删除信息
     */
    @org.junit.Test
    public void testDelete(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    /**
     * 根据主键进行查询
     */
    @org.junit.Test
    public void testFindOne(){
        TbItem item = solrTemplate.getById(1, TbItem.class);
        System.out.println(item.getTitle());
    }



}
