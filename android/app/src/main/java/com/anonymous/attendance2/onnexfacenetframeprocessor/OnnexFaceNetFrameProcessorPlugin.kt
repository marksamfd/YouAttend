package com.anonymous.attendance2.onnexfacenetframeprocessor

import ai.onnxruntime.*
import ai.onnxruntime.OrtSession.RunOptions
import ai.onnxruntime.extensions.OrtxPackage
import ai.onnxruntime.providers.NNAPIFlags
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.media.Image
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.mrousavy.camera.frameprocessors.Frame
import com.mrousavy.camera.frameprocessors.FrameProcessorPlugin
import com.mrousavy.camera.frameprocessors.VisionCameraProxy
import org.json.JSONArray

import java.nio.*
import java.security.AccessController.getContext
import java.util.Collections
import java.util.EnumSet


class OnnexFaceNetFrameProcessorPlugin(proxy: VisionCameraProxy, options: Map<String, Any>?): FrameProcessorPlugin() {

  private var ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
  private var p = proxy
  private lateinit var ortSession: OrtSession
  private var TAG = "FrameProcessor"

  private fun readModel(): ByteArray {
    val modelID = com.anonymous.attendance2.R.raw.model
    return p.context.applicationContext.resources.openRawResource(modelID).readBytes()
  }
  init{
    val sessionOptions: OrtSession.SessionOptions = OrtSession.SessionOptions()
    sessionOptions.registerCustomOpLibrary(OrtxPackage.getLibraryPath())
    sessionOptions.addNnapi(EnumSet.of(NNAPIFlags.CPU_DISABLED))
    ortSession = ortEnv.createSession(readModel(), sessionOptions)
    Log.i(TAG, "Model Loaded Sucessfully" )
  }
  private fun imageToBitmap(image: Image): Bitmap? {
    // Ensure the image format is correct
    if (image.format != ImageFormat.JPEG && image.format != ImageFormat.YUV_420_888) {
      throw IllegalArgumentException("Unsupported image format: ${image.format}")
    }

    return when (image.format) {
      ImageFormat.JPEG -> {
        // Directly convert JPEG to Bitmap
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
      }

      ImageFormat.YUV_420_888 -> {
        // Convert YUV to Bitmap (slightly more complex)
        yuvToBitmap(image)
      }

      else -> null
    }
  }

  private fun yuvToBitmap(image: Image): Bitmap? {
    val yBuffer = image.planes[0].buffer // Y
    val uBuffer = image.planes[1].buffer // U
    val vBuffer = image.planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    // Create a byte array to hold the YUV data
    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copy Y, U, and V planes into the byte array
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    // Convert YUV to Bitmap using RenderScript (or an external library)
    return nv21ToBitmap(nv21, image.width, image.height)
  }

  private fun nv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap? {
    val yuvImage = android.graphics.YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
  }

  private fun bitmapToDoubleBufferRGB565(bitmap: Bitmap): DoubleBuffer {
    val width = bitmap.width
    val height = bitmap.height

    // Create an array to hold the pixel data
    val intArray = IntArray(width * height)
    bitmap.getPixels(intArray, 0, width, 0, 0, width, height)

    // Create a double array to hold normalized pixel data
    val doubleArray = DoubleArray(width * height * 3) // Assuming RGB format
    var index = 0

    for (pixel in intArray) {
      // Extract RGB values from the 16-bit RGB_565 pixel
      val r = ((pixel shr 11) and 0x1F) / 31.0   // 5 bits for red
      val g = ((pixel shr 5) and 0x3F) / 63.0    // 6 bits for green
      val b = (pixel and 0x1F) / 31.0            // 5 bits for blue

      // Store normalized values
      doubleArray[index++] = r
      doubleArray[index++] = g
      doubleArray[index++] = b
    }

    // Convert the double array into a DoubleBuffer
    val doubleBuffer = ByteBuffer.allocateDirect(doubleArray.size * 8) // 8 bytes per double
      .order(ByteOrder.nativeOrder())
      .asDoubleBuffer()
    doubleBuffer.put(doubleArray)
    doubleBuffer.position(0) // Reset the buffer position
    return doubleBuffer
  }

  private fun bitmapToFloatBufferRGB565(bitmap: Bitmap): FloatBuffer {
    val width = bitmap.width
    val height = bitmap.height

    // Create an array to hold the pixel data
    val intArray = IntArray(width * height)
    bitmap.getPixels(intArray, 0, width, 0, 0, width, height)

    // Create a float array to hold normalized pixel data
    val floatArray = FloatArray(width * height * 3) // Assuming RGB format
    var index = 0

    for (pixel in intArray) {
      // Extract RGB values from the 16-bit RGB_565 pixel
      val r = ((pixel shr 11) and 0x1F) / 31.0f // 5 bits for red
      val g = ((pixel shr 5) and 0x3F) / 63.0f  // 6 bits for green
      val b = (pixel and 0x1F) / 31.0f          // 5 bits for blue

      // Store normalized values
      floatArray[index++] = r
      floatArray[index++] = g
      floatArray[index++] = b
    }

    // Convert the float array into a FloatBuffer
    val floatBuffer = ByteBuffer.allocateDirect(floatArray.size * 4) // 4 bytes per float
      .order(ByteOrder.nativeOrder())
      .asFloatBuffer()
    floatBuffer.put(floatArray)
    floatBuffer.position(0) // Reset the buffer position
    return floatBuffer
  }

  override fun callback(frame: Frame, arguments: Map<String, Any>?): Any? {
    // code goes here

    if (arguments != null) {
      Log.i(TAG, arguments.values.toString() + " " + arguments.keys.toString() )
    }

    val bitmap = imageToBitmap(frame.image)
    val mutableBitmap = bitmap!!.copy(Bitmap.Config.RGB_565, true)
    val croppedBitmap = Bitmap.createBitmap(mutableBitmap,(arguments?.get("x")as Double).toInt() , (arguments?.get("y")as Double).toInt(), (arguments?.get("width")as Double).toInt(),(arguments.get("height") as Double).toInt())
    val resizedBitmap = Bitmap.createScaledBitmap(
      croppedBitmap , 160, 160, false
    )

    val bitmapdata = bitmapToFloatBufferRGB565(resizedBitmap)

    Log.i(TAG, "callback: BitmapData Created")
    val inputTensor = OnnxTensor.createTensor(
      ortEnv,
      bitmapdata,
      longArrayOf(1,resizedBitmap.height.toLong(),resizedBitmap.width.toLong(),3)
    )
    Log.i(TAG, "callback: Tensor Created")
    var runOpts = RunOptions()
    runOpts.logVerbosityLevel = 5
    runOpts.logLevel = OrtLoggingLevel.ORT_LOGGING_LEVEL_VERBOSE
    runOpts.runTag = TAG
    inputTensor.use {
      Log.i(TAG, "callback: model will call  ")
      // Step 3: call ort inferenceSession run
      var output = ortSession.run(Collections.singletonMap("input_2", inputTensor),runOpts)
      Log.i(TAG, "callback: model called ")
      output.use {
        val outputName = ortSession.outputNames.iterator().next()
        val rawOutput = (output[outputName].get() as OnnxTensor).floatBuffer

        Log.i(TAG, "output:${rawOutput[0]} ${rawOutput[1]} ${rawOutput[2]} ${rawOutput[3]} ${rawOutput[4]} ")
        return arrayListOf(rawOutput)
      }
    }
    // Step 3: call ort inferenceSession run


  }
}