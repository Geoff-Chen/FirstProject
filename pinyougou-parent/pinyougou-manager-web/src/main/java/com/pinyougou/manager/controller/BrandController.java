package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;


@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();		
	}

	/**
	 * 分页查询
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int size){
		return brandService.findPage(page,size);
	}

	/**
	 * 添加商品信息
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand tbBrand){
		try {
			brandService.add(tbBrand);
			return new Result(true,"添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败！");
		}
	}


	/**
	 * 修改商品信息
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("update")
	public Result update(@RequestBody TbBrand tbBrand){
		try {
			brandService.update(tbBrand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	@RequestMapping("findOne")
	public TbBrand findOne(Long id){
		return brandService.findOne(id);
	}

	/**
	 * 删除选中商品信息
	 * @param ids
	 * @return
	 */
	@RequestMapping("delete")
	public Result delete(Long[] ids){
		try {
			brandService.delete(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}

	/**
	 * 搜索分页查询
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand brand,int page,Integer rows){
		return brandService.findPage(brand, page, rows);
	}

	/**
	 * 查询下拉列表属性
	 * @return
	 */
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
}
