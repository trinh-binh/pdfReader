package windykiss.mupdf;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by windykiss on 3/28/2017.
 * Project: MuPDF
 */

public class DownloadFileAsync extends AsyncTask<String, String, String> {

    Context context;
    private int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private String onlineFileUrl;
    private String offlineFileURL, offlineFolder;
    private DownloadListener listener;

    public interface DownloadListener {
        public void onSuccess();

        public void onError();
    }

    public DownloadFileAsync(Context context, String onlineUrl, String offlineFolderUrl, String fileName, DownloadListener listener) {
        this.context = context;
        this.onlineFileUrl = onlineUrl;
        this.offlineFolder = offlineFolderUrl;
        this.offlineFileURL=offlineFolder+fileName;
        this.listener = listener;
    }

    public DownloadFileAsync(Context context, String onlineUrl, String offlineFolderUrl, DownloadListener listener) {
        this.context = context;
        this.onlineFileUrl = onlineUrl;
        this.offlineFolder = offlineFolderUrl;
        this.offlineFileURL=offlineFolder+getFullFileNameFromUrl(onlineUrl);
        this.listener = listener;
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("loading");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        File folder = new File(this.offlineFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Log.e("DownloadFileAsync", "file url :" + this.offlineFileURL);
        showProgressDialog();

    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {
            URL url = new URL(this.onlineFileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(0);
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();

            int lenghtOfFile = connection.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(offlineFileURL);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
//
            Log.e("Download", " VAo day 44 " + e.getMessage());
            return "false";
        }
        return "done";
    }

    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        DIALOG_DOWNLOAD_PROGRESS = Integer.parseInt(progress[0]);
        Log.d("ANDRO_ASYNC", " " + DIALOG_DOWNLOAD_PROGRESS);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (DIALOG_DOWNLOAD_PROGRESS == 100) {
            listener.onSuccess();
        } else {
            listener.onError();
        }
    }

    private static String getFullFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

    private static String getFullFileNameWithoutExtensionFromUrl(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private static String getFileExtension(String url) {
        return url.substring(url.lastIndexOf("."));
    }
}
