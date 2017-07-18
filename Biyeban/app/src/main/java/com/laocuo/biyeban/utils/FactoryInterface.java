/*
* Copyright (C) 2013-2016 laocuo@163.com .
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.laocuo.biyeban.utils;

public class FactoryInterface {
//    private void loadData() {
//        //do something
//        Observable.from(new Integer[]{1, 2, 3, 4})
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        mView.showLoading();
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .compose(mTransformer)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onCompleted() {
//                        mView.hideLoading();
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        L.d(s+"\n");
//                    }
//                });
//    }
//
//    private Observable.Transformer<Integer, String> mTransformer =
//            new Observable.Transformer<Integer, String>() {
//                @Override
//                public Observable<String> call(Observable<Integer> observable) {
//                    return observable
//                            .map(new Func1<Integer, String>() {
//                                @Override
//                                public String call(Integer integer) {
//                                    return integer.toString();
//                                }
//                            });
//                }
//            };
}
