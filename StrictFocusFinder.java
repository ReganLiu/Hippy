package com.tencent.mtt.hippy.utils;

import android.graphics.Rect;
import android.view.View;

/**
 * 使用严格模式搜索焦点
 * <p>
 * 1. 水平查找焦点时，要求竖直方向必须有重叠部分
 */
public class StrictFocusFinder extends FocusFinder {

    private static final ThreadLocal<FocusFinder> tlFocusFinder =
            new ThreadLocal<FocusFinder>() {
                @Override
                protected FocusFinder initialValue() {
                    return new StrictFocusFinder();
                }
            };

    /**
     * Get the focus finder for this thread.
     */
    public static FocusFinder getInstance() {
        return tlFocusFinder.get();
    }

    @Override
    boolean isBetterCandidate(float scale, int direction, Rect source, Rect rect1, Rect rect2) {
        if (isNonOverlapped(source, rect1, direction)) {
            return false;
        }
        return super.isBetterCandidate(scale, direction, source, rect1, rect2);
    }

    /**
     * 判断是否目标区域和src是否没有重叠区域
     */
    boolean isNonOverlapped(Rect srcRect, Rect destRect, int direction) {
        switch (direction) {
            case View.FOCUS_LEFT:
            case View.FOCUS_RIGHT:
                return srcRect.top > destRect.bottom || srcRect.bottom < destRect.top;
            case View.FOCUS_UP:
            case View.FOCUS_DOWN:
                return false;
            default:
                break;
        }
        throw new IllegalArgumentException("direction must be one of "
                + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    }
}
