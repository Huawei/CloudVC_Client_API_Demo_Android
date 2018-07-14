package com.huawei.opensdk.commonservice.util;

/**
 * This class is about log tag.
 * 日志标签类
 */
public class LogTag
{

    String getExtraInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        int i = getStackTraceElementIndex();
        StackTraceElement ste = Thread.currentThread().getStackTrace()[i];
        stringBuilder.append(getClassName(ste.getClassName())).append(".");
        stringBuilder.append(ste.getMethodName()).append("(");
        stringBuilder.append(ste.getFileName()).append(":");
        stringBuilder.append(ste.getLineNumber()).append(")");
        return stringBuilder.toString();
    }

    private static String getClassName(String className) {
        int pos = className.lastIndexOf(46);
        if(pos == -1) {
            return className;
        } else {
            className = className.substring(pos + 1);
            pos = className.lastIndexOf(36);
            return pos == -1?className:className.substring(pos + 1);
        }
    }

    private int getStackTraceElementIndex() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        int i;
        for(i = 0; i < stack.length; ++i) {
            String cls = stack[i].getClassName();
            if(cls.equals(this.getClass().getName())) {
                i += 2;
                break;
            }
        }

        return i;
    }
}
