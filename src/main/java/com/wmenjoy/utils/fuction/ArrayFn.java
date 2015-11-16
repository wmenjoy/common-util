package com.wmenjoy.utils.fuction;

/**
 * Array Functions

注意: 所有数组函数都可以用在 arguments 对象上. 然而, Underscore 函数的设计并不只是针对稀疏数组的.

first_.first(array, [n]) 别名: head, take 
返回数组 array 里的第一个元素. 如果传了参数 n 将返回数组里前 n 个元素.

_.first([5, 4, 3, 2, 1]);
=> 5
initial_.initial(array, [n]) 
返回一个数组里除了最后一个元素以外的所有元素. 在arguments对象上特别有用. 传参 n 将排除数组最后的 n 个元素.

_.initial([5, 4, 3, 2, 1]);
=> [5, 4, 3, 2]
last_.last(array, [n]) 
返回数组 array 里的最后一个元素. 传参 n 将返回 数组里的后 n 个元素.

_.last([5, 4, 3, 2, 1]);
=> 1
rest_.rest(array, [index]) 别名: tail, drop 
返回一个数组里除了第一个以外 剩余的 所有元素. 传参 index 将返回除了第 index 个元素以外剩余的所有元素.

_.rest([5, 4, 3, 2, 1]);
=> [4, 3, 2, 1]
compact_.compact(array) 
返回一个数组 array 除空(真值检验为false)后的副本. 在JavaScript里, false, null, 0, "", undefined 和 NaN 真值检验的结果都为false.

_.compact([0, 1, false, 2, '', 3]);
=> [1, 2, 3]
flatten_.flatten(array, [shallow]) 
将一个嵌套多层的数组 array (嵌套可以是任何层数)转换为只有一层的数组. 如果传参 shallow 为true, 数组只转换第一层.

_.flatten([1, [2], [3, [[4]]]]);
=> [1, 2, 3, 4];

_.flatten([1, [2], [3, [[4]]]], true);
=> [1, 2, 3, [[4]]];
without_.without(array, [*values]) 
返回一个除去所有 values 后的 array 副本.

_.without([1, 2, 1, 0, 3, 1, 4], 0, 1);
=> [2, 3, 4]
union_.union(*arrays) 
返回传入的多个数组 arrays 结合后的数组: 且所有数组元素都是唯一的, 传入的数组可以是一个或多个数组 arrays.

_.union([1, 2, 3], [101, 2, 1, 10], [2, 1]);
=> [1, 2, 3, 101, 10]
intersection_.intersection(*arrays) 
返回一个多个数组 arrays 的交集. 即返回的数组里每个元素, 都存在于参数 arrays 每个数组里.

_.intersection([1, 2, 3], [101, 2, 1, 10], [2, 1]);
=> [1, 2]
difference_.difference(array, *others) 
跟 without 相似, 但是返回的数组是 array 里跟别的数组 other 里不一样的元素.

_.difference([1, 2, 3, 4, 5], [5, 2, 10]);
=> [1, 3, 4]
uniq_.uniq(array, [isSorted], [iterator]) 别名: unique 
返回 array 去重后的副本, 使用 === 做相等测试. 如果您确定 array 已经排序, 给 isSorted 参数传如 true, 此函数将使用更快的算法. 如果要处理对象元素, 传参 iterator 来获取要对比的属性.

_.uniq([1, 2, 1, 3, 1, 4]);
=> [1, 2, 3, 4]
zip_.zip(*arrays) 
合并 arrays 里每一个数组的每个元素, 并保留对应位置. 在合并分开保存的数据时很有用. 如果你用来处理矩阵嵌套数组时, zip.apply 可以做类似的效果.

_.zip(['moe', 'larry', 'curly'], [30, 40, 50], [true, false, false]);
=> [["moe", 30, true], ["larry", 40, false], ["curly", 50, false]]

_.zip.apply(_, arrayOfRowsOfData);
=> arrayOfColumnsOfData
object_.object(list, [values]) 
把数组转换成对象. 传一个或多个 [key, value] 形式的数组, 或者一个包含key的数组和一个包含value的数组.

_.object(['moe', 'larry', 'curly'], [30, 40, 50]);
=> {moe: 30, larry: 40, curly: 50}

_.object([['moe', 30], ['larry', 40], ['curly', 50]]);
=> {moe: 30, larry: 40, curly: 50}
indexOf_.indexOf(array, value, [isSorted]) 
返回元素 value 在数组 array 里的索引位置, 如果元素没在数组 array 中, 将返回 -1. 此函数将使用原生的 indexOf 方法, 除非原生的方法无故消失或者被覆盖重写了, 才使用非原生的. 如果您要处理一个大型数组, 而且确定数组已经排序, 参数 isSorted 可以传 true, 函数将使用更快的二进制搜索来进行处理... 或者, 传一个数字作为 第三个参数, 以便于在指定索引之后开始寻找对应值.

_.indexOf([1, 2, 3], 2);
=> 1
lastIndexOf_.lastIndexOf(array, value, [fromIndex]) 
返回元素 value 在数组 arrry 里最后一次出现的索引位置, 如果元素没在数组 array 中, 将返回 -1. 如有可能, 此函数将使用原生的 lastIndexOf 方法. 传参 fromIndex 以便从指定索引开始寻找.

_.lastIndexOf([1, 2, 3, 1, 2, 3], 2);
=> 4
sortedIndex_.sortedIndex(list, value, [iterator]) 
为了保持 list 已经排好的顺序, 使用二进制搜索来检测 value 应该 插入到 list 里的所在位置的索引. 如果传入了一个 iterator , 它将用来计算每个值的排名, 包括所传的 value 参数.

_.sortedIndex([10, 20, 30, 40, 50], 35);
=> 3

var stooges = [{name: 'moe', age: 40}, {name: 'curly', age: 60}];
_.sortedIndex(stooges, {name: 'larry', age: 50}, 'age');
=> 1
range_.range([start], stop, [step]) 
一个灵活创建范围内整数数组的函数, each 和 map 循环整合的简便版本. 如果省略start 参数, 默认为 0; step 默认为 1. 返回一个数组, 包含从 start 到 stop (不包含stop) 范围内, 以 step 递增(减)的整数.

_.range(10);
=> [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
_.range(1, 11);
=> [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
_.range(0, 30, 5);
=> [0, 5, 10, 15, 20, 25]
_.range(0, -10, -1);
=> [0, -1, -2, -3, -4, -5, -6, -7, -8, -9]
_.range(0);
=> []
 * @author liujinliang5
 *
 */
public class ArrayFn {

}
