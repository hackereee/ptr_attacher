package ptra.hacc.cc.ptr;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Hale Yang on 2017/8/30.
 * This class allow the nested scroll, if the child view extend the nested scroll
 * then we must close the child nested scroll, and we will realize the function in this.
 */

public abstract class PullToNestedRefreshBase<E extends View> extends PullToRefreshBase<E> implements NestedScrollingChild {

    private NestedScrollingChildHelper mScrollingChildHelper;

    public PullToNestedRefreshBase(Context context) {
        super(context);
        init(context);
    }

    public PullToNestedRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToNestedRefreshBase(Context context, Mode mode) {
        super(context, mode);
        init(context);
    }

    public PullToNestedRefreshBase(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
        init(context);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return null;
    }


    private void init(Context context){
        setNestedScrollingEnabled(true);
    }


    /**
     * 设置是否可以进行嵌套滚动
     * @param enabled true or false
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }


    @Override
    public boolean isNestedScrollingEnabled(){
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getScrollingChildHelper().startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (mScrollingChildHelper == null) {
            mScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return mScrollingChildHelper;
    }

    /**
     * edit by Hale Yang
     * @param consumed save the consumed position 0 is x , 1 is y
     * @param offset save the offset
     * @return true or false
     */
    @Override
    protected boolean onExtractMoveEvent(float dx, float dy, int[] consumed, int[] offset,@EXTRA_STATE int extraState) {
        boolean canPull = true;
        int[] realOffset = new int[2];
        int unConsumedX = (int) dx, unConsumedY = (int) dy;
        if(extraState == EXTRA_START){
            int axes = getPullToRefreshScrollDirection() == Orientation.VERTICAL ? ViewCompat.SCROLL_AXIS_VERTICAL : ViewCompat.SCROLL_AXIS_HORIZONTAL;
            startNestedScroll(axes);
        }else if((getPullToRefreshScrollDirection() == Orientation.VERTICAL && dy < 0)
                || (getPullToRefreshScrollDirection() == Orientation.HORIZONTAL) && dx < 0){
            if(dispatchNestedPreScroll((int)dx, (int)dy, consumed, offset)){
                unConsumedX = (int) (dx - consumed[0]);
                unConsumedY = (int) (dy - consumed[1]);
                realOffset = offset;
                canPull = false;
            }
            if(dispatchNestedScroll(consumed[0], consumed[1], unConsumedX, unConsumedY, offset)){
                realOffset[0] += offset[0];
                realOffset[1] += offset[1];
                canPull = false;
            }
            offset[0] = realOffset[0];
            offset[1] = realOffset[1];
        }
        return offset[0] <= 0  && offset[1] <= 0;
    }
}
