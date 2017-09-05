package ptra.hacc.cc.ptr.refreshview;

import android.view.View;

/**
 * Created by Hacceee on 2017/8/28.
 *
 */

public interface IRefreshBase {
    void onPull(float dx, float dy);
    void onRefreshing();
    View getRefreshingView();
}
