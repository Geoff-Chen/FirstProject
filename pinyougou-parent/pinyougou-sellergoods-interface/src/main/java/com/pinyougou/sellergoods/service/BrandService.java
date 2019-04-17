package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();

	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);

	/**
	 * 添加品牌信息的方法
	 * @param tbBrand
	 */
	public void add (TbBrand tbBrand);

	/**
	 * 修改商品信息
	 * @param tbBrand
	 */
	public void update(TbBrand tbBrand);

	/**
	 * 根据id查询商品信息
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);

	/**
	 * 删除商品信息
	 * @param ids
	 */
	public void delete(Long[] ids);

	//搜索分页查询
	public PageResult findPage(TbBrand tbBrand,int pageNum, int pageSize);

	/**
	 * 查询下拉列表属性
	 * @return
	 */
	public List<Map> selectOptionList();
}
