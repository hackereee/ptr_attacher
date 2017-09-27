package ptra.hacc.cc.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ptra.hacc.cc.ptr.PullToRefershNestedScrollView;
import ptra.hacc.cc.ptr.PullToRefreshBase;

/**
 * -------------------------------
 * Created by Hale Yang on 2017/9/27.
 * -------------------------------
 */

public class NestedScrollViewFragment extends BaseFragment {

    private NestedScrollView mRefreshableView;
    private PullToRefershNestedScrollView ptr;
    private LinearLayout mContainer;
    ExecutorService mTestRequest = Executors.newCachedThreadPool();

    private MineHandler mHandler = new MineHandler(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scroll, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        ptr = (PullToRefershNestedScrollView) view.findViewById(R.id.ptr);
        ptr.setMode(PullToRefreshBase.Mode.BOTH);
        mRefreshableView = ptr.getRefreshableView();
        mContainer = new LinearLayout(getContext());
        mContainer.setOrientation(LinearLayout.VERTICAL);
        mRefreshableView.addView(mContainer, generateContainerLayoutParams());
        ptr.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<NestedScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {
                ptr.setNoMore(false);
                mTestRequest.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<TestEntity> entities = new ArrayList<>();
                        for (int i = 0; i <= 5; i++) {
                            TestEntity entity = new TestEntity();
                            entity.setContent("this is the " + i + " item content");
                            entities.add(entity);
                        }
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                        mHandler.obtainMessage(MineHandler.REFRESH_LIST, entities).sendToTarget();
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {
                mTestRequest.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<TestEntity> entities = new ArrayList<>();
                        for (int i = 0; i <= 5; i++) {
                            TestEntity entity = new TestEntity();
                            entity.setContent("this is the " + (mContainer.getChildCount() + i) + " item content");
                            entities.add(entity);
                        }
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                        mHandler.obtainMessage(MineHandler.ADDMORE_LIST, entities).sendToTarget();
                        if (mContainer.getChildCount() > 20) {
                            mHandler.sendEmptyMessage(MineHandler.NOMORE_DATA);
                        }
                    }
                });

            }
        });

    }

    private ViewGroup.LayoutParams generateContainerLayoutParams(){
        return new NestedScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Override
    void clearDatas() {
        mContainer.removeAllViews();
    }

    private static class MineHandler extends Handler {

        private static final int REFRESH_LIST = 1;
        private static final int ADDMORE_LIST = 2;
        private static final int NOMORE_DATA = 3;

        private WeakReference<NestedScrollViewFragment> a;

        private MineHandler(NestedScrollViewFragment fragment) {
            a = new WeakReference<>(fragment);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if (a.get() == null) return;
            switch (msg.what) {
                case REFRESH_LIST:
                    a.get().ptr.onRefreshComplete();
                    if (msg.obj != null)
                        a.get().refreshData((List<TestEntity>) msg.obj);
                    break;
                case ADDMORE_LIST:
                    a.get().ptr.onRefreshComplete();
                    if (msg.obj != null)
                        a.get().addMoreData((List<TestEntity>) msg.obj);
                    break;
                case NOMORE_DATA:
                    a.get().ptr.onRefreshComplete();
                    a.get().ptr.setNoMore(true);
                    break;
            }
        }
    }


    private void refreshData(@NonNull List<TestEntity> entities) {
        int abCount = entities.size() - mContainer.getChildCount();
        if (abCount < 0) {
            for (int i = mContainer.getChildCount() - 1; i >= entities.size(); i--) {
                mContainer.removeViewAt(i);
            }
        } else {
            for (int i = 0; i < abCount; i++) {
                mContainer.addView(createChildView());
            }
        }

        for (int i = 0; i < mContainer.getChildCount(); i++) {
            setChildViewData(mContainer.getChildAt(i), entities.get(i).getContent());
        }

    }

    private void addMoreData(@NonNull List<TestEntity> entities) {
        for (TestEntity entity : entities) {
            mContainer.addView(createChildView(entity.getContent()));
        }
    }


    private View setChildViewData(@NonNull View v, @NonNull CharSequence content) {
        TextView textView = (TextView) v.findViewById(R.id.text);
        textView.setText(content);
        return v;
    }

    private View createChildView(@NonNull CharSequence content) {
        View v = createChildView();
        setChildViewData(v, content);
        return v;
    }

    private View createChildView() {
        return getLayoutInflater(null).inflate(R.layout.view_mine_item, mContainer, false);
    }
}
