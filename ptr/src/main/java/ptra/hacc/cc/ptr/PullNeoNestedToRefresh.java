package ptra.hacc.cc.ptr;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hale Yang on 2017/9/13.
 * This is an nested scroll pull to refresh view
 */

public abstract class PullNeoNestedToRefresh<E extends View> extends PullToRefreshBase<E> implements NestedScrollingChild, NestedScrollingParent {

    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private NestedScrollingParentHelper mNestedScorllingParentHelper;

    /*
     * 获取滚动后所消耗的距离
     */
    private int[] mParentConmused = new int[2];
    /*
     * 获取滚动消耗的偏移量
     */
    private int[] mParentOffsetWindow = new int[2];
    private int mTotalUnConmused;



    public PullNeoNestedToRefresh(Context context) {
        super(context);
        init(context);
    }

    public PullNeoNestedToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullNeoNestedToRefresh(Context context, Mode mode) {
        super(context, mode);
        init(context);
    }

    public PullNeoNestedToRefresh(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
        init(context);
    }

    private void init(Context context){
        setNestedScrollingEnabled(true);
    }


    /*
    * 这个模式下，我们默认不拦截处理下拉手势，通过nested scroll 来处理下拉拖动和释放，如果这里重写了，会和nested scroll中的处理发生冲突，
    * 建议在没有嵌套滚动视图里再重写此方法
    * */
    @Override
    protected  boolean isReadyForPullStart() {
        return false;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return false;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !isRefreshing();
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        getNestedScrollingParentHelper().onNestedScrollAccepted(child, target, axes);
        startNestedScroll(axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        if(getState() == State.RELEASE_TO_REFRESH && (mOnRefreshListener != null || mOnRefreshListener2 != null)){
            setState(State.REFRESHING, true);
        }else if(getState() != State.RESET){
            setState(State.RESET);
        }
        mTotalUnConmused = 0;
        stopNestedScroll();
        getNestedScrollingParentHelper().onStopNestedScroll(child);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetWindow);
        if((dxUnconsumed < 0 ||dyUnconsumed < 0)
                && getMode().showHeaderLoadingLayout()
                && mTotalUnConmused <= 0) {
            mCurrentMode = Mode.PULL_FROM_START;
            switch (getPullToRefreshScrollDirection()) {
                case VERTICAL:
                    /*
                    * 滚动结束剩余消费
                    * */
                    int dy = dyUnconsumed + mParentOffsetWindow[1];
                    if (dy < 0) {
                        mTotalUnConmused += dy;
                        pullWithNestedOver(mTotalUnConmused);
                    }
                    break;
                case HORIZONTAL:
                    int dx = dxUnconsumed + mParentOffsetWindow[0];
                    if(dx < 0){
                        mTotalUnConmused += dx;
                        pullWithNestedOver(mTotalUnConmused);
                    }
                    break;
            }
        }else if(getMode().showFooterLoadingLayout()){
            mCurrentMode = Mode.PULL_FROM_END;
            switch (getPullToRefreshScrollDirection()){
                case VERTICAL:
                    if(dyConsumed == 0){
                        mTotalUnConmused += dyUnconsumed;
                        pullWithNestedOver(mTotalUnConmused);
                    }
                    break;
                case HORIZONTAL:
                    if(dyConsumed == 0){
                        mTotalUnConmused += dxUnconsumed;
                        pullWithNestedOver(mTotalUnConmused);
                    }
                    break;
            }

        }
    }

    /**
     * 如果target是一个nested child ,那么它必须遵循{@link NestedScrollingChild}的处理逻辑，
     * 当调用此方法的时候，说明target想滚动parent所嵌套的那个view ,那么此时如果这个view是已经滚动过的，
     * 这个动作是需要回滚这个view,那么parent的处理此方法的时候将直接回滚而不等到调用了滚动方法后再去滚动
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        int offset[] = new int[2];
        /*
         *向上移动
         *并且嵌套滚动已经向下执行过
         */
        if(dy > 0 && mTotalUnConmused <= 0 ){
            switch (getPullToRefreshScrollDirection()){
                case VERTICAL:
                    if(dy > mTotalUnConmused) {
                        consumed[1] = -mTotalUnConmused;
                        mTotalUnConmused = 0;
                    }else {
                        consumed[1] = dy;
                        mTotalUnConmused += dy;
                    }
                    int[] parentConsumedY = mParentConmused;
                    pullWithNestedOver(mTotalUnConmused);
                    dispatchNestedPreScroll(dx, dy - consumed[1], parentConsumedY, null);
                    consumed[0] += parentConsumedY[0];
                    consumed[1] += parentConsumedY[1];
                    break;
                case HORIZONTAL:
                    if(dx > mTotalUnConmused){
                        consumed[0] = -mTotalUnConmused;
                        mTotalUnConmused = 0;
                    }else {
                        consumed[0] = dx;
                        mTotalUnConmused += dx;
                    }
                    int parentConsumedX[] = mParentConmused;
                    pullWithNestedOver(mTotalUnConmused);
                    dispatchNestedPreScroll(dx - consumed[0], dy, parentConsumedX, null);
                    consumed[0] += parentConsumedX[0];
                    consumed[1] += parentConsumedX[1];
                    break;
            }

        }
    }

    /**
     *这里我们直接交给真正的parent去处理fling,我们是拖动刷新，所以这里不能在fling里去执行pull
     */
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
       return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     *这里我们直接交给真正的parent去处理fling,我们是拖动刷新，所以这里不能在fling里去执行pull
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    /**
     *嵌套滚动中给下拉刷新消费
     */
    private void pullWithNestedOver(int scrollConsumed){
//            mCurrentMode = Mode.PULL_FROM_START;
        if(mCurrentMode.getIntValue() ==  Mode.PULL_FROM_START.getIntValue()) {
            int headerDimension = getHeaderSize();
            int neoScrollValue = (int) (scrollConsumed / FRICTION);
            setHeaderScroll(neoScrollValue);
            onPullChange(scrollConsumed, headerDimension);
        }else if(mCurrentMode.getIntValue() == Mode.PULL_FROM_END.getIntValue()){
            int footerDimension = getFooterSize();
            int neoScrollValue = (int) (scrollConsumed / FRICTION);
            setHeaderScroll(neoScrollValue);
            onPullChange(scrollConsumed, footerDimension);
        }
    }


    @Override
    public int getNestedScrollAxes() {
        return getNestedScrollingParentHelper().getNestedScrollAxes();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getNestedScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return getNestedScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getNestedScrollingChildHelper().startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        getNestedScrollingChildHelper().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getNestedScrollingChildHelper().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable @Size(value = 2) int[] offsetInWindow) {
        return getNestedScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable @Size(value = 2) int[] consumed, @Nullable @Size(value = 2) int[] offsetInWindow) {
        return getNestedScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getNestedScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getNestedScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    private NestedScrollingChildHelper getNestedScrollingChildHelper(){
        if(mNestedScrollingChildHelper == null) mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        return mNestedScrollingChildHelper;
    }

    private NestedScrollingParentHelper getNestedScrollingParentHelper(){
        if(mNestedScorllingParentHelper == null) mNestedScorllingParentHelper = new NestedScrollingParentHelper(this);
        return mNestedScorllingParentHelper;
    }

}
