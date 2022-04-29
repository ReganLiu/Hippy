package com.tencent.mtt.hippy.adapter.platform;

import com.tencent.mtt.hippy.common.HippyMap;

public interface HippyPlatformAdapter {

  boolean isTvPlatform();

  HippyMap getNativeMap();
}
