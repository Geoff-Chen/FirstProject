app.controller('searchController',function($scope,searchService,$location){

	//定义searchMap初始格式
	$scope.searchMap={'keywords':'','category':'','brand':'','price':'',
        'pageNomber':1,'pageSize':20,'sort':'','sortField':'','spec':{}};

	//添加集合元素到searchMap中
	$scope.addSearchItem = function (key, value) {
		if(key == 'category' ||key == 'brand' ||key == 'price'){
			//如果添加的是分类，品牌，价格键值对，直接添加
            $scope.searchMap[key] = value;
		}else{
			//如果添加的是规格，存储到spec对象中
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();
    }

    //从searchMap中移除删除的元素
    $scope.removeSearchItem = function (key) {
        if(key == 'category' ||key == 'brand' ||key == 'price'){
            //如果添加的是分类，品牌，价格键值对，直接赋值为空
            $scope.searchMap[key] ="";
        }else{
            //如果添加的是规格，删除对应的键值对
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }

    //如果关键字发生变化，清空搜索集合
    $scope.cleanSearchMap = function () {
        $scope.searchMap.spec = null;
        $scope.searchMap.category = '';
        $scope.searchMap.brand = '';
        $scope.searchMap.price = '';
    }

    //判断当前页码是否为第一页
    $scope.isFirstPage = function () {
        if($scope.searchMap.pageNomber == 1){
            return true;
        }else{
            return false;
        }
    }

    //判断当前页是否为最后一页
    $scope.isLastPage = function () {
        if($scope.searchMap.pageNomber == $scope.resultMap.totalPage){
            return true;
        }else{
            return false;
        }
    }
    //根据页码进行查询
    $scope.queryByPage = function (pageNo) {
        if(pageNo < 1 || pageNo >$scope.resultMap.totalPage){
            return ;
        }
        $scope.searchMap.pageNomber = pageNo;
        $scope.search();

    }
    //排序查询
    $scope.sortSearch = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }
    //接收关键字参数，并进行查询
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
    //搜索
    $scope.search=function(){
	    $scope.searchMap.pageNomber = parseInt($scope.searchMap.pageNomber);
        searchService.search($scope.searchMap).success(
            function(response){
                $scope.resultMap=response;
                pageSearch();
            }
        );
    }

    //判断关键字中是否包含品牌信息
    $scope.keywordsIsBrand = function () {
        for(var i =0;i<$scope.resultMap.brandList.length;i++){
            //如果关键字中包含品牌信息
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>0){
                return true;
            }
        }
        return false;
    }

    //分页查询方法
    pageSearch = function () {
        //初始化分页栏属性集合
        $scope.pageList = [];
        //获取总页码数
        var totalPage = $scope.resultMap.totalPage;
        //定义起始页码数
        var beginPage = 1;
        //定义截止页码数
        var endPage = totalPage;
        //定义前省略号变量
        $scope.beforeDot = true;
        //定义后省略号变量
        $scope.afterDot = true;
        //定义当前页码数
        var pageNomber = $scope.searchMap.pageNomber;
        //如果总页码数大于5
        if(totalPage > 5){
            //如果当前页码数小于等于三
            if(pageNomber <=3){
                endPage = 5;
                $scope.beforeDot = false;
                //如果当前页码数是最后三页
            }else if(pageNomber +2 >= totalPage){
                beginPage = totalPage - 4;
                $scope.afterDot = false;
                //如果是中间页码。则起始位置是当前页减二，结束页码是当前页加二
            }else{
                beginPage = pageNomber - 2;
                endPage = pageNomber +2 ;
            }
        }else {//如果当前的总页数小于5
            $scope.beforeDot = false;
            $scope.afterDot = false;
        }
        //循环遍历页码数，添加到集合中
        for(var i = beginPage;i <= endPage;i++){
            $scope.pageList.push(i);
        }
    }
	
});