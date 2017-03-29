package windykiss.mupdf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by windykiss on 3/26/2017.
 * Project: MuPDF
 */

public class WebViewActivity extends AppCompatActivity {
    @BindView(R.id.web_view)
    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_layout);
        ButterKnife.bind(this);
        webView.getSettings().setJavaScriptEnabled(true);
        String pdf_url = "https://media.blackhat.com/ad-12/Hamon/bh-ad-12-malicious%20URI-Hamon-WP.pdf";
        //webView.loadUrl(pdf_url);
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf_url);
    }
}
