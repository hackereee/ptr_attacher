package ptra.hacc.cc.demo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ptra.hacc.cc.ptr.PullToRefreshBase;

/**
 * -------------------------------
 * Created by Hale Yang on 2017/9/27.
 * -------------------------------
 */

public class RecyclerViewFragment extends BaseFragment {

    ExecutorService mTestRequest = Executors.newCachedThreadPool();
    PulltoRefreshURecyclerView ptr;
    MineAdapter mAdapter;

    private boolean mFirst = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view){
        ptr = (PulltoRefreshURecyclerView) view.findViewById(R.id.ptr);
        ptr.setMode(PullToRefreshBase.Mode.BOTH);
        ptr.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                ptr.setNoMore(false);
                mTestRequest.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<TestEntity> entities = new ArrayList<TestEntity>();
                        for(int i = 0; i <= 5; i++){
                            TestEntity entity = new TestEntity();
                            entity.setContent("this is the " + i + " item content");
                            entities.add(entity);
                        }
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                        mHandler.obtainMessage(MineHanlder.REFRESH_LIST, entities).sendToTarget();
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                mTestRequest.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<TestEntity> entities = new ArrayList<>();
                        for(int i = 0; i <= 5; i++){
                            TestEntity entity = new TestEntity();
                            entity.setContent("this is the " + (mAdapter.getItemCount() + i) + " item content");
                            entities.add(entity);
                        }
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                        mHandler.obtainMessage(MineHanlder.ADDMORE_LIST, entities).sendToTarget();
                        if(mAdapter.getItemCount() > 20){
                            mHandler.sendEmptyMessage(MineHanlder.NOMORE_DATA);
                        }
                    }
                });

            }
        });
        mAdapter = new MineAdapter();
        ptr.setAdapter(mAdapter);
        ptr.setEmptyView(getLayoutInflater(null).inflate(R.layout.view_main_empty, (ViewGroup) view, false));
        ptr.setRefreshing();
    }

    @Override
    void clearDatas() {
        mAdapter.clear();
    }


    private class MineAdapter extends RecyclerView.Adapter<MineViewHolder>{
        private List<TestEntity> entities = new ArrayList<>();
        private MineAdapter(){
        }

        private void refresh(@NonNull List<TestEntity> entities){
            this.entities.clear();
            this.entities.addAll(entities);
            notifyDataSetChanged();
        }

        private void clear(){
            if(!entities.isEmpty()) {
                this.entities.clear();
                notifyDataSetChanged();
            }
        }

        private void addMore(@NonNull List<TestEntity> entities){
            notifyItemRangeInserted(this.entities.size(), entities.size());
            this.entities.addAll(entities);
        }

        @Override
        public MineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater(null).inflate(R.layout.view_mine_item, ptr.getRefreshableView(), false);
            return new MineViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MineViewHolder holder, int position) {
            holder.itemText.setText(entities.get(position).getContent());
        }

        @Override
        public int getItemCount() {
            return entities.size();
        }
    }

    class MineViewHolder extends RecyclerView.ViewHolder{
        TextView itemText;
        private MineViewHolder(View itemView) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(ptra.hacc.cc.ptr.R.id.text);
        }
    }

    private MineHanlder mHandler = new MineHanlder(this);

    private static class MineHanlder extends Handler {
        private static final int REFRESH_LIST = 1;
        private static final int ADDMORE_LIST = 2;
        private static final int NOMORE_DATA = 3;
        private WeakReference<RecyclerViewFragment> a;
        private MineHanlder(RecyclerViewFragment fragment){
            this.a  = new WeakReference<>(fragment);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if(a.get() == null) return;
            switch (msg.what){
                case REFRESH_LIST:
                    a.get().mAdapter.refresh((List<TestEntity>) msg.obj);
                    a.get().ptr.onRefreshComplete();
                    break;
                case ADDMORE_LIST:
                    a.get().mAdapter.addMore((List<TestEntity>) msg.obj);
                    a.get().ptr.onRefreshComplete();
                    break;
                case NOMORE_DATA:
                    a.get().ptr.setNoMore();
                    break;
            }
        }

    }
}
