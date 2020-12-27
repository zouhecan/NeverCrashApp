package com.example.nevercrashapp;

import android.app.Activity;
import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Activity 管理栈
 */
public class ActivityStack {

    private final Object lock = new Object();
    private final Stack<Activity> stack = new Stack<>();
    private final static ActivityStack sInstance = new ActivityStack();
    public final ArrayList<Application.ActivityLifecycleCallbacks> mActivityLifecycleCallbacks = new ArrayList<>();

    private ActivityStack() {
    }

    /**
     * 单例
     */
    public static ActivityStack Instance() {
        return sInstance;
    }

    /**
     * 压入堆栈顶部
     *
     * @param activity
     */
    public <A extends Activity> void push(@NonNull A activity) {
        synchronized (lock) {
            stack.push(activity);
        }
    }

    /**
     * 获取当前Activity(最后一个入栈的)
     */
    public Activity curr() {
        synchronized (lock) {
            try {
                return stack.lastElement();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * 移除堆栈顶部的Activity
     * 出栈，即finish
     */
    public void pop() {
        synchronized (lock) {
            if (stack.empty()) {
                return;
            }

            pop(stack.peek());
        }
    }

    /**
     * 移除堆栈中指定位置的Activity
     *
     * @param index
     */
    public void pop(int index) {
        synchronized (lock) {
            if (index < 0) {
                return;
            }

            if (!stack.empty() && index < stack.size()) {
                pop(stack.get(index));
            }
        }
    }

    /**
     * 移除堆栈中的Activity，并将Activity Finish
     *
     * @param activity
     */
    public void pop(Activity activity) {
        synchronized (lock) {
            pop(activity, true);
        }
    }


    /**
     * 移除堆栈中的Activity
     *
     * @param activity
     * @param finish   Activity finish
     */
    public void pop(Activity activity, boolean finish) {
        synchronized (lock) {
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed() && finish) {
                activity.finish();
            }
            remove(activity);
        }
    }

    /**
     * 移除堆栈中的Activity
     *
     * @param list
     */
    public void popAll(List<Activity> list) {
        synchronized (lock) {
            if (null == list || list.isEmpty()) {
                return;
            }

            Stream.of(list).forEach(new Consumer<Activity>() {
                @Override
                public void accept(Activity activity) {
                    pop(activity, true);
                }
            });
        }
    }

    /**
     * 移除Activity至某一个Activity为止
     *
     * @param cls first-activity
     */
    public <A extends Activity> void popAllUntilTheOneClass(@NonNull Class<A> cls) {
        synchronized (lock) {
            if (!exist4Class(cls)) {
                return;
            }

            while (true) {
                Activity activity = curr();

                if (activity != null && activity.getClass().equals(cls)) {
                    break;
                }

                pop(activity);
            }
        }
    }

    /**
     * 移除Activity至某一个Activity为止
     *
     * @param activity activity
     */
    public <A extends Activity> void popAllUntilTheOne(A activity) {
        synchronized (lock) {
            if (!exist(activity)) {
                return;
            }

            while (true) {
                Activity a = curr();

                if (activity != null && a.equals(activity)) {
                    break;
                }

                pop(a);
            }
        }
    }

    /**
     * 移除除了某一个Activity之外的所有Activity
     *
     * @param a
     * @param <A>
     */
    public <A extends Activity> void popAllExcept(@NonNull final A a) {
        synchronized (lock) {
            popAll(Stream.of(stack).filter(new Predicate<Activity>() {
                @Override
                public boolean test(Activity value) {
                    return value == null || !value.equals(a);
                }
            }).toList());
        }
    }

    /**
     * 移除栈中的所有Activity
     */
    public void popAll() {
        synchronized (lock) {
            if (stack.isEmpty()) {
                return;
            }

            try {
                Iterator<Activity> it = stack.iterator();

                while (it.hasNext()) {
                    Activity activity = it.next();

                    if (activity != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (!activity.isFinishing() && !activity.isDestroyed()) {
                                activity.finish();
                            }
                        } else {
                            if (!activity.isFinishing()) {
                                activity.finish();
                            }
                        }
                    }

                    it.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //    /**
    //     * 结束指定Activity
    //     *
    //     * @param activity
    //     */
    //    private synchronized void finish(Activity activity) {
    //        if (stack == null) {
    //            return;
    //        }
    //
    //        pop(activity);
    //    }
    //
    //    /**
    //     * 结束当前Activity(最后一个入栈的)
    //     */
    //    private synchronized void finish() {
    //        finish(curr());
    //    }


    /**
     * 移除栈
     *
     * @param activity
     */
    public void remove(Activity activity) {
        synchronized (lock) {
            if (stack.empty() || !stack.contains(activity)) {
                return;
            }

            try {
                stack.remove(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回指定位置的Activity
     *
     * @param index
     * @return
     */
    public Activity get(int index) {
        synchronized (lock) {
            if (index < 0) {
                return null;
            }

            if (!stack.empty() && index < stack.size()) {
                return stack.get(index);
            }

            return null;
        }
    }


    /**
     * 返回Class对应的Activity的所有对象
     *
     * @param cls
     * @return
     */
    public List<Activity> list(@NonNull final Class<Activity> cls) {
        synchronized (lock) {
            return Stream.of(stack).filter(new Predicate<Activity>() {
                @Override
                public boolean test(Activity a) {
                    return a != null && a.getClass().equals(cls);
                }
            }).toList();
        }
    }

    /**
     * 返回前一个 Activity
     *
     * @return
     */
    public Activity pre() {
        synchronized (lock) {
            final int index = indexOf();
            return get(index - 1);
        }
    }

    /**
     * 当前Activity索引位置
     *
     * @return
     */
    public int indexOf() {
        synchronized (lock) {
            Activity activity = curr();
            if (activity == null) {
                return -1;
            }

            if (!stack.empty() && stack.contains(activity)) {
                return stack.indexOf(activity);
            }

            return -1;
        }
    }

    public <A extends Activity> int indexOf(@NonNull A activity) {
        synchronized (lock) {
            if (!stack.empty() && stack.contains(activity)) {
                return stack.indexOf(activity);
            }

            return -1;
        }
    }

    /**
     * 获取所有的Activity
     *
     * @return
     */
    public List<Activity> all() {
        synchronized (lock) {
            return Stream.of(stack).filter(new Predicate<Activity>() {
                @Override
                public boolean test(Activity value) {
                    return value != null;
                }
            }).toList();
        }
    }

    /**
     * 栈中是否存在Class对应的对象
     *
     * @param cls
     * @return
     */
    public <A extends Activity> boolean exist4Class(final @NonNull Class<A> cls) {
        synchronized (lock) {
            return Stream.of(stack).filter(new Predicate<Activity>() {
                @Override
                public boolean test(Activity a) {
                    return a != null && a.getClass().equals(cls);
                }
            }).count() > 0;
        }
    }

    /**
     * 栈中是否存在Class对应的对象
     *
     * @return
     */
    public <A extends Activity> boolean exist(final A activity) {
        synchronized (lock) {
            if (activity == null) {
                return false;
            }

            return Stream.of(stack).filter(new Predicate<Activity>() {
                @Override
                public boolean test(Activity a) {
                    return a != null && a.equals(activity);
                }
            }).count() > 0;
        }
    }

}
