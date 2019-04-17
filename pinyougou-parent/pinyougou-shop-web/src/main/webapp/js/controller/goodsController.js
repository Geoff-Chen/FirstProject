//控制层
app.controller('goodsController', function ($scope, $controller,$location,
                                            goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
/*
    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }*/

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }

    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();

        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert("添加成功！");
                    $scope.entity = {};
                    editor.html("");//清空富文本编辑器
                } else {
                    alert(response.message);
                }
            }
        )
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };


    //上传图片到图片服务器
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {//如果保存成功，获取保存图片保存路径
                    $scope.image_entity.url = response.message;//将图片URL绑定到对象中
                } else {
                    alert(response.message);
                }
            }).error(
            function () {
                alert("上传文件发生错误！")
            }
        )
    };

    //定义页面实体类对象的初始化结构
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};
    //在列表中添加一条图片记录
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };
    //移除一条图片记录
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
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

    //更新模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //根据选择的值查询模板id
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId; //更新模板 ID
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

    //保存选中的规格选项
    $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectBykey(
            $scope.entity.goodsDesc.specificationItems, "attributeName", name
        );
        if (object == null) {
            //如果集合中没有对应的属性，则创建集合初始化格式
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName": name, "attributeValue": [value]});
        } else {
            //勾选规格属性，向集合中添加元素
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {
                //取消勾选规格选项，从集合中移除该元素
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                //如果一个规格下的所有属性都被移除，则删除整个规格集合
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        }
    }

    //利用克隆生成规格属性表
    $scope.createItemList = function () {
        //声明原始SKU集合
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
        //获得勾选的规格属性表
        var specList = $scope.entity.goodsDesc.specificationItems;
        //遍历规格属性列表

        for (var x = 0; x < specList.length; x++) {
            //获取规格属性名
            var specName = specList[x].attributeName;
            //获取规格属性值
            var specValue = specList[x].attributeValue;
            //创建一个行的sku列表数组
            var newList =[];

            //遍历sku列表
            for (var y = 0; y < $scope.entity.itemList.length; y++) {
                var oldSKU = $scope.entity.itemList[y];

                //遍历规格属性值
                for (var z = 0; z < specValue.length; z++) {
                    //克隆sku对象
                    var newSKU = JSON.parse(JSON.stringify(oldSKU));
                    //增加规格属性
                    newSKU.spec[specName] = specValue[z];
                    //将最后组成的SKU添加到集合中
                    newList.push(newSKU);
                }
            }
            //将最后的集合赋值给itemList，以便下次添加组合
            $scope.entity.itemList = newList;
        }
    }

    $scope.status=['未审核','已审核','审核未通过','已关闭'];//定义商品状态数组
    $scope.itemCatList=[];//商品分类列表
    //查询商品分类列表数据
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for(var i =0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        )
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

    $scope.checkAttributeValue=function(specName,optionName){
        var items= $scope.entity.goodsDesc.specificationItems;
        var object =$scope.searchObjectBykey(items,'attributeName', specName);

        if(object!=null){
            if(object.attributeValue.indexOf(optionName)>=0){//如果能够查询到规格选项
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    $scope.save=function(){
        $scope.entity.goodsDesc.introduction=editor.html();

        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    alert("保存成功");
                    location.href='goods.html';
                }else{
                    alert(response.message);
                }
            }
        );
    }



});	
