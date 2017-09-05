package ptra.hacc.cc.pulltorefreshattacher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import ptra.hacc.cc.ptr.PullToRefreshRecyclerView;
import ptra.hacc.cc.ptr.refreshview.URecyclerView;

/**
 * Created by Hale Yang on 2017/9/5.
 * a demo for pull
 */

public class PulltoRefreshURecyclerView extends PullToRefreshRecyclerView {
    public PulltoRefreshURecyclerView(Context context) {
        super(context);
    }

    public PulltoRefreshURecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PulltoRefreshURecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PulltoRefreshURecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    public void setEmptyView(@Nullable View emptyView){
        URecyclerView r = (URecyclerView) getRefreshableView();
        r.setEmptyView(emptyView);
    }


    @NonNull
    @Override
    protected RecyclerView createRecyclerView(Context context) {
        return new URecyclerView(context);
    }
}
