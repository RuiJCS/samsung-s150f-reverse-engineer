package com.samsungimaging.connectionmanager.app.pullservice.util;

import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Stack;

/* JADX INFO: loaded from: classes.dex */
public class ImageDownloader {
    Handler handler;
    int messageID;
    private Trace.Tag TAG = Trace.Tag.RVF;
    ImageDownloadQueue imageDownloadQueue = new ImageDownloadQueue();
    Downloader downloader = new Downloader();

    public ImageDownloader(Handler handler, int messageID) {
        this.handler = handler;
        this.messageID = messageID;
    }

    public void downloadImage(String url) {
        String[] urlPath = url.split("/");
        ImageItem imageItem = new ImageItem(url, FileManager.createFileName(urlPath[urlPath.length - 1]));
        synchronized (this.imageDownloadQueue.imageItems) {
            this.imageDownloadQueue.imageItems.push(imageItem);
            this.imageDownloadQueue.imageItems.notifyAll();
        }
        if (this.downloader.getState() == Thread.State.NEW) {
            this.downloader.start();
        }
    }

    public String syncDownloadImage(String url) throws Throwable {
        String[] urlPath = url.split("/");
        ImageItem imageItem = new ImageItem(url, FileManager.createFileName(urlPath[urlPath.length - 1]));
        this.downloader.download(imageItem.url, imageItem.saveFileName);
        return imageItem.saveFileName;
    }

    private class ImageItem {
        public String saveFileName;
        public String url;

        public ImageItem(String url, String saveFileName) {
            this.url = url;
            this.saveFileName = saveFileName;
        }
    }

    class ImageDownloadQueue {
        private Stack<ImageItem> imageItems = new Stack<>();

        ImageDownloadQueue() {
        }
    }

    class Downloader extends Thread {
        Downloader() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws Throwable {
            ImageItem imageItem;
            while (true) {
                try {
                    if (ImageDownloader.this.imageDownloadQueue.imageItems.size() == 0) {
                        synchronized (ImageDownloader.this.imageDownloadQueue.imageItems) {
                            ImageDownloader.this.imageDownloadQueue.imageItems.wait();
                        }
                    }
                    if (ImageDownloader.this.imageDownloadQueue.imageItems.size() != 0) {
                        synchronized (ImageDownloader.this.imageDownloadQueue.imageItems) {
                            imageItem = (ImageItem) ImageDownloader.this.imageDownloadQueue.imageItems.remove(0);
                        }
                        download(imageItem.url, imageItem.saveFileName);
                        Message msg = new Message();
                        msg.what = ImageDownloader.this.messageID;
                        msg.obj = imageItem.saveFileName;
                        ImageDownloader.this.handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Code duplicated, block: B:124:0x0169 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:126:0x0163 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:130:0x01a1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:132:0x019b A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:134:0x01b9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:136:0x012e A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:138:0x0128 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:142:0x016f A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:146:0x01c5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:148:0x01bf A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:150:0x0134 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:152:0x0195 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:165:? A[SYNTHETIC] */
        public void download(String srcPath, String destPath) throws Throwable {
            BufferedInputStream bin;
            Trace.d(ImageDownloader.this.TAG, "start download() srcPath : " + srcPath + " destPath : " + destPath);
            byte[] buffer = new byte[4096];
            FileOutputStream out = null;
            InputStream in = null;
            BufferedInputStream bin2 = null;
            try {
                try {
                    FileOutputStream out2 = new FileOutputStream(destPath);
                    try {
                        try {
                            in = new URL(srcPath).openStream();
                            while (true) {
                                try {
                                    int cnt = bin.read(buffer);
                                    if (cnt < 0) {
                                        break;
                                    } else {
                                        out2.write(buffer, 0, cnt);
                                    }
                                } catch (FileNotFoundException e) {
                                    e = e;
                                    bin2 = bin;
                                    out = out2;
                                    Trace.d(ImageDownloader.this.TAG, "Occured FileNotFoundException when download");
                                    e.printStackTrace();
                                    if (bin2 != null) {
                                        try {
                                            bin2.close();
                                        } catch (IOException e2) {
                                            e2.printStackTrace();
                                        }
                                        bin2 = null;
                                    }
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e3) {
                                            e3.printStackTrace();
                                        }
                                        in = null;
                                    }
                                    if (out != null) {
                                        try {
                                            out.close();
                                        } catch (IOException e4) {
                                            e4.printStackTrace();
                                        }
                                        out = null;
                                    }
                                } catch (MalformedURLException e5) {
                                    e = e5;
                                    bin2 = bin;
                                    out = out2;
                                    Trace.d(ImageDownloader.this.TAG, "Occured MalformedURLException when download");
                                    e.printStackTrace();
                                    if (bin2 != null) {
                                        try {
                                            bin2.close();
                                        } catch (IOException e6) {
                                            e6.printStackTrace();
                                        }
                                        bin2 = null;
                                    }
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e7) {
                                            e7.printStackTrace();
                                        }
                                        in = null;
                                    }
                                    if (out != null) {
                                        try {
                                            out.close();
                                        } catch (IOException e8) {
                                            e8.printStackTrace();
                                        }
                                        out = null;
                                    }
                                } catch (IOException e9) {
                                    e = e9;
                                    bin2 = bin;
                                    out = out2;
                                    Trace.d(ImageDownloader.this.TAG, "Occured IOException when download");
                                    e.printStackTrace();
                                    if (bin2 != null) {
                                        try {
                                            bin2.close();
                                        } catch (IOException e10) {
                                            e10.printStackTrace();
                                        }
                                        bin2 = null;
                                    }
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e11) {
                                            e11.printStackTrace();
                                        }
                                        in = null;
                                    }
                                    if (out != null) {
                                        try {
                                            out.close();
                                        } catch (IOException e12) {
                                            e12.printStackTrace();
                                        }
                                        out = null;
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    bin2 = bin;
                                    out = out2;
                                    if (bin2 != null) {
                                        try {
                                            bin2.close();
                                        } catch (IOException e13) {
                                            e13.printStackTrace();
                                        }
                                    }
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e14) {
                                            e14.printStackTrace();
                                        }
                                    }
                                    if (out != null) {
                                        throw th;
                                    }
                                    try {
                                        out.close();
                                    } catch (IOException e15) {
                                        e15.printStackTrace();
                                    }
                                    throw th;
                                }
                            }
                        } catch (FileNotFoundException e16) {
                            e = e16;
                            out = out2;
                            Trace.d(ImageDownloader.this.TAG, "Occured FileNotFoundException when download");
                            e.printStackTrace();
                            if (bin2 != null) {
                                bin2.close();
                                bin2 = null;
                            }
                            if (in != null) {
                                in.close();
                                in = null;
                            }
                            if (out != null) {
                                out.close();
                                out = null;
                            }
                            Trace.d(ImageDownloader.this.TAG, "end download() srcPath : " + srcPath + " destPath : " + destPath);
                        } catch (MalformedURLException e17) {
                            e = e17;
                            out = out2;
                            Trace.d(ImageDownloader.this.TAG, "Occured MalformedURLException when download");
                            e.printStackTrace();
                            if (bin2 != null) {
                                bin2.close();
                                bin2 = null;
                            }
                            if (in != null) {
                                in.close();
                                in = null;
                            }
                            if (out != null) {
                                out.close();
                                out = null;
                            }
                            Trace.d(ImageDownloader.this.TAG, "end download() srcPath : " + srcPath + " destPath : " + destPath);
                        } catch (IOException e18) {
                            e = e18;
                            out = out2;
                            Trace.d(ImageDownloader.this.TAG, "Occured IOException when download");
                            e.printStackTrace();
                            if (bin2 != null) {
                                bin2.close();
                                bin2 = null;
                            }
                            if (in != null) {
                                in.close();
                                in = null;
                            }
                            if (out != null) {
                                out.close();
                                out = null;
                            }
                            Trace.d(ImageDownloader.this.TAG, "end download() srcPath : " + srcPath + " destPath : " + destPath);
                        } catch (Throwable th2) {
                            th = th2;
                            out = out2;
                            if (bin2 != null) {
                                bin2.close();
                            }
                            if (in != null) {
                                in.close();
                            }
                            if (out != null) {
                                throw th;
                            }
                            out.close();
                            throw th;
                        }
                    } catch (Exception e19) {
                        Trace.d(ImageDownloader.this.TAG, "Occured Exception when openStream, try again URL openStream");
                        e19.printStackTrace();
                        try {
                            in = new URL(srcPath).openStream();
                        } catch (Exception e20) {
                            Trace.d(ImageDownloader.this.TAG, "Occured Exception again when openStream");
                            e19.printStackTrace();
                        }
                    }
                    bin = new BufferedInputStream(in, 8192);
                    out2.flush();
                    ExifInterface exif = new ExifInterface(destPath);
                    Trace.d(ImageDownloader.this.TAG, "System date : " + Calendar.getInstance().get(1) + "-" + (Calendar.getInstance().get(2) + 1) + "-" + Calendar.getInstance().get(5) + " exif date : " + exif.getAttribute("DateTime"));
                    if (bin != null) {
                        try {
                            bin.close();
                        } catch (IOException e21) {
                            e21.printStackTrace();
                        }
                        bin2 = null;
                    } else {
                        bin2 = bin;
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                        in = null;
                    }
                    if (out2 != null) {
                        try {
                            out2.close();
                        } catch (IOException e23) {
                            e23.printStackTrace();
                        }
                        out = null;
                    } else {
                        out = out2;
                    }
                } catch (FileNotFoundException e24) {
                    e = e24;
                } catch (MalformedURLException e25) {
                    e = e25;
                } catch (IOException e26) {
                    e = e26;
                }
                Trace.d(ImageDownloader.this.TAG, "end download() srcPath : " + srcPath + " destPath : " + destPath);
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }
}
