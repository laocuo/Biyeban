package com.laocuo.biyeban.main;

import dagger.Component;

/**
 * Created by hoperun on 6/5/17.
 */
@Component(modules = {MainModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
}
