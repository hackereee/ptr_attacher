package ptra.hacc.cc.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Hale Yang on 2017/8/30.
 * pull to refreshing for Recyclerview
 */

public class PullToRefreshRecyclerView extends PullToNestedRefreshBase<RecyclerView>{

    private static final int LAYOUT_LIST = 0x0;
    private static final int LAYOUT_GRID = 0x1;
    private static final int LAYOUT_STAGGERED_GRID = 0x2;

    private Orientation mOrientation = Orientation.VERTICAL;

    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }



    public void setLayoutManager(RecyclerView.LayoutManager layoutManager){
        getRefreshableView().setLayoutManager(layoutManager);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        getRefreshableView().setAdapter(adapter);
    }

    public void setNoMore(){
        setNoMore(true);
    }

    public void setNoMore(boolean noMore){
        setPullWay(noMore ? PullWay.WUP_PULL_ONLY : PullWay.WBOTH_PULL_ALLOW);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return mOrientation;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        boolean readyForPull = false;
        RecyclerView recyclerView = getRefreshableView();
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager lm = (LinearLayoutManager) layoutManager;
                int position = lm.findLastVisibleItemPosition();
                if (position == lm.getItemCount() - 1) {
                    View child = lm.findViewByPosition(position);
                    if (child != null && child.getBottom() <= recyclerView.getBottom()) {
                        readyForPull = true;
                    }
                }else if(position < 0){
                    readyForPull = true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager sm = (StaggeredGridLayoutManager) layoutManager;
                int[] positions = sm.findFirstCompletelyVisibleItemPositions(null);
                int spanCount = sm.getSpanCount();
                if(positions.length > 0 && positions[spanCount - 1] == sm.getItemCount() - 1){
                    for(int i = 0; i < spanCount; i++){
                        if(positions[i] < 0){
                            readyForPull = true;
                            break;
                        }
                        View child = sm.findViewByPosition(positions[i]);
                        if(child.getBottom() <= recyclerView.getBottom()){
                            readyForPull = true;
                            break;
                        }
                    }
                }
            }
        }
        return  readyForPull;
    }

    @Override
    protected boolean isReadyForPullStart() {
        boolean readyForPull = false;
        RecyclerView recyclerView = getRefreshableView();
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager != null) {
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager lm = (LinearLayoutManager) layoutManager;
                int position = lm.findFirstCompletelyVisibleItemPosition();
                if (position == 0) {
                    View child = lm.findViewByPosition(position);
                    if (child != null && child.getTop() == recyclerView.getTop()) {
                        readyForPull = true;
                    }
                }else if(position < 0){
                    readyForPull = true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager sm = (StaggeredGridLayoutManager) layoutManager;
                int[] positions = sm.findFirstCompletelyVisibleItemPositions(null);
                int spanCount = sm.getSpanCount();
                if(positions.length > 0 && positions[spanCount - 1] == spanCount - 1){
                    for(int i = 0; i < spanCount; i++){
                        if(positions[i] < 0){
                            readyForPull = true;
                            break;
                        }
                        View child = sm.findViewByPosition(positions[i]);
                        if(child.getTop() == recyclerView.getTop()){
                            readyForPull = true;
                            break;
                        }
                    }
                }
            }
        }
        return  readyForPull;
    }

    /**
     * find the layoutManager loaded
     * @param a - TypedArray of PullToRefresh Attributes
     */
    @Override
    protected void handleStyledAttributes(TypedArray a) {

    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefresh);
        int tag = a.getInt(R.styleable.PullToRefresh_ptrLayoutManager, LAYOUT_LIST);
        RecyclerView.LayoutManager layoutManager = createLayoutManagerForTag(context, a, tag);
        a.recycle();
        RecyclerView recyclerView = createRecyclerView(context);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }

    @NonNull
    protected RecyclerView.LayoutManager createLayoutManagerForTag(Context context, TypedArray a, int tag){
        RecyclerView.LayoutManager layoutManager;
        int spanCount = a.getInt(R.styleable.PullToRefresh_ptrSpanCount, 2);
        int layoutOritation =  a.getInt(R.styleable.PullToRefresh_ptrOrientation, LinearLayout.VERTICAL);
        mOrientation = layoutOritation == LinearLayout.VERTICAL ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        setOrientation(mOrientation == Orientation.VERTICAL ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        switch (tag){
            case LAYOUT_LIST:
                layoutManager = new LinearLayoutManager(context, layoutOritation, false);
                break;
            case LAYOUT_GRID:
                layoutManager = new GridLayoutManager(context, spanCount, layoutOritation, false);
                break;
            case LAYOUT_STAGGERED_GRID:
                layoutManager = new StaggeredGridLayoutManager(spanCount, layoutOritation);
                break;
            default:
                layoutManager = new LinearLayoutManager(context, layoutOritation, false);
                break;
        }
        return layoutManager;
    }

    @NonNull
    protected  RecyclerView createRecyclerView(Context context){
        return  new RecyclerView(context);
    }

}
