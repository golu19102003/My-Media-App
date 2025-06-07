package com.example.mymediaapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends Activity {

    private static final int PICK_MEDIA = 1;
    private TextView fileInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectFileButton = findViewById(R.id.selectFileButton);
        fileInfoText = findViewById(R.id.fileInfoText);

        selectFileButton.setOnClickListener(v -> selectFile());
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        startActivityForResult(Intent.createChooser(intent, "Select Image or Video"), PICK_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            handleFile(uri);
        }
    }

    private void handleFile(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        long fileSize = getFileSize(uri);
        String readableSize = readableFileSize(fileSize);

        String fileType = (mimeType != null && mimeType.startsWith("image")) ? "Image" :
                (mimeType != null && mimeType.startsWith("video")) ? "Video" : "Unknown";

        String result = fileType + " selected\nSize: " + readableSize;

        if ((fileType.equals("Image") && fileSize <= 5 * 1024 * 1024) || // 5 MB
                (fileType.equals("Video") && fileSize <= 20 * 1024 * 1024)) { // 20 MB
            result += "\nStatus: Accepted ✅";
        } else {
            result += "\nStatus: Too Large ❌";
            Toast.makeText(this, "File too large!", Toast.LENGTH_SHORT).show();
        }

        fileInfoText.setText(result);
    }

    private long getFileSize(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        long size = cursor.getLong(sizeIndex);
        cursor.close();
        return size;
    }

    private String readableFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = {"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
