package com.jeahwan.keyboard;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Rxjava封装工具类
 * Created by Makise on 2016/8/3.
 */
public class RxTask {

    /**
     * 在ui线程中工作
     *
     * @param uiTask
     */
    public static <T> void doInUIThread(UITask<T> uiTask) {
        doInUIThreadDelay(uiTask, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时在主线程中执行任务
     *
     * @param uiTask
     * @param time
     * @param timeUnit
     * @param <T>
     */
    public static <T> void doInUIThreadDelay(UITask<T> uiTask, long time, TimeUnit timeUnit) {
        Observable.just(uiTask)
                .delay(time, timeUnit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UITask<T>>() {
                    @Override
                    public void call(UITask<T> uitask) {
                        uitask.doInUIThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("RxTask",throwable.toString());
                    }
                });
    }

    /**
     * 在IO线程中执行任务
     *
     * @param <T>
     */
    public static <T> void doInIOThread(IOTask<T> ioTask) {
        doInIOThreadDelay(ioTask, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时在IO线程中执行任务
     *
     * @param <T>
     */
    public static <T> void doInIOThreadDelay(IOTask<T> ioTask, long time, TimeUnit timeUnit) {
        Observable.just(ioTask)
                .delay(time, timeUnit)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<IOTask<T>>() {
                    @Override
                    public void call(IOTask<T> ioTask) {
                        ioTask.doInIOThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("RxTask",throwable.toString());
                    }
                });
    }

    /**
     * 执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     *
     * @param t
     * @param <T>
     */
    public static <T> void executeRxTask(CommonTask<T> t) {
        executeRxTaskDelay(t, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     *
     * @param t
     * @param <T>
     */
    public static <T> void executeRxTaskDelay(CommonTask<T> t, long time, TimeUnit timeUnit) {
        MyOnSubscribe<CommonTask<T>> onsubscribe = new MyOnSubscribe<CommonTask<T>>(t) {
            @Override
            public void call(Subscriber<? super CommonTask<T>> subscriber) {
                getT().doInIOThread();
                subscriber.onNext(getT());
                subscriber.onCompleted();
            }
        };
        Observable.create(onsubscribe)
                .delay(time, timeUnit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonTask<T>>() {
                    @Override
                    public void call(CommonTask<T> t) {
                        t.doInUIThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("RxTask",throwable.toString());
                    }
                });
    }

    /**
     * 在IO线程中执行的任务
     * Created by Makise on 2016/8/3.
     */
    public static abstract class IOTask<T> {
        private T t;

        public IOTask(T t) {
            setT(t);
        }

        public IOTask() {
        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }

        public abstract void doInIOThread();
    }

    /**
     * 在UI线程中执行的任务
     * Created by Makise on 2016/8/3.
     */
    public static abstract class UITask<T> {
        private T t;

        public UITask(T t) {
            setT(t);
        }

        public UITask() {

        }

        public abstract void doInUIThread();

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }

    /**
     * 通用的Rx执行任务
     * Created by Makise on 2016/8/3.
     */
    public static abstract class CommonTask<T> {
        private T t;

        public CommonTask(T t) {
            setT(t);
        }

        public CommonTask() {

        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }

        public abstract void doInIOThread();

        public abstract void doInUIThread();

    }

    /**
     * 自定义OnSubscribe
     * Created by Makise on 2016/8/3.
     */
    public static abstract class MyOnSubscribe<T> implements Observable.OnSubscribe<T> {
        private T t;

        public MyOnSubscribe(T t) {
            setT(t);
        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }
}