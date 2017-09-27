package ptra.hacc.cc.ptr;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * -------------------------------
 * Created by Hale Yang on 2017/9/27.
 * -------------------------------
 */

public class PullToRefershNestedScrollView extends PullNeoNestedToRefresh<NestedScrollView> {

    private boolean mNoMore = false;
    private CharSequence mPullLabel, mNoMoreLabel;
    public PullToRefershNestedScrollView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefershNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getChildCount() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return  mRefreshableView.getChildCount() == 0;
    }

    public PullToRefershNestedScrollView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
        init(context);
    }

    private void init(Context context){
        this.mPullLabel = getFooterLayout().getPullLabel();
        this.mNoMoreLabel = getResources().getString(R.string.no_mor_data);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected NestedScrollView createRefreshableView(Context context, AttributeSet attrs) {
        return new NestedScrollView(context);
    }

    @Override
    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
        mNoMore = savedInstanceState.getBoolean("noMore");
        mNoMoreLabel = savedInstanceState.getCharSequence("noMoreLabel");
        mPullLabel = savedInstanceState.getCharSequence("pullLabel");
    }

    @Override
    protected void onPtrSaveInstanceState(Bundle saveState) {
        saveState.putBoolean("noMore", mNoMore);
        saveState.putCharSequence("noMoreLabel", mNoMoreLabel);
        saveState.putCharSequence("pullLabel", mPullLabel);
    }

    public void setNoMore(){
        setNoMore(true);
    }

    public void setNoMore(boolean noMore){
        this.mNoMore = noMore;
        if(noMore){
            mPullLabel = getFooterLayout().getPullLabel();
            getFooterLayout().setPullLabel(mNoMoreLabel);
        }else{
            getFooterLayout().setPullLabel(mPullLabel);
        }
        setPullWay(noMore ? PullWay.WUP_PULL_ONLY : PullWay.WBOTH_PULL_ALLOW);
    }

}
