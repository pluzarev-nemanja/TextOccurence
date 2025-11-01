# Kotlin Concurrent Text Occurrence Finder 🔎

This project implements a high-performance, concurrent utility to search for a specific string across all files within a directory using **Kotlin Coroutines** and **Flow**.

## ✨ Features

* **Concurrent & Non-Blocking:** Leverages `Dispatchers.IO` and `Flow` to process multiple files simultaneously without blocking the main thread.
* **Reactive Stream:** Results are streamed as `Occurrence` objects are found, making it efficient for large directories.
* **Robust I/O:** Safely reads file contents using `bufferedReader().useLines`.
* **Unit Tested:** Includes JUnit 5 and `kotlinx-coroutines-test` for reliable verification.

---

## 💻 How to run test for it

```bash
./gradlew test
```
