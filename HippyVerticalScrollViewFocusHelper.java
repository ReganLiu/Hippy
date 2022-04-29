package com.tencent.mtt.hippy.views.scroll;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.utils.StrictFocusFinder;
import java.util.ArrayList;

public class HippyVerticalScrollViewFocusHelper {

  private static final String TAG = "HippyVerticalScrollViewFocusHelper";
  private HippyVerticalScrollView mScrollView = null;
  private Rect mFocusRect = new Rect();
  private int mFocusPosition = 0;

  public HippyVerticalScrollViewFocusHelper(HippyVerticalScrollView scrollView) {
    this.mScrollView = scrollView;
  }

  public View focusSearch(View focused, int direction) {
    View nextFocus = StrictFocusFinder.getInstance().findNextFocus(mScrollView, focused, direction);
    if (nextFocus == null && mScrollView.getParent() != null) {
      return mScrollView.getParent().focusSearch(focused, direction);
    }
    return nextFocus;
  }

  public void handleRequestFocusFromTouch(MotionEvent event) {
    float posY = event.getY();
    ViewGroup liView = (ViewGroup) mScrollView.getChildAt(0);
    int count = liView.getChildCount();
    for (int i = 0; i < count; ++i) {
      View child = liView.getChildAt(i);

      if (isChildRequestOnTouch(posY, child)) {
        child.requestFocusFromTouch();
        break;
      }
    }
  }

  private boolean isChildRequestOnTouch(float posY, View child) {
    return (child.getTop() - mScrollView.getScrollY()) < posY && posY < (
      child.getTop() - mScrollView.getScrollY() + child
        .getHeight());
  }

  public int getFocusPosition() {
    return mFocusPosition;
  }

  public void setFocusPosition(int position) {
    mFocusPosition = position;
  }

  public void scrollToFocusChild(View child) {
    child.getDrawingRect(mFocusRect);
    mScrollView.offsetDescendantRectToMyCoords(child, mFocusRect);
    int scrollY = computeScrollDelta(mFocusRect);
    if (scrollY != 0) {
      mScrollView.smoothScrollBy(0, scrollY);
    }
  }

  public boolean requestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
    final int focusPosition;
    focusPosition = getFocusPosition();
    ViewGroup liView = (ViewGroup) mScrollView.getChildAt(0);
    View realFocuView = liView.getChildAt(focusPosition);

    boolean ret = (realFocuView != null && realFocuView
      .requestFocus(direction, previouslyFocusedRect));
    LogUtils.d("HippyVerticalScrollView", "requestFocusInDescendants  ret : " + ret);
    if (!ret) {
      if (Math.abs(focusPosition) <= Math.abs(focusPosition - liView.getChildCount())) {
        for (int i = 0; i < liView.getChildCount(); i++) {
          View v = liView.getChildAt(i);
          if (v.getVisibility() == View.VISIBLE && v.requestFocus(direction,
            previouslyFocusedRect)) {
            ret = true;
            break;
          }
        }
      } else {
        for (int i = liView.getChildCount() - 1; i >= 0; i--) {
          View v = liView.getChildAt(i);
          if (v.getVisibility() == View.VISIBLE && v.requestFocus(direction,
            previouslyFocusedRect)) {
            ret = true;
            break;
          }
        }
      }
    }
    return ret;
  }

  public boolean addFocusablesImp(ArrayList<View> views, int direction, int focusableMode) {
    if (mScrollView.hasFocus()
      || mScrollView.getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
      return false;
    }

    ViewGroup liView = (ViewGroup) mScrollView.getChildAt(0);
    ArrayList<View> childViews = new ArrayList<>();
    for (int i = 0; i < liView.getChildCount(); i++) {
      View child = liView.getChildAt(i);
      if (child.getVisibility() == View.VISIBLE) {
        child.addFocusables(childViews, direction, focusableMode);
      }
    }

    if (!childViews.isEmpty()) {
      views.add(mScrollView);
    }

    return true;
  }

  protected int computeScrollDelta(Rect rect) {
    if (mScrollView.getChildCount() == 0) {
      return 0;
    }
    int height = mScrollView.getHeight();
    int screenTop = mScrollView.getScrollY();
    int screenBottom = screenTop + height;
    int fadingEdge = mScrollView.getVerticalFadingEdgeLength();
    if (rect.top > 0) {
      screenTop += fadingEdge;
    }
    if (rect.bottom < mScrollView.getChildAt(0).getHeight()) {
      screenBottom -= fadingEdge;
    }

    int scrollYDelta = 0;
    LogUtils.i(TAG,
      "computeScrollDelta height=" + height + ",screenTop=" + screenTop + ",screenBottom="
        + screenBottom
        + ",fadingEdge=" + fadingEdge + ",rect=" + rect);
    if (rect.bottom > screenBottom && rect.top > screenTop) {
      if (rect.height() > height) {
        scrollYDelta += (rect.top - screenTop);
      } else {
        scrollYDelta += (rect.bottom - screenBottom) + height / 4;
      }

      int bottom = mScrollView.getChildAt(0).getBottom();
      int distanceToBottom = bottom - screenBottom;
      scrollYDelta = Math.min(scrollYDelta, distanceToBottom);
      LogUtils.i(TAG,
        "computeScrollDelta bottom=" + bottom + ",distanceToBottom=" + distanceToBottom
          + ",scrollYDelta="
          + scrollYDelta);

    } else if (rect.top < screenTop && rect.bottom < screenBottom) {
      if (rect.height() > height) {
        scrollYDelta -= (screenBottom - rect.bottom);
      } else {
        scrollYDelta -= (screenTop - rect.top + height / 4);
      }

      scrollYDelta = Math.max(scrollYDelta, -mScrollView.getScrollY());
      LogUtils.i(TAG, "computeScrollDelta getScrollY=" + mScrollView.getScrollY()
        + ",scrollYDelta=" + scrollYDelta);
    }
    return scrollYDelta;
  }
}
