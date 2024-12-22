# Cam Attendance App 👋

This is an [Expo](https://expo.dev) project created with [`create-expo-app`](https://www.npmjs.com/package/create-expo-app).

`"react-native-fast-tflite": "github:mrousavy/react-native-fast-tflite#pull/112/head"` for using the new architecture

## Get started

1. Install dependencies

   ```bash
   npm install
   ```

2. Connect your phone and Start the app

   ```bash
    npm run start
   ```

## Trials

### 1. [Faceapi](https://github.com/justadudewhohacks/face-api.js) by justadudewhohacks

I created a side project similar to this and I used this library in a side project and i tried to adapt it to the app but the library was using an older version or Tensorflow and it was not compitable with React Native and Expo

### 2. [tflite](https://www.npmjs.com/package/@tensorflow/tfjs-react-native) by Tensorflow

i tried to adapt this library to faceapi but it was a fail

### 3. [VisionCamera ](https://github.com/mrousavy/react-native-vision-camera) by mrousavy

#### 1. [react-native-fast-tflite](https://github.com/mrousavy/react-native-fast-tflite) by mrousavy

i tried to use this library but due to updates in react native and new architecture it was giving errors

#### 2. Kotlin FrameProcessor plugin using [Onnx](https://onnx.ai/)

it was working but it was working with half percision so the results was not accurate

#### 3. [react-native-fast-tflite#112](https://github.com/mrousavy/react-native-fast-tflite)

`"react-native-fast-tflite": "github:mrousavy/react-native-fast-tflite#pull/112/head"` for using the new architecture

The app is using sqlited with VectorDb plugin
