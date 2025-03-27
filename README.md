# YouAttend 👋

An Expo-based attendance tracking application leveraging face recognition.

## Description

This project is an Expo application designed to streamline attendance tracking using face recognition technology. It utilizes SQLite with the VectorDb plugin for efficient data storage and processing.

## Technologies Used

*   **Expo:**  A framework for building universal native apps for Android, iOS, and web.
*   **React Native:** A JavaScript framework for writing real, natively rendering mobile applications.
*   **SQLite:** A self-contained, high-reliability, embedded, full-featured, public-domain, SQL database engine.
*   **VectorDb Plugin:** vector db to store face embeddings.
*   **Face Net:**  Currently exploring optimal solutions.

## Installation

1.  Install dependencies:

    ```bash
    npm install
    ```

2.  Connect your phone and start the app:

    ```bash
    npm run start
    ```

## Face Recognition Implementation Trials

The development process involved experimenting with various face recognition libraries and approaches:

*   **[Faceapi](https://github.com/justadudewhohacks/face-api.js) by justadudewhohacks:**  Initial attempt.  Abandoned due to incompatibility with current React Native and Expo versions (outdated TensorFlow version).
*   **Tensorflow's [`tflite`](https://www.npmjs.com/package/@tensorflow/tfjs-react-native):** Attempted to adapt this library to a face recognition implementation, but the approach was unsuccessful.
*   **[VisionCamera ](https://github.com/mrousavy/react-native-vision-camera) by mrousavy:** Considered as a potential solution.
*   **[`react-native-fast-tflite`](https://github.com/mrousavy/react-native-fast-tflite) by mrousavy:** Faced errors related to React Native updates and the New Architecture.
*   **Kotlin FrameProcessor plugin using [Onnx](https://onnx.ai/):**  Functioned, but produced inaccurate results due to half-precision processing.
*   **[react-native-fast-tflite#112](https://github.com/mrousavy/react-native-fast-tflite):**  Currently using a specific pull request from `react-native-fast-tflite` to leverage the New Architecture.  `"react-native-fast-tflite": "github:mrousavy/react-native-fast-tflite#pull/112/head"`
