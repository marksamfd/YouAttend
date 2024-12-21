package com.anonymous.attendance2.onnexfacenetframeprocessor

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.mrousavy.camera.frameprocessors.FrameProcessorPluginRegistry

class OnnexFaceNetFrameProcessorPluginPackage : ReactPackage {
  companion object {
    init {
      FrameProcessorPluginRegistry.addFrameProcessorPlugin("recognizeFace") { proxy, options ->

        OnnexFaceNetFrameProcessorPlugin(proxy, options)
      }
    }
  }

  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return emptyList()
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return emptyList()
  }
}