MyMediaApp
MyMediaApp is a simple Android application that allows users to select an image or video from their device, checks its file size, and determines if it's within predefined limits for images (5 MB) and videos (20 MB).

✨ Features
Media Selection: Easily pick an image or video from your device's storage.
Media Type Detection: Automatically identifies whether the selected file is an image or a video.
File Size Validation: Compares the selected file's size against set maximum limits.
Status Display: Provides clear feedback on whether the file is "Accepted ✅" or "Too Large ❌".
User-Friendly Interface: A straightforward UI with a single button for media selection and a text view for displaying file information.

🛠️ Build Requirements
* **Minimum SDK:** API 21 (Android 5.0 Lollipop)
* **Target SDK:** API 33+
* **Java Version:** Java 8 or higher
* **Development Environment:** Android Studio (recommended)

📁 Project Structure
The project follows a standard Android application structure:
MyMediaApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/mymediaapp/MainActivity.java  // Main application logic
│   │   │   ├── res/layout/activity_main.xml                   // User interface layout
│   │   │   ├── AndroidManifest.xml                            // Application manifest

🚀 Installation & Setup
* **Clone the Repository (if applicable):**
git clone [https://github.com/golu19102003/My-Media-App]
cd MyMediaApp

* **Open in Android Studio:**
Launch Android Studio.
Select Open an existing Android Studio project and navigate to the MyMediaApp directory.

* **Sync Project with Gradle Files:**
Android Studio will automatically try to sync the project. If not, click File > Sync Project with Gradle Files.

* **Build and Run:**
Connect an Android device or start an emulator.
Click the Run button (green play icon) in Android Studio to deploy the app.

💡 Usage
Upon launching the app, you will see a button labeled "Select Image or Video".
Tap this button to open your device's file picker.
Choose an image or video file from your gallery or file manager.
The app will then display the file type, its size, and whether it's Accepted or Too Large based on the following limits:
Max Image Size: 5 MB
Max Video Size: 20 MB
📝 Key Implementation Details

* **MainActivity.java**
This file contains the core logic for handling media selection, file size retrieval, and UI updates.
selectFile(): Initiates an ACTION_GET_CONTENT intent to allow the user to pick media files (image/* or video/*).
onActivityResult(): Handles the result from the file picker, retrieving the Uri of the selected file.
handleFile(Uri uri): This crucial method determines the MIME type of the file, gets its size, formats the size into a human-readable string, and performs the size validation.
getFileSize(Uri uri): Queries the content resolver to get the exact size of the selected file.
readableFileSize(long size): Converts file size in bytes to a more readable format (e.g., KB, MB, GB).

* package com.example.mymediaapp;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap; // Although included in original code, not explicitly used for type detection.
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat; // Used for formatting file sizes to be human-readable.

/**
 * MainActivity is the primary entry point for the MyMediaApp application.
 * It handles user interaction for selecting media files (images or videos),
 * determines their type and size, and displays a status based on predefined
 * size limits.
 */
public class MainActivity extends Activity {

    // Request code used to identify the result of the media picker intent.
    private static final int PICK_MEDIA = 1;

    // TextView to display information about the selected file.
    private TextView fileInfoText;

    /**
     * Called when the activity is first created. This is where you should do
     * all of your normal static set up: create views, bind data to lists, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the user interface layout for this Activity.
        // The layout file is defined in res/layout/activity_main.xml.
        setContentView(R.layout.activity_main);

        // Get references to the UI elements defined in activity_main.xml.
        Button selectFileButton = findViewById(R.id.selectFileButton);
        fileInfoText = findViewById(R.id.fileInfoText);

        // Set an OnClickListener for the selectFileButton.
        // When the button is clicked, the selectFile() method will be called.
        selectFileButton.setOnClickListener(v -> selectFile());
    }

    /**
     * Initiates the process of selecting an image or video file from the device's storage.
     * It launches an intent that allows the user to pick content of any type (* /*),
     * but specifically suggests image and video MIME types.
     */
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow selection of any file type initially.
        // Specify preferred MIME types to filter for images and videos.
        // This hint is used by the file picker to show relevant files.
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        // Start the activity, allowing the user to choose an app to complete the action.
        startActivityForResult(Intent.createChooser(intent, "Select Image or Video"), PICK_MEDIA);
    }

    /**
     * Called when an activity launched with startActivityForResult() finishes.
     * This method processes the result from the media picker.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     * allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (e.g., the Uri of the selected media).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the result is for our PICK_MEDIA request, if it was successful,
        // and if data (the selected file's URI) is available.
        if (requestCode == PICK_MEDIA && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData(); // Get the URI of the selected file.
            handleFile(uri); // Process the selected file.
        }
    }

    /**
     * Handles the selected file by determining its type, size, and
     * validating it against predefined size limits. Updates the TextView
     * with the file information and status.
     *
     * @param uri The Uri of the selected media file.
     */
    private void handleFile(Uri uri) {
        // Get the MIME type of the content at the given URI.
        // This is crucial for distinguishing between images and videos.
        String mimeType = getContentResolver().getType(uri);
        // Get the actual file size in bytes.
        long fileSize = getFileSize(uri);
        // Convert the file size into a human-readable format (e.g., "5 MB").
        String readableSize = readableFileSize(fileSize);

        String fileType;
        // Determine if the file is an image, video, or unknown based on its MIME type.
        if (mimeType != null && mimeType.startsWith("image")) {
            fileType = "Image";
        } else if (mimeType != null && mimeType.startsWith("video")) {
            fileType = "Video";
        } else {
            fileType = "Unknown"; // Fallback for types not explicitly handled.
        }

        // Construct the initial string to display in the TextView.
        String result = fileType + " selected\nSize: " + readableSize;

        // Define the maximum allowed sizes for images and videos in bytes.
        // 1 MB = 1024 * 1024 bytes
        long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
        long MAX_VIDEO_SIZE_BYTES = 20 * 1024 * 1024; // 20 MB

        // Check if the file is accepted based on its type and size.
        if ((fileType.equals("Image") && fileSize <= MAX_IMAGE_SIZE_BYTES) ||
            (fileType.equals("Video") && fileSize <= MAX_VIDEO_SIZE_BYTES)) {
            result += "\nStatus: Accepted ✅";
        } else {
            result += "\nStatus: Too Large ❌";
            // Display a short-lived message to the user if the file is too large.
            Toast.makeText(this, "File too large!", Toast.LENGTH_SHORT).show();
        }

        // Update the TextView on the UI with the final result string.
        fileInfoText.setText(result);
    }

    /**
     * Retrieves the size of the file associated with the given Uri.
     * It queries the ContentResolver for the file's metadata, specifically the _SIZE column.
     *
     * @param uri The Uri of the file for which to get the size.
     * @return The size of the file in bytes, or 0 if the size cannot be determined.
     */
    private long getFileSize(Uri uri) {
        Cursor cursor = null;
        long size = 0;
        try {
            // Query the content resolver for the file's metadata.
            // We're interested in the OpenableColumns.SIZE column, which contains the file size.
            cursor = getContentResolver().query(uri, null, null, null, null);
            // Move the cursor to the first result (there should only be one for a single file URI).
            if (cursor != null && cursor.moveToFirst()) {
                // Get the index of the SIZE column.
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // Ensure the column exists before trying to read from it.
                if (sizeIndex != -1) {
                    size = cursor.getLong(sizeIndex); // Retrieve the file size.
                }
            }
        } catch (Exception e) {
            // Log the error or handle it gracefully if file size retrieval fails.
            e.printStackTrace();
            size = 0; // Return 0 if there's an error.
        } finally {
            // Always close the cursor to prevent resource leaks.
            if (cursor != null) {
                cursor.close();
            }
        }
        return size;
    }

    /**
     * Converts a file size in bytes to a human-readable string format (e.g., "10.5 MB").
     *
     * @param size The file size in bytes.
     * @return A formatted string representing the file size.
     */
    private String readableFileSize(long size) {
        if (size <= 0) return "0 B"; // Handle zero or negative sizes.

        // Units for file size (Bytes, Kilobytes, Megabytes, Gigabytes).
        final String[] units = {"B", "KB", "MB", "GB"};
        // Calculate the appropriate unit group (0 for B, 1 for KB, etc.).
        // Math.log10(size) / Math.log10(1024) gives the power of 1024 that 'size' is.
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        // Format the number to one decimal place.
        // Divide the size by the corresponding power of 1024 and append the unit.
        return new DecimalFormat("#,##0.#")
            .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}



* **AndroidManifest.xml**
Permissions: Includes android.permission.READ_EXTERNAL_STORAGE to allow the app to read media files.
android:exported="true": This attribute is essential for Android 12 (API 31) and higher. It explicitly declares that MainActivity can be launched by external components (like the system's file picker via an intent-filter). Without this, the app would crash on newer Android versions.

<?xml version="1.0" encoding="utf-8"?>
<!--
    This is the main layout file for the MyMediaApp application.
    It defines the user interface elements that allow users to select media files
    and display information about the selected file.
-->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:padding="20dp"
    android:gravity="center"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <!--
        This Button allows the user to initiate the file selection process.
        When clicked, it will open the device's media picker to let the user
        choose an image or video file.
    -->
    <Button
        android:id="@+id/selectFileButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Select Image or Video" />
    <!--
        This TextView is used to display information about the selected file,
        including its type (Image/Video), size, and whether it's accepted or too large.
        The text will be updated dynamically by the MainActivity.java code.
    -->
    <TextView
        android:id="@+id/fileInfoText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="File info will appear here"
        android:paddingTop="20dp" />
</LinearLayout>

🧪 Test Cases
The application has been tested against the following scenarios:
File Type           Size                Expected Output
image.jpg           3 MB                  Accepted ✅
image.png           6 MB                 Too Large ❌
video.mp4          15 MB                  Accepted ✅
video.mov          25 MB                 Too Large ❌

🚧 Possible Enhancements
This project can be extended with the following functionalities:
Compress oversized files: Implement logic to compress images or videos that exceed the set size limits.
Upload functionality: Add options to upload the selected and validated files to cloud storage like Firebase or save them locally to a specific app directory.
Preview selected media: Display a thumbnail or a small preview of the selected image or video within the app.
Support for newer Scoped Storage APIs: Update file handling to comply with Android's modern Scoped Storage guidelines for better privacy and security.

🤝 Contributing
Feel free to fork this repository, submit pull requests, or open issues to suggest improvements or report bugs.
 📄 License
This project is open-sourced under the MIT License.
