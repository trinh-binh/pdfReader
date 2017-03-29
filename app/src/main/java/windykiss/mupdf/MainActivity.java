package windykiss.mupdf;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.artifex.mupdfdemo.FilePicker;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.artifex.mupdfdemo.SearchTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PDFViewFragment";
    @BindView(R.id.pdflayout)
    RelativeLayout pdfLayout;
    @BindView(R.id.ll_pdf)
    RelativeLayout ll_pdf;
    @OnClick(R.id.btn_full)
    public void OnFullClick(){
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth(); // ((display.getWidth()*20)/100)
        int height = display.getHeight();// ((display.getHeight()*30)/100)
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width,height);
        ll_pdf.setLayoutParams(parms);
    }

    private MuPDFCore core;
    private MuPDFReaderView mDocView;
    private SearchTask mSearchTask;
    private String mFilePath;
    private Context mContext;
    private FilePicker.FilePickerSupport filePickerSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);
        filePickerSupport = new FilePicker.FilePickerSupport() {
            @Override
            public void performPickFor(FilePicker picker) {

            }
        };
        //initData();
        String url = "https://media.blackhat.com/ad-12/Hamon/bh-ad-12-malicious%20URI-Hamon-WP.pdf";
        downloadFromUrl(url);

    }

    private void initData() {
        //download file here
        String uri = Constants.CACHE_FOLDER+"abc.pdf";
        //String uri = "https://media.blackhat.com/ad-12/Hamon/bh-ad-12-malicious%20URI-Hamon-WP.pdf";
        display(uri);
    }

    private void display(String mFilePath) {
        core = openFile(Uri.decode(mFilePath));

        if (core != null && core.countPages() == 0) {
            core = null;
        }
        if (core == null || core.countPages() == 0 || core.countPages() == -1) {
            Log.e(TAG, "Document Not Opening");
        }
        if (core != null) {
            mDocView = new MuPDFReaderView(this) {
                @Override
                protected void onMoveToChild(int i) {
                    if (core == null)
                        return;
                    super.onMoveToChild(i);
                }

            };
            mDocView.setAdapter(new MuPDFPageAdapter(mContext,filePickerSupport,core));
            pdfLayout.addView(mDocView);
        }
    }


    private MuPDFCore openBuffer(byte buffer[]) {
        System.out.println("Trying to open byte buffer");
        try {
            core = new MuPDFCore(mContext, buffer,"magic");

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        return core;
    }

    private MuPDFCore openFile(String path) {
        int lastSlashPos = path.lastIndexOf('/');
        mFilePath = new String(lastSlashPos == -1
                ? path
                : path.substring(lastSlashPos + 1));
        try {
            Log.d("windy.f",mFilePath);
            core = new MuPDFCore(mContext, path);
            // New file: drop the old outline data
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        return core;
    }

    public void onDestroy() {
        if (core != null)
            core.onDestroy();
        core = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSearchTask != null)
            mSearchTask.stop();
    }
    public void downloadFromUrl(String url){
        try {
            String folder =Environment.getExternalStorageDirectory() + "/cache/";
            new DownloadFileAsync(mContext, url, folder, new DownloadFileAsync.DownloadListener() {
                @Override
                public void onSuccess() {
                    //display(chapter.getCacheDirectUrl());
                }

                @Override
                public void onError() {
//                            DialogUtility.alert(mContext, R.string.error_LoadPage);
//                    if (!chapter.isCacheAttachedFile()) {
//                        try {
//                            new DownloadFileAsync(mContext, chapter.getAttachedFile(), GlobalValue.CACHE_FOLDER, new DownloadFileAsync.DownloadListener() {
//                                @Override
//                                public void onSuccess() {
//                                   // display(chapter.getCacheAttachedFileUrl());
//                                }
//
//                                @Override
//                                public void onError() {
//                                    //DialogUtility.alert(mContext, R.string.error_LoadPage);
//                                }
//                            }).execute();
//                        } catch (Exception ex) {
//                            Log.e(TAG, ex.getMessage());
//                        }
//
//                    } else {
//                        //display(chapter.getCacheAttachedFileUrl());
//                    }
                }
            }).execute();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
    private class FileDownloader extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
//            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            int count;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setChunkedStreamingMode(0);
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.connect();

                int lenghtOfFile = connection.getContentLength();
                Log.d("windy.f", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                File file =new File(Environment.getExternalStorageDirectory()+"/pdf/","abc.pdf");
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    //publishProgress();
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
//
                Log.e("Download", " VAo day 44 " + e.getMessage());
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressBar.dismiss();
            //dropboxDownloadListener.onDownloadDone();
            initData();
        }
    }
    public interface DropboxDownloadListener{
        void onDownloadDone();
    }
}
