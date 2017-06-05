package com.laocuo.biyeban.main.freshnews;

import dagger.Component;

/**
 * Created by hoperun on 6/5/17.
 */

@Component(modules = FreshNewsModule.class)
public interface FreshNewsComponent {
    void inject(FreshNewsFragment fragment);
}
