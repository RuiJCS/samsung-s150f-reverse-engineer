package com.samsungimaging.connectionmanager.app.pullservice.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.pullservice.PullService;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.ImageLoader;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Item;
import com.samsungimaging.connectionmanager.app.pullservice.demo.ml.Utils;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CustomDialogFileSave extends AsyncTask<Void, Bundle, Integer> {
    private static final Trace.Tag TAG = Trace.Tag.COMMON;
    private Button closenotice;
    private LinearLayout layout;
    private Context mContext;
    private Handler mHandler;
    private ImageLoader mImageLoader;
    private boolean mIsResizeMode;
    private List<Item> mItems;
    private TextView ml_partInfo_downloadedSize;
    private TextView ml_partInfo_fileName;
    private TextView ml_partInfo_fileSize;
    private ProgressBar ml_partInfo_progressBar;
    private LinearLayout ml_totalInfo;
    private TextView ml_totalInfo_currentDownloadedSize;
    private TextView ml_totalInfo_currentIndex;
    private ProgressBar ml_totalInfo_progressBar;
    private LinearLayout ml_totalInfo_size;
    private TextView ml_totalInfo_totalCount;
    private TextView ml_totalInfo_totalSize;
    private ImageView showPicture;
    private TextView txtView;
    private TextView txtView2;
    private long mPartInfo_itemSize = 0;
    private long mPartInfo_itemDownloadedSize = 0;
    private long mTotalInfo_TotalCheckedItemSize = 0;
    private long mTotalInfo_DownloadedItemSize = 0;
    private Dialog mProgressDialog = null;
    private Item mCurrentSavingItem = null;
    private String mCurrentSavingItemPath = null;
    private DialogInterface.OnClickListener clickListener = null;
    private DialogInterface.OnKeyListener keyListener = null;
    Dialog notice = null;

    public static class Command {
        public static final int END = 2;
        public static final int START = 0;
        public static final int UPDATE = 1;
    }

    public static class ResultID {
        public static final int IO_EXCEPTION = 1;
        public static final int MEMORY_FULL = 2;
        public static final int SUCCESS = 0;
    }

    public CustomDialogFileSave(Context context, Handler handler, boolean isResizeMode, List<Item> items, ImageLoader imageLoader) {
        this.mContext = null;
        this.mHandler = null;
        this.mIsResizeMode = false;
        this.mItems = null;
        this.mImageLoader = null;
        Trace.d(TAG, "start CustomDialogFileSave() isResizeMode : " + isResizeMode);
        this.mContext = context;
        this.mHandler = handler;
        this.mIsResizeMode = isResizeMode;
        this.mItems = items;
        this.mImageLoader = imageLoader;
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        Trace.d(TAG, "start onPreExecute()");
        showProgress();
        super.onPreExecute();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Integer doInBackground(Void... params) {
        Trace.d(TAG, "start doInBackground()");
        int itemCount = this.mItems.size();
        Trace.d(TAG, "Download item count : " + itemCount);
        for (int i = 0; i < itemCount && !isCancelled(); i++) {
            try {
                if (Long.parseLong(this.mItems.get(i).getSize()) < Utils.getAvailableExternalMemorySize()) {
                    saveFile(this.mItems.get(i));
                } else {
                    Log.d("Not enough memory", "Not enough memory is available on phone");
                    return 2;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onProgressUpdate(Bundle... values) {
        Integer index = Integer.valueOf(values[0].getInt("index"));
        String path = values[0].getString("path");
        switch (values[0].getInt("command")) {
            case 0:
                Trace.d(TAG, "START index : " + index + " path : " + path);
                this.ml_partInfo_fileName.setText(this.mCurrentSavingItem.getTitle());
                this.ml_partInfo_downloadedSize.setText(Utils.formatSize(0L));
                this.ml_partInfo_fileSize.setText(String.valueOf(Utils.formatSize(this.mPartInfo_itemSize)));
                this.ml_partInfo_progressBar.setProgress(0);
                this.ml_totalInfo_currentDownloadedSize.setText("0");
                this.ml_totalInfo_currentIndex.setText(String.valueOf(index));
                if (this.mIsResizeMode) {
                    this.ml_totalInfo_progressBar.setProgress(index.intValue());
                } else {
                    this.ml_totalInfo_progressBar.setProgress(0);
                }
                break;
            case 1:
                this.ml_partInfo_fileName.setText(this.mCurrentSavingItem.getTitle());
                this.ml_partInfo_downloadedSize.setText(String.valueOf(Utils.formatSize(this.mPartInfo_itemDownloadedSize)));
                this.ml_partInfo_fileSize.setText(String.valueOf(Utils.formatSize(this.mPartInfo_itemSize)));
                this.ml_partInfo_progressBar.setProgress((int) ((this.mPartInfo_itemDownloadedSize * 1000) / this.mPartInfo_itemSize));
                this.ml_totalInfo_currentDownloadedSize.setText(String.valueOf(Utils.formatSize(this.mTotalInfo_DownloadedItemSize)));
                this.ml_totalInfo_currentIndex.setText(String.valueOf(index));
                if (this.mIsResizeMode) {
                    this.ml_totalInfo_progressBar.setProgress(index.intValue());
                } else {
                    this.ml_totalInfo_progressBar.setProgress((int) ((this.mTotalInfo_DownloadedItemSize * 1000) / this.mTotalInfo_TotalCheckedItemSize));
                }
                break;
            case 2:
                Trace.d(TAG, "END index : " + index + " path : " + path);
                this.ml_partInfo_progressBar.setProgress(1000);
                if (this.mIsResizeMode) {
                    this.ml_totalInfo_progressBar.setProgress(index.intValue() + 1);
                }
                break;
        }
        super.onProgressUpdate((Object[]) values);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Integer result) {
        Trace.d(TAG, "start onPostExecute()");
        hideProgress();
        switch (result.intValue()) {
            case 0:
                this.mHandler.sendEmptyMessage(4);
                break;
            case 1:
                this.mHandler.sendEmptyMessage(47);
                onCancelled();
                break;
            case 2:
                Trace.d(TAG, "start onPostExecute(): result memory full");
                this.mHandler.sendEmptyMessage(21);
                this.mHandler.sendEmptyMessage(36);
                break;
        }
        super.onPostExecute(result);
    }

    @Override // android.os.AsyncTask
    protected void onCancelled() {
        Trace.d(TAG, "start onCancelled() mCurrentSavingItemPath : " + this.mCurrentSavingItemPath);
        File file = new File(this.mCurrentSavingItemPath);
        if (file.exists()) {
            Trace.d(TAG, "delete file");
            file.delete();
        }
        this.mHandler.sendEmptyMessage(36);
        super.onCancelled();
    }

    private void showProgress() {
        Trace.d(TAG, "start showProgress()");
        int itemCount = this.mItems.size();
        View view = ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.ml_custom_progress_dialog, (ViewGroup) null);
        this.showPicture = (ImageView) view.findViewById(R.id.showPicture);
        this.ml_partInfo_fileName = (TextView) view.findViewById(R.id.ml_partInfo_fileName);
        this.ml_partInfo_downloadedSize = (TextView) view.findViewById(R.id.ml_partInfo_downloadedSize);
        this.ml_partInfo_fileSize = (TextView) view.findViewById(R.id.ml_partInfo_fileSize);
        this.ml_partInfo_progressBar = (ProgressBar) view.findViewById(R.id.ml_partInfo_progressBar);
        this.ml_totalInfo = (LinearLayout) view.findViewById(R.id.ml_totalInfo);
        this.ml_totalInfo_currentIndex = (TextView) view.findViewById(R.id.ml_totalInfo_currentIndex);
        this.ml_totalInfo_totalCount = (TextView) view.findViewById(R.id.ml_totalInfo_totalCount);
        this.ml_totalInfo_size = (LinearLayout) view.findViewById(R.id.ml_totalInfo_size);
        this.ml_totalInfo_currentDownloadedSize = (TextView) view.findViewById(R.id.ml_totalInfo_currentDownloadedSize);
        this.ml_totalInfo_totalSize = (TextView) view.findViewById(R.id.ml_totalInfo_totalSize);
        this.ml_totalInfo_progressBar = (ProgressBar) view.findViewById(R.id.ml_totalInfo_progressBar);
        this.ml_partInfo_fileName.setText("");
        this.ml_partInfo_progressBar.setMax(1000);
        this.ml_partInfo_downloadedSize.setText(String.valueOf(Utils.formatSize(0L)));
        this.ml_partInfo_fileSize.setText(String.valueOf(Utils.formatSize(0L)));
        if (this.mIsResizeMode) {
            this.ml_totalInfo_progressBar.setMax(itemCount);
            this.ml_totalInfo_size.setVisibility(4);
        } else {
            this.ml_totalInfo_progressBar.setMax(1000);
            for (int i = 0; i < itemCount; i++) {
                this.mTotalInfo_TotalCheckedItemSize += Long.parseLong(this.mItems.get(i).getSize());
            }
            this.ml_totalInfo_totalSize.setText(String.valueOf(Utils.formatSize(this.mTotalInfo_TotalCheckedItemSize)));
        }
        if (itemCount == 1) {
            this.ml_totalInfo.setVisibility(8);
            this.ml_totalInfo_progressBar.setVisibility(8);
        } else {
            this.ml_totalInfo_totalCount.setText(String.valueOf(itemCount));
        }
        this.ml_totalInfo_currentIndex.setText("0");
        this.mProgressDialog = new Dialog(this.mContext);
        this.mProgressDialog.requestWindowFeature(1);
        this.mProgressDialog.setContentView(view);
        this.closenotice = (Button) this.mProgressDialog.findViewById(R.id.btnOk);
        this.closenotice.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.dialog.CustomDialogFileSave.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CustomDialogFileSave.this.mProgressDialog.dismiss();
                CustomDialogFileSave.this.onCancelled();
                CustomDialogFileSave.this.cancel(true);
            }
        });
        this.mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.samsungimaging.connectionmanager.app.pullservice.dialog.CustomDialogFileSave.2
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    CustomDialogFileSave.this.mProgressDialog.dismiss();
                    CustomDialogFileSave.this.onCancelled();
                    CustomDialogFileSave.this.cancel(true);
                }
                return true;
            }
        });
        this.txtView = (TextView) this.mProgressDialog.findViewById(R.id.title);
        this.layout = (LinearLayout) this.mProgressDialog.findViewById(R.id.titleLayout2);
        this.layout.setVisibility(0);
        this.txtView.setText(R.string.ml_saving);
        this.mProgressDialog.setCanceledOnTouchOutside(false);
        this.mProgressDialog.show();
    }

    public void setNegativeButton(DialogInterface.OnClickListener listener) {
        this.clickListener = listener;
    }

    private void hideProgress() {
        Trace.d(TAG, "start hideProgress() mProgressDialog.isShowing : " + this.mProgressDialog.isShowing());
        try {
            this.mProgressDialog.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Trace.e(TAG, "Occured IllegalArgumentException on dismiss save dialog.");
            this.mProgressDialog = null;
        }
    }

    private void saveFile(Item item) throws IOException {
        int size;
        Trace.d(TAG, "start saveFile()");
        this.mCurrentSavingItem = item;
        this.mCurrentSavingItemPath = getDownloadFileName(item);
        this.mPartInfo_itemSize = Long.parseLong(item.getSize());
        this.mPartInfo_itemDownloadedSize = 0L;
        this.mImageLoader.setImage(this.mCurrentSavingItem.getThumbRes(), (Activity) this.mContext, this.showPicture, this.mCurrentSavingItem);
        FileOutputStream out = new FileOutputStream(this.mCurrentSavingItemPath);
        URL url = new URL(item.getRes());
        URLConnection con = url.openConnection();
        con.setReadTimeout(5000);
        con.connect();
        if (!Utils.getExtention(this.mCurrentSavingItem.getRes()).equalsIgnoreCase("MP4")) {
            this.mPartInfo_itemSize = con.getContentLength();
        }
        Bundle bundle = new Bundle();
        bundle.putString("path", this.mCurrentSavingItemPath);
        bundle.putInt("index", this.mItems.indexOf(this.mCurrentSavingItem));
        bundle.putInt("command", 0);
        publishProgress(bundle);
        InputStream in = con.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(in, 8192);
        int count = 0;
        long intervalSize = getProgressUpdateIntervalSize(this.mPartInfo_itemSize);
        byte[] buffer = new byte[4096];
        while (!isCancelled() && (size = bis.read(buffer)) >= 0) {
            this.mPartInfo_itemDownloadedSize += (long) size;
            this.mTotalInfo_DownloadedItemSize += (long) size;
            if (this.mPartInfo_itemDownloadedSize > ((long) count) * intervalSize) {
                bundle.putInt("command", 1);
                publishProgress(bundle);
                count++;
            }
            out.write(buffer, 0, size);
        }
        if (!isCancelled()) {
            this.mCurrentSavingItem.setItemState(3);
            bundle.putInt("command", 1);
            publishProgress(bundle);
            out.flush();
            bundle.putInt("command", 2);
            publishProgress(bundle);
            String mimeType = com.samsungimaging.connectionmanager.util.Utils.getMimeType(this.mCurrentSavingItemPath);
            MediaScannerConnection.scanFile(this.mContext, new String[]{this.mCurrentSavingItemPath}, new String[]{mimeType}, null);
            if (this.mContext instanceof PullService) {
                ((PullService) this.mContext).CheckDirtyCache(this.mCurrentSavingItemPath);
            }
            ExifInterface exif = new ExifInterface(this.mCurrentSavingItemPath);
            Trace.d(TAG, "System date : " + Calendar.getInstance().get(1) + "-" + (Calendar.getInstance().get(2) + 1) + "-" + Calendar.getInstance().get(5) + " exif date : " + exif.getAttribute("DateTime") + " sorting Info : " + this.mCurrentSavingItem.getDate());
        }
    }

    private String getDownloadFileName(Item item) {
        String ext;
        Trace.d(TAG, "start getDownloadFileName()");
        String downloadFileName = item.getTitle();
        if (!downloadFileName.contains(".") && (ext = Utils.getExtention(item.getRes().toLowerCase())) != null && ext.length() > 0) {
            downloadFileName = String.valueOf(downloadFileName) + "." + ext;
        }
        return String.valueOf(Utils.getDefaultStorage()) + "/" + Utils.renameFile(Utils.getDefaultStorage(), downloadFileName);
    }

    private long getProgressUpdateIntervalSize(long fileSize) {
        long intervalSize;
        Trace.d(TAG, "start getProgressUpdateIntervalSize()");
        long minIntervalSize = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID * 80;
        long maxIntervalSize = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID * 800;
        if (fileSize < 1048576) {
            intervalSize = minIntervalSize;
        } else if (fileSize < 104857600) {
            long intervalSize2 = ((maxIntervalSize - minIntervalSize) / 103809024) * fileSize;
            intervalSize = (intervalSize2 + minIntervalSize) - ((1048576 * (maxIntervalSize - minIntervalSize)) / 103809024);
        } else {
            intervalSize = maxIntervalSize;
        }
        Trace.d(TAG, "intervalSize : " + intervalSize + " fileSize : " + fileSize);
        return intervalSize;
    }
}
