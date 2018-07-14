package com.huawei.opensdk.ec_sdk_demo.ui.base;

import android.app.Activity;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;

import java.util.Iterator;
import java.util.Stack;

public final class ActivityStack
{
    private static final ActivityStack INSTANCE = new ActivityStack();
    private final Stack<Activity> stack = new Stack<>();
    private static final int EMPTY_STACK = -1;

    public static ActivityStack getIns()
    {
        return INSTANCE;
    }

    public int getSize()
    {
        return stack.size();
    }

    public void push(Activity activity, boolean singleTask)
    {
        if (null == activity)
        {
            return;
        }
        // if singleTask mode and contain the activity,
        // remove all activities above the activity and the activity
        if (singleTask && contain(activity.getClass()))
        {
            popupAbove(activity.getClass());
            stack.pop().finish();
        }
        // then push the activity
        stack.push(activity);
    }

    public void push(Activity activity)
    {
        push(activity, false);
    }

    public void popup()
    {
        if (stack.isEmpty())
        {
            return;
        }

        Activity activity = stack.pop();
        if (null != activity)
        {
            LogUtil.d(UIConstants.DEMO_TAG, activity.getLocalClassName());
            activity.finish();
        }
    }

    /**
     * remove the activity from stack and do finish
     * @param activity activity removed and finished
     */
    public void popup(Activity activity)
    {
        if (null != activity)
        {
            LogUtil.d(UIConstants.DEMO_TAG, activity.getLocalClassName());
            stack.removeElement(activity);
            activity.finish();
        }
    }

    /**
     * remove the activity from stack and do finish
     * @param cls activity removed and finished
     */
    public void popup(Class<? extends Activity> cls)
    {
        if (null == cls)
        {
            return;
        }

        Activity activity;

        for (Iterator<Activity> it = stack.iterator(); it.hasNext(); )
        {
            activity = it.next();
            if (activity == null)
            {
                continue;
            }

            if (activity.getClass() == cls)
            {
                it.remove();
                activity.finish();
            }
        }
    }

    /**
     * remove only, not do finish
     * @param target activity removed
     */
    public void remove(Activity target)
    {
        if (null != target)
        {
            stack.removeElement(target);
        }
    }

    public void popupAbove(Activity activity)
    {
        int index = stack.indexOf(activity);
        int size = stack.size();
        if (index == -1)
        {
            return;
        }

        Activity temp;

        for (int i = size - 1; i > index; i--)
        {
            temp = stack.remove(i);
            temp.finish();
        }
    }

    public void popupAllExcept(Activity activity)
    {
        Activity target;
        int size = stack.size();
        LogUtil.d(UIConstants.DEMO_TAG, "size#" + size + ";" + activity.getLocalClassName());

        try
        {
            for (int i = 0; i < size; i++)
            {
                target = stack.pop();
                if (target == null)
                {
                    continue;
                }

                if (target != activity)
                {
                    target.finish();
                }
            }
        }
        catch (Exception e)
        {
            LogUtil.d(UIConstants.DEMO_TAG, e.getMessage());
        }

        if (null != activity)
        {
            stack.push(activity);
        }
    }

    /**
     * 通过类名找到Activity
     * @param cls 类名
     * @return 找到的Activity，为找到返回null
     */
    public Activity findActivity(Class<? extends Activity> cls)
    {

        for (Activity activity : stack)
        {
            if (null == activity)
            {
                continue;
            }

            if (activity.getClass() == cls)
            {
                return activity;
            }
        }

        return null;
    }

    public Activity getCurActivity()
    {
        if (stack.isEmpty())
        {
            return null;
        }

        Activity lastElement = stack.lastElement();
        if (null == lastElement)
        {
            popup();

            lastElement = getCurActivity();
        }

        return lastElement;
    }

    public Activity getActivity(int position)
    {
        if (!stack.isEmpty() && position < stack.size())
        {
            return stack.elementAt(position);
        }

        return null;
    }

    /**
     * 判断当前界面栈是否存在某个Activity的实例
     * @param cls
     * @return
     */
    public boolean contain(Class<? extends Activity> cls)
    {
        if (cls == null)
        {
            return false;
        }

        Activity activity;
        for (int i = 0; i < stack.size(); i++)
        {
            activity = getActivity(i);
            if (activity == null)
            {
                continue;
            }

            if (activity.getClass() == cls)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 方法名称：popupAbove
     * 作者：zhudongfang
     * 方法描述：
     * @param cls void
     * 备注：
     */
    public void popupAbove(Class<?> cls)
    {
        if (cls == null)
        {
            return;
        }

        Activity activity;
        int index = EMPTY_STACK;
        for (int i = 0; i < stack.size(); i++)
        {
            activity = getActivity(i);
            if (activity == null)
            {
                continue;
            }

            if (activity.getClass() == cls)
            {
                index = i;
                break;
            }
        }

        if (index == EMPTY_STACK)
        {
            return;
        }

        int stackSize = stack.size();
        for (int i = 0; i < stackSize - index - 1; i++)
        {
            stack.pop().finish();
        }
    }
}
