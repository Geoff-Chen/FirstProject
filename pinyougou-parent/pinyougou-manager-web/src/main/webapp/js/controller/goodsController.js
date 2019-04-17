 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,itemCatService,$location,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //商品分类一级目录下拉菜单查询
    $scope.selectItemCatList1 = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCatList1 = response;
                $scope.itemCatList3 = null;
            }
        );
    };


    //商品分类级联二级目录
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        $scope.itemCatList3 = null;
        //根据选择的值查询二级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList2 = response;
            });
    });

    //商品分类级联三级目录
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //根据选择的值查询三级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList3 = response;
            });
    });

    //获取扩展属性
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//获取类型模板
                $scope.typeTemplate.brandIds =
                    JSON.parse($scope.typeTemplate.brandIds);//品牌列表
                //如果是增加商品,从模板表中查询
                if( $location.search()['id']==null ){
                    $scope.entity.goodsDesc.customAttributeItems=
                        JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            });

        //获取模板对应的规格属性
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        )
    });

    //更新模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //根据选择的值查询模板id
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId; //更新模板 ID
            });
    });

    $scope.status=['未审核','已审核','审核未通过','已关闭'];

    $scope.itemCatList=[];//商品分类列表
    //查询商品分类列表
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );

    };

    //更新状态
    $scope.updateStatus=function(status){
        goodsService.updateStatus( $scope.selectIds ,status).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新页面
                    $scope.selectIds=[];
                }else{
                    alert(response.message);
                }
            }
        );
    }

    $scope.findOne =function () {
        var id = $location.search()['id'];//获取参数值
        if(id == null){
            return ;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //商品图片
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
                //扩展属性
                $scope.entity.goodsDesc.customAttributeItems
                    =JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //规格选择
                $scope.entity.goodsDesc.specificationItems
                    =JSON.parse($scope.entity.goodsDesc.specificationItems);
                //转换sku列表中的规格对象
                for(var i=0;i< $scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec= JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        )
    }

});	
