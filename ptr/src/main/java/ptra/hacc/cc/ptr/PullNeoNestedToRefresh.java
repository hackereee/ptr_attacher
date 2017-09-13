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

    public PullNeoNestedToRefresh(Context context) {
        super(context);
    }

    public PullNeoNestedToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullNeoNestedToRefresh(Context context, Mode mode) {
        super(context, mode);
    }

    public PullNeoNestedToRefresh(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return false;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return false;
    }

    protected abstract boolean isExtraReadyForPullStart();
    protected abstract boolean isExtraReadyForPullEnd();

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !isRefreshing();
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScorllingParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        stopNestedScroll();
        mNestedScorllingParentHelper.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int offset[] = new int[2];
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offset);
    }

    /**
     * 如果target是一个nested child ,那么它必须遵循{@link NestedScrollingChild}的处理逻辑，
     * 当调用此方法的时候，说明target想滚动parent所嵌套的那个view ,那么此时如果这个view是已经滚动过的，
     * 这个动作是需要回滚这个view,那么parent的处理此方法的时候将直接回滚而不等到调用了滚动方法后再去滚动
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        int offset[] = new int[2];
        dispatchNestedPreScroll(dx, dy, consumed, offset);
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

    @Override
    public int getNestedScrollAxes() {
        return mNestedScorllingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        super.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return super.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return super.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        super.stopNestedScroll();
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
