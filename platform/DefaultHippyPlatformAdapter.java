package com.tencent.mtt.hippy.adapter.platform;

import com.tencent.mtt.hippy.common.HippyMap;

public class DefaultHippyPlatformAdapter implements HippyPlatformAdapter {

  @Override
  public boolean isTvPlatform() {
    return false;
  }

  @Override
  public HippyMap getNativeMap() {
    return null;
  }
}
