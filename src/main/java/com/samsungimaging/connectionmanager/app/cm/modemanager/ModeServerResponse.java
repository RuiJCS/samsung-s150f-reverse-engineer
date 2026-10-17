package com.samsungimaging.connectionmanager.app.cm.modemanager;

import android.os.SystemClock;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.service.CMService;
import com.samsungimaging.connectionmanager.util.Trace;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.cybergarage.http.HTTP;

/* JADX INFO: loaded from: classes.dex */
public class ModeServerResponse extends Thread {
    private Socket mClient;

    public ModeServerResponse(Socket sock) {
        this.mClient = null;
        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, construct.");
        this.mClient = sock;
    }

    /* JADX WARN: Code duplicated, block: B:104:0x0243  */
    /* JADX WARN: Code duplicated, block: B:107:0x0247  */
    /* JADX WARN: Code duplicated, block: B:110:0x0259 A[LOOP:3: B:105:0x0244->B:110:0x0259, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:168:0x01b1 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:170:0x0251 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:19:0x009b A[Catch: IOException -> 0x0271, TryCatch #1 {IOException -> 0x0271, blocks: (B:17:0x0096, B:19:0x009b, B:20:0x009e, B:22:0x00a4), top: B:151:0x0096 }] */
    /* JADX WARN: Code duplicated, block: B:22:0x00a4 A[Catch: IOException -> 0x0271, TRY_LEAVE, TryCatch #1 {IOException -> 0x0271, blocks: (B:17:0x0096, B:19:0x009b, B:20:0x009e, B:22:0x00a4), top: B:151:0x0096 }] */
    /* JADX WARN: Code duplicated, block: B:38:0x013a A[Catch: IOException -> 0x015e, TryCatch #6 {IOException -> 0x015e, blocks: (B:36:0x0135, B:38:0x013a, B:39:0x013d, B:41:0x0143), top: B:153:0x0135 }] */
    /* JADX WARN: Code duplicated, block: B:41:0x0143 A[Catch: IOException -> 0x015e, TRY_LEAVE, TryCatch #6 {IOException -> 0x015e, blocks: (B:36:0x0135, B:38:0x013a, B:39:0x013d, B:41:0x0143), top: B:153:0x0135 }] */
    /* JADX WARN: Code duplicated, block: B:44:0x014e  */
    /* JADX WARN: Code duplicated, block: B:48:0x0163  */
    /* JADX WARN: Code duplicated, block: B:64:0x01a3  */
    /* JADX WARN: Code duplicated, block: B:67:0x01a7  */
    /* JADX WARN: Code duplicated, block: B:70:0x01b9 A[LOOP:2: B:65:0x01a4->B:70:0x01b9, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:77:0x01d9 A[Catch: IOException -> 0x01fc, TryCatch #7 {IOException -> 0x01fc, blocks: (B:75:0x01d4, B:77:0x01d9, B:78:0x01dc, B:80:0x01e2), top: B:155:0x01d4 }] */
    /* JADX WARN: Code duplicated, block: B:80:0x01e2 A[Catch: IOException -> 0x01fc, TRY_LEAVE, TryCatch #7 {IOException -> 0x01fc, blocks: (B:75:0x01d4, B:77:0x01d9, B:78:0x01dc, B:80:0x01e2), top: B:155:0x01d4 }] */
    /* JADX WARN: Code duplicated, block: B:83:0x01ed  */
    /* JADX WARN: Code duplicated, block: B:88:0x0201  */
    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws Throwable {
        int time;
        int time2;
        String[] header_array;
        if (this.mClient == null) {
            return;
        }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        int requestType = 0;
        try {
            try {
                BufferedInputStream bis2 = new BufferedInputStream(this.mClient.getInputStream());
                try {
                    String str = "";
                    byte[] buf = new byte[356];
                    do {
                        int len = bis2.read(buf, 0, buf.length);
                        if (len == -1) {
                            break;
                        }
                        str = String.valueOf(str) + new String(buf, 0, len);
                        header_array = str.split("\r\n\r\n");
                        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, header parsing...str = " + str + ", header_array.length = " + header_array.length);
                    } while (header_array.length < 2);
                    Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, Request Received.");
                    requestType = checkRequest(str);
                    Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, preparing Response...");
                    StringBuffer sb = new StringBuffer();
                    if (requestType == 20) {
                        sb.append("HTTP/1.1 400 Bad Request").append(HTTP.CRLF).append("Content-Length: 0").append(HTTP.CRLF).append(HTTP.CRLF);
                    } else {
                        sb.append("HTTP/1.1 200 OK").append(HTTP.CRLF).append("Content-Length: 0").append(HTTP.CRLF).append(HTTP.CRLF);
                    }
                    BufferedOutputStream bos2 = new BufferedOutputStream(this.mClient.getOutputStream());
                    try {
                        bos2.write(sb.toString().getBytes());
                        bos2.flush();
                        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, Response flush done!, response = " + sb.toString());
                        if (bis2 != null) {
                            try {
                                bis2.close();
                                if (bos2 != null) {
                                    bos2.close();
                                }
                                if (this.mClient != null) {
                                    this.mClient.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (bos2 != null) {
                                bos2.close();
                            }
                            if (this.mClient != null) {
                                this.mClient.close();
                            }
                        }
                        if (requestType == 23) {
                            ModeServer.getInstance().performRunByebye();
                        } else if ((requestType == 1 || requestType == 2 || requestType == 3 || requestType == 4 || requestType == 5 || requestType == 6) && CMService.getInstance().isSubAppAlive()) {
                            Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, already sub service running..., ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", requestType = " + requestType);
                            if (requestType != CMService.ACTIVE_SERVICE) {
                                for (int time3 = 0; time3 < 6; time3++) {
                                    if (!CMService.getInstance().isSubAppAlive()) {
                                        ModeServer.getInstance().performRunSubApplication(requestType);
                                        break;
                                    }
                                    SystemClock.sleep(500L);
                                }
                            }
                        } else {
                            ModeServer.getInstance().performRunSubApplication(requestType);
                        }
                        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, DONE.");
                        bis = bis2;
                        bos = bos2;
                    } catch (Exception e2) {
                        e = e2;
                        bis = bis2;
                        bos = bos2;
                        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, Exception01");
                        e.printStackTrace();
                        if (bis != null) {
                            try {
                                bis.close();
                                if (bos != null) {
                                    bos.close();
                                }
                                if (this.mClient != null) {
                                    this.mClient.close();
                                }
                            } catch (IOException e3) {
                                e3.printStackTrace();
                                if (requestType == 23) {
                                    ModeServer.getInstance().performRunByebye();
                                } else if ((requestType != 1 || requestType == 2 || requestType == 3 || requestType == 4 || requestType == 5 || requestType == 6) && CMService.getInstance().isSubAppAlive()) {
                                    Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, already sub service running..., ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", requestType = " + requestType);
                                    if (requestType != CMService.ACTIVE_SERVICE) {
                                        for (time2 = 0; time2 < 6; time2++) {
                                            if (!CMService.getInstance().isSubAppAlive()) {
                                                ModeServer.getInstance().performRunSubApplication(requestType);
                                                break;
                                            }
                                            SystemClock.sleep(500L);
                                        }
                                    }
                                } else {
                                    ModeServer.getInstance().performRunSubApplication(requestType);
                                }
                                Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, DONE.");
                            }
                        } else {
                            if (bos != null) {
                                bos.close();
                            }
                            if (this.mClient != null) {
                                this.mClient.close();
                            }
                        }
                        if (requestType == 23) {
                            ModeServer.getInstance().performRunByebye();
                        } else if (requestType != 1) {
                            Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, already sub service running..., ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", requestType = " + requestType);
                            if (requestType != CMService.ACTIVE_SERVICE) {
                                while (time2 < 6) {
                                    if (!CMService.getInstance().isSubAppAlive()) {
                                        ModeServer.getInstance().performRunSubApplication(requestType);
                                        break;
                                    }
                                    SystemClock.sleep(500L);
                                }
                            }
                        } else {
                            Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, already sub service running..., ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", requestType = " + requestType);
                            if (requestType != CMService.ACTIVE_SERVICE) {
                                while (time2 < 6) {
                                    if (!CMService.getInstance().isSubAppAlive()) {
                                        ModeServer.getInstance().performRunSubApplication(requestType);
                                        break;
                                    }
                                    SystemClock.sleep(500L);
                                }
                            }
                        }
                        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, DONE.");
                    } catch (Throwable th) {
                        th = th;
                        bis = bis2;
                        bos = bos2;
                        if (bis != null) {
                            try {
                                bis.close();
                                if (bos != null) {
                                    bos.close();
                                }
                                if (this.mClient != null) {
                                    this.mClient.close();
                                }
                            } catch (IOException e4) {
                                e4.printStackTrace();
                                if (requestType == 23) {
                                    ModeServer.getInstance().performRunByebye();
                                } else if ((requestType != 1 || requestType == 2 || requestType == 3 || requestType == 4 || requestType == 5 || requestType == 6) && CMService.getInstance().isSubAppAlive()) {
                                    Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, already sub service running..., ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", requestType = " + requestType);
                                    if (requestType != CMService.ACTIVE_SERVICE) {
                                        for (time = 0; time < 6; time++) {
                                            if (!CMService.getInstance().isSubAppAlive()) {
                                                ModeServer.getInstance().performRunSubApplication(requestType);
                                                break;
                                            }
                                            SystemClock.sleep(500L);
                                        }
                                    }
                                } else {
                                    ModeServer.getInstance().performRunSubApplication(requestType);
                                }
                                Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, DONE.");
                                throw th;
                            }
                        } else {
                            if (bos != null) {
                                bos.close();
                            }
                            if (this.mClient != null) {
                                this.mClient.close();
                            }
                        }
                        if (requestType == 23) {
                            ModeServer.getInstance().performRunByebye();
                        } else if (requestType != 1) {
                            Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, already sub service running..., ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", requestType = " + requestType);
                            if (requestType != CMService.ACTIVE_SERVICE) {
                                while (time < 6) {
                                    if (!CMService.getInstance().isSubAppAlive()) {
                                        ModeServer.getInstance().performRunSubApplication(requestType);
                                        break;
                                    }
                                    SystemClock.sleep(500L);
                                }
                            }
                        } else {
                            Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, already sub service running..., ACTIVE_SERVICE = " + CMService.ACTIVE_SERVICE + ", requestType = " + requestType);
                            if (requestType != CMService.ACTIVE_SERVICE) {
                                while (time < 6) {
                                    if (!CMService.getInstance().isSubAppAlive()) {
                                        ModeServer.getInstance().performRunSubApplication(requestType);
                                        break;
                                    }
                                    SystemClock.sleep(500L);
                                }
                            }
                        }
                        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, DONE.");
                        throw th;
                    }
                } catch (Exception e5) {
                    e = e5;
                    bis = bis2;
                } catch (Throwable th2) {
                    th = th2;
                    bis = bis2;
                }
            } catch (Exception e6) {
                e = e6;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private int checkRequest(String request) {
        int requestType;
        if (request.toLowerCase().contains(CMConstants.MsgString.SAMSUNGMODESERVER)) {
            if (request.toLowerCase().contains(CMConstants.MsgString.MOBILELINK)) {
                requestType = 1;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.REMOTEVIEWFINDER)) {
                requestType = 2;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.AUTOSHARE)) {
                requestType = 3;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.SELECTIVEPUSH)) {
                requestType = 4;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.GROUPSHARE)) {
                requestType = 5;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.IDLE)) {
                requestType = 7;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.MOBILEBACKUP)) {
                requestType = 6;
            } else if (request.toLowerCase().contains(CMConstants.MsgString.BYEBYE)) {
                requestType = 23;
            } else {
                requestType = 20;
            }
        } else if (request.toLowerCase().contains(CMConstants.MsgString.BYEBYE)) {
            requestType = 23;
        } else {
            requestType = 20;
        }
        Trace.d(CMConstants.TAG_NAME, "ModeServerResponse, Current ACTIVE_SERVICE is " + CMService.ACTIVE_SERVICE + ", return request Type is " + requestType);
        return requestType;
    }
}
