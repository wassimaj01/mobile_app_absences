package com.example.mobile_app_absences;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
    }

    private void openPdfFile() {
        String filePath = "/data/user/0/com.example.mobile_app_absences/files/justification_20230531_141229.pdf.pdf";

        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            WebView webView = findViewById(R.id.webView);

            // Enable JavaScript for the WebView
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Load the PDF file using the PDF.js Viewer HTML page
            String pdfViewerHtml = "<html><head><title>PDF Viewer</title></head><body>" +
                    "<iframe src='file:///android_asset/pdfjs/web/viewer.html?file=" +
                    file.getAbsolutePath() + "' " +
                    "width='100%' height='100%' style='border: none;'></iframe></body></html>";
            webView.loadDataWithBaseURL("file:///android_asset/pdfjs/web/viewer.html", pdfViewerHtml, "text/html", "UTF-8", null);
        } else {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
        }
    }

}