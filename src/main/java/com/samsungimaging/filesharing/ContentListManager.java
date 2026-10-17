package com.samsungimaging.filesharing;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import com.samsungimaging.filesharing.adapter.ContentArrayAdapter;
import com.samsungimaging.filesharing.util.Logger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import org.cybergarage.http.HTTP;

/* JADX INFO: loaded from: classes.dex */
public class ContentListManager {
    private Handler mHandler;
    private TimerTask mTimer;
    private static final String TAG = ContentListManager.class.getSimpleName();
    public static boolean mcancelsaving = false;
    static boolean isBlocked = false;
    public ContentArrayAdapter mAdapter = null;
    public long mTotalDownloadImageSize = 0;
    public int mTotalDownloadImageCount = 0;
    public long mDownloadedImageSize = 0;
    private int mCurrentSavingFileIndex = -1;
    private Timer timer = new Timer();
    HttpURLConnection connection = null;
    private FileOutputStream out = null;
    private InputStream in = null;
    private BufferedInputStream bis = null;
    private String mUrl = "";

    public ContentListManager(Handler handler) {
        this.mHandler = null;
        this.mHandler = handler;
        setTotalDownloadImageSize(0L);
        setDownloadedImageSize(0L);
        setCurrentSavingFileIndex(-1);
    }

    public void cancelSaving() {
        Logger.e(TAG, "cancelSaving start!!!");
        mcancelsaving = true;
        this.mAdapter.notifyDataSetChanged();
        Logger.i("cancelSaving", "3--- size is  ---3");
        setTotalDownloadImageSize(0L);
        setTotalDownloadImageCount(0);
        setDownloadedImageSize(0L);
        setCurrentSavingFileIndex(-1);
    }

    public void stopImageLoader() {
        Logger.d(TAG, "stopImageLoader");
        if (this.mAdapter != null && this.mAdapter.mImageLoader != null) {
            this.mAdapter.mImageLoader.clearCache();
        }
    }

    private class SaveAllFileTask extends AsyncTask<Context, String, Void> {
        private SaveAllFileTask() {
        }

        /* synthetic */ SaveAllFileTask(ContentListManager contentListManager, SaveAllFileTask saveAllFileTask) {
            this();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            ContentListManager.mcancelsaving = false;
            ContentListManager.this.mHandler.sendEmptyMessage(34);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Context... params) {
            ContentListManager.this.setTotalDownloadImageSize(0L);
            for (int i = 0; i < ContentListManager.this.mAdapter.getCount() && !ContentListManager.mcancelsaving; i++) {
                Item content = ContentListManager.this.mAdapter.getItem(i);
                if (content.getItemState() == 2 && !ContentListManager.mcancelsaving) {
                    String url = content.getRes();
                    String filePath = ContentListManager.this.getdownloadFileName(content, url);
                    ContentListManager.this.setCurrentSavingFileIndex(i);
                    Logger.d(ContentListManager.TAG, String.valueOf(filePath) + " : " + url);
                    if (filePath != null) {
                        ContentListManager.this.setTotalDownloadImageCount(ContentListManager.this.getTotalDownloadImageCount() + 1);
                        try {
                            Thread.sleep(200L);
                        } catch (InterruptedException e) {
                            ContentListManager.mcancelsaving = true;
                            e.printStackTrace();
                        }
                        try {
                            ContentListManager.this.setDownloadedImageSize(0L);
                            ContentListManager.this.saveFile(params[0], url, filePath, i);
                        } catch (MalformedURLException e2) {
                            ContentListManager.mcancelsaving = true;
                            File file = new File(filePath);
                            if (file.exists()) {
                                file.delete();
                            }
                            e2.printStackTrace();
                        } catch (IOException e3) {
                            ContentListManager.mcancelsaving = true;
                            File file2 = new File(filePath);
                            if (file2.exists()) {
                                file2.delete();
                            }
                            e3.printStackTrace();
                        }
                        if (Utils.isMemoryFull()) {
                            ContentListManager.mcancelsaving = true;
                            ContentListManager.this.mHandler.sendEmptyMessage(21);
                        }
                    }
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            ContentListManager.this.mHandler.sendEmptyMessage(38);
            if (!ContentListManager.mcancelsaving && !FileSharing.mCancel) {
                ContentListManager.this.mHandler.sendEmptyMessage(4);
                FileSharing.mCancel = false;
            } else {
                FileSharing.mCancel = false;
            }
            ContentListManager.this.setTotalDownloadImageSize(0L);
            ContentListManager.this.setTotalDownloadImageCount(0);
            ContentListManager.this.setDownloadedImageSize(0L);
            ContentListManager.this.setCurrentSavingFileIndex(-1);
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            FileSharing.mCancel = false;
            try {
                if (ContentListManager.this.bis != null) {
                    ContentListManager.this.bis.close();
                    ContentListManager.this.bis = null;
                }
                if (ContentListManager.this.in != null) {
                    ContentListManager.this.in.close();
                    ContentListManager.this.in = null;
                }
                if (ContentListManager.this.out != null) {
                    ContentListManager.this.out.close();
                    ContentListManager.this.out = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onCancelled();
        }
    }

    private class SaveAllFileTaskQF30 extends AsyncTask<Context, String, Void> {
        private SaveAllFileTaskQF30() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            ContentListManager.mcancelsaving = false;
            ContentListManager.this.mHandler.sendEmptyMessage(34);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Context... params) {
            ContentListManager.this.setTotalDownloadImageSize(0L);
            for (int i = 0; i < ContentListManager.this.mAdapter.getCount() && !ContentListManager.mcancelsaving; i++) {
                Item content = ContentListManager.this.mAdapter.getItem(i);
                if (content.getItemState() == 2 && !ContentListManager.mcancelsaving) {
                    String url = content.getRes();
                    String filePath = ContentListManager.this.getdownloadFileName(content, url);
                    ContentListManager.this.setCurrentSavingFileIndex(i);
                    Logger.d(ContentListManager.TAG, String.valueOf(filePath) + " : " + url);
                    if (filePath != null) {
                        ContentListManager.this.setTotalDownloadImageCount(ContentListManager.this.getTotalDownloadImageCount() + 1);
                        try {
                            Thread.sleep(200L);
                        } catch (InterruptedException e) {
                            ContentListManager.mcancelsaving = true;
                            e.printStackTrace();
                        }
                        ContentListManager.this.setDownloadedImageSize(0L);
                        try {
                            ContentListManager.this.out = new FileOutputStream(filePath);
                        } catch (FileNotFoundException e2) {
                            ContentListManager.mcancelsaving = true;
                            File file = new File(filePath);
                            if (file.exists()) {
                                file.delete();
                            }
                            e2.printStackTrace();
                        }
                        if (FileSharing.mPreconnectedSsid == null || (!FileSharing.mPreconnectedSsid.contains("QF30") && !FileSharing.mPreconnectedSsid.contains("EX2"))) {
                            do {
                                try {
                                    if (!ContentListManager.this.saveFile(params[0], url, filePath, i)) {
                                        break;
                                    }
                                    Logger.d(ContentListManager.TAG, "mcancelsaving : " + ContentListManager.mcancelsaving);
                                } catch (MalformedURLException e3) {
                                    ContentListManager.mcancelsaving = true;
                                    File file2 = new File(filePath);
                                    if (file2.exists()) {
                                        file2.delete();
                                    }
                                    e3.printStackTrace();
                                    break;
                                } catch (IOException e4) {
                                    ContentListManager.mcancelsaving = true;
                                    File file3 = new File(filePath);
                                    if (file3.exists()) {
                                        file3.delete();
                                    }
                                    e4.printStackTrace();
                                    break;
                                }
                            } while (!ContentListManager.mcancelsaving);
                        } else {
                            while (ContentListManager.this.saveFileQF30(params[0], url, filePath, i)) {
                                Logger.d(ContentListManager.TAG, "mcancelsaving : " + ContentListManager.mcancelsaving);
                                if (ContentListManager.mcancelsaving) {
                                    break;
                                }
                            }
                        }
                        try {
                            if (ContentListManager.this.out != null) {
                                Logger.d(ContentListManager.TAG, "out close 01");
                                ContentListManager.this.out.close();
                                ContentListManager.this.out = null;
                            }
                        } catch (IOException e5) {
                            Logger.d(ContentListManager.TAG, "IOException 04");
                            e5.printStackTrace();
                        }
                        if (Utils.isMemoryFull()) {
                            ContentListManager.mcancelsaving = true;
                            ContentListManager.this.mHandler.sendEmptyMessage(21);
                        }
                    }
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            ContentListManager.this.mHandler.sendEmptyMessage(38);
            if (!ContentListManager.mcancelsaving && !FileSharing.mCancel) {
                ContentListManager.this.mHandler.sendEmptyMessage(4);
                FileSharing.mCancel = false;
            } else {
                FileSharing.mCancel = false;
            }
            ContentListManager.this.setTotalDownloadImageSize(0L);
            ContentListManager.this.setTotalDownloadImageCount(0);
            ContentListManager.this.setDownloadedImageSize(0L);
            ContentListManager.this.setCurrentSavingFileIndex(-1);
        }
    }

    public void SaveAllFileToPhone(Context context) {
        new SaveAllFileTask(this, null).execute(context);
    }

    public long getTotalDownloadImageSize() {
        return this.mTotalDownloadImageSize;
    }

    public void setTotalDownloadImageSize(long totalDownloadImageSize) {
        if (totalDownloadImageSize >= 0) {
            this.mTotalDownloadImageSize = totalDownloadImageSize;
        }
    }

    public int getTotalDownloadImageCount() {
        return this.mTotalDownloadImageCount;
    }

    public void setTotalDownloadImageCount(int totalDownloadImageCount) {
        if (totalDownloadImageCount >= 0) {
            this.mTotalDownloadImageCount = totalDownloadImageCount;
        }
    }

    public long getDownloadedImageSize() {
        return this.mDownloadedImageSize;
    }

    public void setDownloadedImageSize(long downloadedImageSize) {
        if (downloadedImageSize >= 0) {
            this.mDownloadedImageSize = downloadedImageSize;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getdownloadFileName(Item file, String url) {
        String ext;
        String fileName = file.getTitle();
        Logger.d(TAG, "-=> getdownloadFileName fileName=" + fileName);
        Logger.d(TAG, "-=> getdownloadFileName url=" + url);
        if (!fileName.contains(".") && (ext = Utils.getExtention(url.toLowerCase())) != null && ext.length() > 0) {
            fileName = String.valueOf(fileName) + "." + ext;
        }
        return getCreatedFileName(Utils.renameFile(Utils.getDefaultStorage(), fileName));
    }

    private String getCreatedFileName(String fileName) {
        String filePath = String.valueOf(Utils.getDefaultStorage()) + "/" + fileName;
        return filePath;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean saveFileQF30(Context context, String url, String path, int index) {
        this.mUrl = url;
        byte[] buffer = new byte[4096];
        try {
            try {
                try {
                    try {
                        this.connection = (HttpURLConnection) new URL(url).openConnection();
                        this.connection.setRequestProperty(HTTP.RANGE, "bytes=" + getDownloadedImageSize() + "-");
                        this.connection.setDoInput(true);
                        this.in = this.connection.getInputStream();
                        this.bis = new BufferedInputStream(this.in, 8192);
                        int count = 0;
                        long intervalSize = getProgressUpdateIntervalSize(Long.parseLong(this.mAdapter.getItem(index).getSize()));
                        startTimer();
                        while (true) {
                            int cnt = this.bis.read(buffer);
                            if (cnt >= 0) {
                                stopTimer();
                                if (mcancelsaving) {
                                    break;
                                }
                                setDownloadedImageSize(getDownloadedImageSize() + ((long) cnt));
                                setTotalDownloadImageSize(getTotalDownloadImageSize() + ((long) cnt));
                                if (getDownloadedImageSize() > ((long) count) * intervalSize) {
                                    count++;
                                    this.mHandler.sendEmptyMessage(35);
                                }
                                this.out.write(buffer, 0, cnt);
                                if (this.in == null) {
                                    this.connection = (HttpURLConnection) new URL(url).openConnection();
                                    this.connection.setRequestProperty(HTTP.RANGE, "bytes=" + getDownloadedImageSize() + "-");
                                    this.connection.setDoInput(true);
                                    this.in = this.connection.getInputStream();
                                }
                                if (this.bis == null) {
                                    this.bis = new BufferedInputStream(this.in);
                                }
                                startTimer();
                            } else {
                                break;
                            }
                        }
                        this.out.flush();
                        if (mcancelsaving) {
                            Logger.d(TAG, "STATE_CHECKED index : " + index);
                            this.mAdapter.getItem(index).setItemState(2);
                            if (this.bis != null) {
                                this.bis.close();
                                this.bis = null;
                            }
                            if (this.in != null) {
                                this.in.close();
                                this.in = null;
                            }
                            if (this.out != null) {
                                Logger.d(TAG, "out close 01");
                                this.out.close();
                                this.out = null;
                            }
                        } else {
                            this.mHandler.sendEmptyMessage(35);
                            this.mAdapter.getItem(index).setItemState(3);
                            String mimetype = Utils.getMimetype(path);
                            MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{mimetype}, new MediaScannerConnection.OnScanCompletedListener() { // from class: com.samsungimaging.filesharing.ContentListManager.1
                                @Override // android.media.MediaScannerConnection.OnScanCompletedListener
                                public void onScanCompleted(String path2, Uri uri) {
                                }
                            });
                        }
                        stopTimer();
                        if (mcancelsaving) {
                            File file = new File(path);
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        try {
                            if (this.bis != null) {
                                this.bis.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                        } catch (IOException e) {
                            Logger.d(TAG, "IOException 02");
                            e.printStackTrace();
                        }
                    } catch (IOException e2) {
                        if (!isBlocked) {
                            mcancelsaving = true;
                        }
                        Logger.d(TAG, "IOException 01");
                        e2.printStackTrace();
                        if (mcancelsaving) {
                            File file2 = new File(path);
                            if (file2.exists()) {
                                file2.delete();
                            }
                        }
                        try {
                            if (this.bis != null) {
                                this.bis.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                        } catch (IOException e3) {
                            Logger.d(TAG, "IOException 02");
                            e3.printStackTrace();
                        }
                    } catch (NullPointerException e4) {
                        mcancelsaving = true;
                        Logger.d(TAG, "NullPointerException");
                        e4.printStackTrace();
                        if (mcancelsaving) {
                            File file3 = new File(path);
                            if (file3.exists()) {
                                file3.delete();
                            }
                        }
                        try {
                            if (this.bis != null) {
                                this.bis.close();
                            }
                            if (this.in != null) {
                                this.in.close();
                            }
                        } catch (IOException e5) {
                            Logger.d(TAG, "IOException 02");
                            e5.printStackTrace();
                        }
                    }
                } catch (FileNotFoundException e6) {
                    mcancelsaving = true;
                    Logger.d(TAG, "FileNotFoundException");
                    e6.printStackTrace();
                    if (mcancelsaving) {
                        File file4 = new File(path);
                        if (file4.exists()) {
                            file4.delete();
                        }
                    }
                    try {
                        if (this.bis != null) {
                            this.bis.close();
                        }
                        if (this.in != null) {
                            this.in.close();
                        }
                    } catch (IOException e7) {
                        Logger.d(TAG, "IOException 02");
                        e7.printStackTrace();
                    }
                }
            } catch (MalformedURLException e8) {
                mcancelsaving = true;
                Logger.d(TAG, "MalformedURLException");
                e8.printStackTrace();
                if (mcancelsaving) {
                    File file5 = new File(path);
                    if (file5.exists()) {
                        file5.delete();
                    }
                }
                try {
                    if (this.bis != null) {
                        this.bis.close();
                    }
                    if (this.in != null) {
                        this.in.close();
                    }
                } catch (IOException e9) {
                    Logger.d(TAG, "IOException 02");
                    e9.printStackTrace();
                }
            }
            return isBlocked;
        } catch (Throwable th) {
            if (mcancelsaving) {
                File file6 = new File(path);
                if (file6.exists()) {
                    file6.delete();
                }
            }
            try {
                if (this.bis != null) {
                    this.bis.close();
                }
                if (this.in != null) {
                    this.in.close();
                }
            } catch (IOException e10) {
                Logger.d(TAG, "IOException 02");
                e10.printStackTrace();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean saveFile(Context context, String url, String path, int index) throws IOException {
        byte[] buffer = new byte[4096];
        try {
            this.out = new FileOutputStream(path);
            this.in = new URL(url).openStream();
            this.bis = new BufferedInputStream(this.in, 8192);
            int count = 0;
            long intervalSize = getProgressUpdateIntervalSize(Long.parseLong(this.mAdapter.getItem(index).getSize()));
            while (true) {
                int cnt = this.bis.read(buffer);
                if (cnt < 0 || mcancelsaving) {
                    break;
                }
                setDownloadedImageSize(getDownloadedImageSize() + ((long) cnt));
                setTotalDownloadImageSize(getTotalDownloadImageSize() + ((long) cnt));
                if (getDownloadedImageSize() > ((long) count) * intervalSize) {
                    count++;
                    this.mHandler.sendEmptyMessage(35);
                }
                this.out.write(buffer, 0, cnt);
            }
            this.mHandler.sendEmptyMessage(35);
            this.out.flush();
            if (!mcancelsaving) {
                this.mAdapter.getItem(index).setItemState(3);
                String mimetype = Utils.getMimetype(path);
                MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{mimetype}, new MediaScannerConnection.OnScanCompletedListener() { // from class: com.samsungimaging.filesharing.ContentListManager.2
                    @Override // android.media.MediaScannerConnection.OnScanCompletedListener
                    public void onScanCompleted(String path2, Uri uri) {
                    }
                });
            } else {
                this.mAdapter.getItem(index).setItemState(2);
                if (this.bis != null) {
                    this.bis.close();
                    this.bis = null;
                }
                if (this.in != null) {
                    this.in.close();
                    this.in = null;
                }
                if (this.out != null) {
                    this.out.close();
                    this.out = null;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (this.bis != null) {
                this.bis.close();
            }
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
        }
    }

    public int getCurrentSavingFileIndex() {
        return this.mCurrentSavingFileIndex;
    }

    public void setCurrentSavingFileIndex(int currentSavingFileIndex) {
        this.mCurrentSavingFileIndex = currentSavingFileIndex;
    }

    private long getProgressUpdateIntervalSize(long fileSize) {
        long intervalSize;
        long minIntervalSize = 1024 * 80;
        long maxIntervalSize = 1024 * 800;
        if (fileSize < 1048576) {
            intervalSize = minIntervalSize;
        } else if (fileSize < 104857600) {
            long intervalSize2 = ((maxIntervalSize - minIntervalSize) / 103809024) * fileSize;
            intervalSize = (intervalSize2 + minIntervalSize) - ((1048576 * (maxIntervalSize - minIntervalSize)) / 103809024);
        } else {
            intervalSize = maxIntervalSize;
        }
        Logger.d(TAG, "intervalSize : " + intervalSize + " fileSize : " + fileSize);
        return intervalSize;
    }

    private void startTimer() {
        Logger.d(TAG, "startTimer");
        this.mTimer = new TimerTask() { // from class: com.samsungimaging.filesharing.ContentListManager.3
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                Logger.d(ContentListManager.TAG, "Blocking....");
                ContentListManager.isBlocked = true;
                try {
                    if (ContentListManager.this.bis != null) {
                        ContentListManager.this.bis.close();
                        ContentListManager.this.bis = null;
                    }
                    if (ContentListManager.this.in != null) {
                        ContentListManager.this.in.close();
                        ContentListManager.this.in = null;
                    }
                } catch (IOException e) {
                    Logger.d(ContentListManager.TAG, "IOException 03");
                    e.printStackTrace();
                }
            }
        };
        isBlocked = false;
        this.timer.schedule(this.mTimer, 3000L);
    }

    private void stopTimer() {
        Logger.d(TAG, "stopTimer");
        isBlocked = false;
        this.mTimer.cancel();
    }
}
