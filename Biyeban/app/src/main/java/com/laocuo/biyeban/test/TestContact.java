package com.laocuo.biyeban.test;

/**
 * Created by hoperun on 5/26/17.
 */

public interface TestContact {
    public interface View {
        void updateUi(String result);
    }
    public interface Presentr {
        void loadData();
    }
}
