# PullToRefreshAttacher
* 支持嵌套滚动的下拉/上拉组件
* Demo里实现了两种方式，一种是RecyclerView, 一种是NestedScrollView, 对`PullToRefreshBase` 和 `LoadingLayout` 进行了重构，修复了一些bug
* 如果需要使用RecyclerView 的下拉刷新，那么直接使用 `PullToRefreshNeoRecyclerView`
* 您看到还有个`PullToRefreshRecyclerView`， 它继承自`PullToNestedRefreshBase`， 它的原理是直接接管了嵌套滚动的目标视图的`NestedScrollChild`接口，当下拉准备触发的时候会先执行嵌套滚动，使用起来没问题，但是不太建议使用这个方法，有更好的建议可以告诉我
* 如果需要使用NestedScrollView 的下拉刷新，请使用PullToRefershNestedScrollView
* 其他的和原作者的[PullToRefreshListView](https://github.com/chrisbanes/Android-PullToRefresh)一样，并未做改动