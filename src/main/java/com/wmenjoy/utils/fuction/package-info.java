package com.wmenjoy.utils.fuction;

/***
    这个包提供类似javascript的方便的过滤功能
    1、集合操作
    2、
    
    集合函数 (数组或对象)

each_.each(list, iterator, [context]) 别名: forEach 
对一个 list 的所有元素进行迭代, 对每一个元素执行 iterator 函数. iterator 和 context 对象绑定, 如果传了这个参数. 每次 iterator 的调用将会带有三个参数: (element, index, list). 如果 list 是一个 JavaScript 对象, iterator 的参数将会是 (value, key, list). 如果有原生的 forEach 函数就会用原生的代替.

_.each([1, 2, 3], alert);
=> 依次alert每个数字...
_.each({one: 1, two: 2, three: 3}, alert);
=> 依此alert每个数字...
Note: Collection functions work on arrays, objects, and array-like objects such as arguments, NodeList and similar. But it works by duck-typing, so avoid passing objects with a numeric length property. It's also good to note that an each loop cannot be broken out of — to break, use _.find instead.

map_.map(list, iterator, [context]) 别名: collect 
映射 list 里的每一个值, 通过一个转换函数(iterator)产生一个新的数组. 如果有原生的 map 函数, 将用之代替. 如果 list 是一个 JavaScript 对象, iterator的参数将会是 (value, key, list).

_.map([1, 2, 3], function(num){ return num * 3; });
=> [3, 6, 9]
_.map({one: 1, two: 2, three: 3}, function(num, key){ return num * 3; });
=> [3, 6, 9]
reduce_.reduce(list, iterator, memo, [context]) 别名: inject, foldl 
也被称为 inject 和 foldl, reduce 将一个 list 里的所有值归结到一个单独的数值. Memo 是归结的初始值, 而且每一步都由 iterator返回. 迭代器 iterator 会传入四个参数: memo, value 和迭代的索引index (或 key), 最后还有对整个 list 的一个引用.

var sum = _.reduce([1, 2, 3], function(memo, num){ return memo + num; }, 0);
=> 6
reduceRight_.reduceRight(list, iterator, memo, [context]) 别名: foldr 
reduce的右结合版本. 如果可能, 将调用 JavaScript 1.8 版本原生的 reduceRight. Foldr 在 JavaScript 中并没那么有用, 人们对它的评价并不好.

var list = [[0, 1], [2, 3], [4, 5]];
var flat = _.reduceRight(list, function(a, b) { return a.concat(b); }, []);
=> [4, 5, 2, 3, 0, 1]
find_.find(list, iterator, [context]) 别名: detect 
在 list 里的每一项进行查找, 返回第一个符合 测试(iterator)条件的元素. 此函数只返回第一个符合条件的元素, 并不会遍历整个list.

var even = _.find([1, 2, 3, 4, 5, 6], function(num){ return num % 2 == 0; });
=> 2
filter_.filter(list, iterator, [context]) 别名: select 
在 list 里的每一项进行查找, 返回一个符合测试 (iterator) 条件的所有元素的集合. 如果存在原生的 filter 方法, 将采用原生的.

var evens = _.filter([1, 2, 3, 4, 5, 6], function(num){ return num % 2 == 0; });
=> [2, 4, 6]
where_.where(list, properties) 
遍历 list 里的每一个值, 返回包含 properties 里所有 key-value 组合的对象的数组.

_.where(listOfPlays, {author: "Shakespeare", year: 1611});
=> [{title: "Cymbeline", author: "Shakespeare", year: 1611},
    {title: "The Tempest", author: "Shakespeare", year: 1611}]
findWhere_.findWhere(list, properties) 
Looks through the list and returns the first value that matches all of the key-value pairs listed in properties.

If no match is found, or if list is empty, undefined will be returned.

_.findWhere(publicServicePulitzers, {newsroom: "The New York Times"});
=> {year: 1918, newsroom: "The New York Times",
  reason: "For its public service in publishing in full so many official reports,
  documents and speeches by European statesmen relating to the progress and
  conduct of the war."}
reject_.reject(list, iterator, [context]) 
返回在 list 不能通过测试 (iterator) 的所有元素的集合. 与 filter 相反.

var odds = _.reject([1, 2, 3, 4, 5, 6], function(num){ return num % 2 == 0; });
=> [1, 3, 5]
every_.every(list, [iterator], [context]) 别名: all 
如果所有在 list 里的元素通过了 iterator 的测试, 返回 true. 如果存在则使用原生的 every 方法.

_.every([true, 1, null, 'yes'], _.identity);
=> false
some_.some(list, [iterator], [context]) 别名: any 
如果任何 list 里的任何一个元素通过了 iterator 的测试, 将返回 true. 一旦找到了符合条件的元素, 就直接中断对list的遍历. 如果存在, 将会使用原生的 some 方法.

_.some([null, 0, 'yes', false]);
=> true
contains_.contains(list, value) 别名: include 
如果 value 存在与 list 里, 返回 true. 如果 list 是一个数组, 内部会使用 indexOf.

_.contains([1, 2, 3], 3);
=> true
invoke_.invoke(list, methodName, [*arguments]) 
在 list 里的每个元素上调用名为 methodName 的函数. 任何附加的函数传入, invoke 将会转给要调用的函数.

_.invoke([[5, 1, 7], [3, 2, 1]], 'sort');
=> [[1, 5, 7], [1, 2, 3]]
pluck_.pluck(list, propertyName) 
一个 map 通常用法的简便版本: 提取一个集合里指定的属性值.

var stooges = [{name: 'moe', age: 40}, {name: 'larry', age: 50}, {name: 'curly', age: 60}];
_.pluck(stooges, 'name');
=> ["moe", "larry", "curly"]
max_.max(list, [iterator], [context]) 
返回 list 里最大的元素. 如果传入了 iterator, 它将用来比较每个值.

var stooges = [{name: 'moe', age: 40}, {name: 'larry', age: 50}, {name: 'curly', age: 60}];
_.max(stooges, function(stooge){ return stooge.age; });
=> {name: 'curly', age: 60};
min_.min(list, [iterator], [context]) 
返回 list 里最小的元素. 如果传入了 iterator, 它将用来比较每个值.

var numbers = [10, 5, 100, 2, 1000];
_.min(numbers);
=> 2
sortBy_.sortBy(list, iterator, [context]) 
返回一个经过排序的 list 副本, 用升序排列 iterator 返回的值. 迭代器也可以用字符串的属性来进行比较(如length).

_.sortBy([1, 2, 3, 4, 5, 6], function(num){ return Math.sin(num); });
=> [5, 4, 6, 3, 1, 2]
groupBy_.groupBy(list, iterator) 
把一个集合分为多个集合, 通过 iterator 返回的结果进行分组. 如果 iterator 是一个字符串而不是函数, 那么将使用 iterator 作为各元素的属性名来对比进行分组.

_.groupBy([1.3, 2.1, 2.4], function(num){ return Math.floor(num); });
=> {1: [1.3], 2: [2.1, 2.4]}

_.groupBy(['one', 'two', 'three'], 'length');
=> {3: ["one", "two"], 5: ["three"]}
indexBy_.indexBy(list, iteratee, [context]) 
Given a list, and an iteratee function that returns a key for each element in the list (or a property name), returns an object with an index of each item. Just like groupBy, but for when you know your keys are unique.

var stooges = [{name: 'moe', age: 40}, {name: 'larry', age: 50}, {name: 'curly', age: 60}];
_.indexBy(stooges, 'age');
=> {
  "40": {name: 'moe', age: 40},
  "50": {name: 'larry', age: 50},
  "60": {name: 'curly', age: 60}
}
countBy_.countBy(list, iterator) 
把一个数组分组并返回每一组内对象个数. 与 groupBy 相似, 但不是返回一组值, 而是组内对象的个数.

_.countBy([1, 2, 3, 4, 5], function(num) {
  return num % 2 == 0 ? 'even': 'odd';
});
=> {odd: 3, even: 2}
shuffle_.shuffle(list) 
返回一个随机乱序的 list 副本, 使用 Fisher-Yates shuffle 来进行随机乱序.

_.shuffle([1, 2, 3, 4, 5, 6]);
=> [4, 1, 6, 3, 5, 2]
sample_.sample(list, [n]) 
从 list 里进行随机取样. 传一个数字 n 来决定返回的样本个数, 否则只返回一个样本.

_.sample([1, 2, 3, 4, 5, 6]);
=> 4

_.sample([1, 2, 3, 4, 5, 6], 3);
=> [1, 6, 2]
toArray_.toArray(list) 
将一个 list (任何可以被进行迭代的对象)转换成一个数组. 在转换 arguments 对象时非常有用.

(function(){ return _.toArray(arguments).slice(1); })(1, 2, 3, 4);
=> [2, 3, 4]
size_.size(list) 
返回 list 里所有元素的个数.

_.size({one: 1, two: 2, three: 3});
=> 3
partition_.partition(array, predicate) 
Split array into two arrays: one whose elements all satisfy predicate and one whose elements all do not satisfy predicate.

_.partition([0, 1, 2, 3, 4, 5], isOdd);
=> [[1, 3, 5], [0, 2, 4]]
Array Functions

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
*/