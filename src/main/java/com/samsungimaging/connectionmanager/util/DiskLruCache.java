package com.samsungimaging.connectionmanager.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public final class DiskLruCache implements Closeable {
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    private static final int IO_BUFFER_SIZE = 8192;
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_TMP = "journal.tmp";
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final File directory;
    private final File journalFile;
    private final File journalFileTmp;
    private Writer journalWriter;
    private final long maxSize;
    private int redundantOpCount;
    private final int valueCount;
    private long size = 0;
    private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<>(0, 0.75f, true);
    private long nextSequenceNumber = 0;
    private final ExecutorService executorService = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    private final Callable<Void> cleanupCallable = new Callable<Void>() { // from class: com.samsungimaging.connectionmanager.util.DiskLruCache.1
        @Override // java.util.concurrent.Callable
        public Void call() throws Exception {
            synchronized (DiskLruCache.this) {
                if (DiskLruCache.this.journalWriter != null) {
                    DiskLruCache.this.trimToSize();
                    if (DiskLruCache.this.journalRebuildRequired()) {
                        DiskLruCache.this.rebuildJournal();
                        DiskLruCache.this.redundantOpCount = 0;
                    }
                }
            }
            return null;
        }
    };

    private static <T> T[] copyOfRange(T[] tArr, int i, int i2) {
        int length = tArr.length;
        if (i > i2) {
            throw new IllegalArgumentException();
        }
        if (i < 0 || i > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i3 = i2 - i;
        int iMin = Math.min(i3, length - i);
        T[] tArr2 = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), i3));
        System.arraycopy(tArr, i, tArr2, 0, iMin);
        return tArr2;
    }

    public static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            while (true) {
                int count = reader.read(buffer);
                if (count != -1) {
                    writer.write(buffer, 0, count);
                } else {
                    return writer.toString();
                }
            }
        } finally {
            reader.close();
        }
    }

    public static String readAsciiLine(InputStream in) throws IOException {
        StringBuilder result = new StringBuilder(80);
        while (true) {
            int c = in.read();
            if (c == -1) {
                throw new EOFException();
            }
            if (c != 10) {
                result.append((char) c);
            } else {
                int length = result.length();
                if (length > 0 && result.charAt(length - 1) == '\r') {
                    result.setLength(length - 1);
                }
                return result.toString();
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }

    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize) {
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TMP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
    }

    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize) throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        }
        DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        if (cache.journalFile.exists()) {
            try {
                cache.readJournal();
                cache.processJournal();
                cache.journalWriter = new BufferedWriter(new FileWriter(cache.journalFile, true), 8192);
                return cache;
            } catch (IOException e) {
                cache.delete();
            }
        }
        directory.mkdirs();
        DiskLruCache cache2 = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        cache2.rebuildJournal();
        return cache2;
    }

    private void readJournal() throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(this.journalFile), 8192);
        try {
            String magic = readAsciiLine(in);
            String version = readAsciiLine(in);
            String appVersionString = readAsciiLine(in);
            String valueCountString = readAsciiLine(in);
            String blank = readAsciiLine(in);
            if (!MAGIC.equals(magic) || !"1".equals(version) || !Integer.toString(this.appVersion).equals(appVersionString) || !Integer.toString(this.valueCount).equals(valueCountString) || !"".equals(blank)) {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
            }
            while (true) {
                try {
                    readJournalLine(readAsciiLine(in));
                } catch (EOFException e) {
                    closeQuietly(in);
                    return;
                }
            }
        } catch (Throwable th) {
            closeQuietly(in);
            throw th;
        }
    }

    private void readJournalLine(String str) throws IOException {
        Entry entry = null;
        byte b = 0;
        String[] strArrSplit = str.split(" ");
        if (strArrSplit.length < 2) {
            throw new IOException("unexpected journal line: " + str);
        }
        String str2 = strArrSplit[1];
        if (strArrSplit[0].equals(REMOVE) && strArrSplit.length == 2) {
            this.lruEntries.remove(str2);
            return;
        }
        Entry entry2 = this.lruEntries.get(str2);
        if (entry2 == null) {
            entry2 = new Entry(this, str2, entry);
            this.lruEntries.put(str2, entry2);
        }
        if (!strArrSplit[0].equals(CLEAN) || strArrSplit.length != this.valueCount + 2) {
            if (!strArrSplit[0].equals(DIRTY) || strArrSplit.length != 2) {
                if (!strArrSplit[0].equals(READ) || strArrSplit.length != 2) {
                    throw new IOException("unexpected journal line: " + str);
                }
                return;
            }
            entry2.currentEditor = new Editor(this, entry2, b == true ? 1 : 0);
            return;
        }
        entry2.readable = true;
        entry2.currentEditor = null;
        entry2.setLengths((String[]) copyOfRange(strArrSplit, 2, strArrSplit.length));
    }

    private void processJournal() throws IOException {
        deleteIfExists(this.journalFileTmp);
        Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            Entry entry = i.next();
            if (entry.currentEditor == null) {
                for (int t = 0; t < this.valueCount; t++) {
                    this.size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (int t2 = 0; t2 < this.valueCount; t2++) {
                    deleteIfExists(entry.getCleanFile(t2));
                    deleteIfExists(entry.getDirtyFile(t2));
                }
                i.remove();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        Writer writer = new BufferedWriter(new FileWriter(this.journalFileTmp), 8192);
        writer.write(MAGIC);
        writer.write("\n");
        writer.write("1");
        writer.write("\n");
        writer.write(Integer.toString(this.appVersion));
        writer.write("\n");
        writer.write(Integer.toString(this.valueCount));
        writer.write("\n");
        writer.write("\n");
        for (Entry entry : this.lruEntries.values()) {
            if (entry.currentEditor != null) {
                writer.write("DIRTY " + entry.key + '\n');
            } else {
                writer.write("CLEAN " + entry.key + entry.getLengths() + '\n');
            }
        }
        writer.close();
        this.journalFileTmp.renameTo(this.journalFile);
        this.journalWriter = new BufferedWriter(new FileWriter(this.journalFile, true), 8192);
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    public synchronized Snapshot get(String key) throws IOException {
        Snapshot snapshot = null;
        synchronized (this) {
            checkNotClosed();
            validateKey(key);
            Entry entry = this.lruEntries.get(key);
            if (entry != null && entry.readable) {
                InputStream[] ins = new InputStream[this.valueCount];
                for (int i = 0; i < this.valueCount; i++) {
                    try {
                        ins[i] = new FileInputStream(entry.getCleanFile(i));
                    } catch (FileNotFoundException e) {
                    }
                }
                this.redundantOpCount++;
                this.journalWriter.append((CharSequence) ("READ " + key + '\n'));
                if (journalRebuildRequired()) {
                    this.executorService.submit(this.cleanupCallable);
                }
                snapshot = new Snapshot(this, key, entry.sequenceNumber, ins, null);
            }
        }
        return snapshot;
    }

    public Editor edit(String key) throws IOException {
        return edit(key, -1L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
        Editor editor = null;
        synchronized (this) {
            checkNotClosed();
            validateKey(key);
            Entry entry = this.lruEntries.get(key);
            if (expectedSequenceNumber == -1 || (entry != null && entry.sequenceNumber == expectedSequenceNumber)) {
                if (entry == null) {
                    entry = new Entry(this, key, null);
                    this.lruEntries.put(key, entry);
                } else if (entry.currentEditor == null) {
                }
                editor = new Editor(this, entry, null);
                entry.currentEditor = editor;
                this.journalWriter.write("DIRTY " + key + '\n');
                this.journalWriter.flush();
            }
        }
        return editor;
    }

    public File getDirectory() {
        return this.directory;
    }

    public long maxSize() {
        return this.maxSize;
    }

    public synchronized long size() {
        return this.size;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void completeEdit(Editor editor, boolean success) throws IOException {
        Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }
        if (success && !entry.readable) {
            for (int i = 0; i < this.valueCount; i++) {
                if (!entry.getDirtyFile(i).exists()) {
                    editor.abort();
                    throw new IllegalStateException("edit didn't create file " + i);
                }
            }
        }
        for (int i2 = 0; i2 < this.valueCount; i2++) {
            File dirty = entry.getDirtyFile(i2);
            if (success) {
                if (dirty.exists()) {
                    File clean = entry.getCleanFile(i2);
                    dirty.renameTo(clean);
                    long oldLength = entry.lengths[i2];
                    long newLength = clean.length();
                    entry.lengths[i2] = newLength;
                    this.size = (this.size - oldLength) + newLength;
                }
            } else {
                deleteIfExists(dirty);
            }
        }
        this.redundantOpCount++;
        entry.currentEditor = null;
        if (!(entry.readable | success)) {
            this.lruEntries.remove(entry.key);
            this.journalWriter.write("REMOVE " + entry.key + '\n');
        } else {
            entry.readable = true;
            this.journalWriter.write("CLEAN " + entry.key + entry.getLengths() + '\n');
            if (success) {
                long j = this.nextSequenceNumber;
                this.nextSequenceNumber = 1 + j;
                entry.sequenceNumber = j;
            }
        }
        if (this.size > this.maxSize || journalRebuildRequired()) {
            this.executorService.submit(this.cleanupCallable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean journalRebuildRequired() {
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }

    public synchronized boolean remove(String key) throws IOException {
        boolean z;
        checkNotClosed();
        validateKey(key);
        Entry entry = this.lruEntries.get(key);
        if (entry == null || entry.currentEditor != null) {
            z = false;
        } else {
            for (int i = 0; i < this.valueCount; i++) {
                File file = entry.getCleanFile(i);
                if (!file.delete()) {
                    throw new IOException("failed to delete " + file);
                }
                this.size -= entry.lengths[i];
                entry.lengths[i] = 0;
            }
            this.redundantOpCount++;
            this.journalWriter.append((CharSequence) ("REMOVE " + key + '\n'));
            this.lruEntries.remove(key);
            if (journalRebuildRequired()) {
                this.executorService.submit(this.cleanupCallable);
            }
            z = true;
        }
        return z;
    }

    public boolean isClosed() {
        return this.journalWriter == null;
    }

    private void checkNotClosed() {
        if (this.journalWriter == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        checkNotClosed();
        trimToSize();
        this.journalWriter.flush();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        if (this.journalWriter != null) {
            for (Entry entry : new ArrayList(this.lruEntries.values())) {
                if (entry.currentEditor != null) {
                    entry.currentEditor.abort();
                }
            }
            trimToSize();
            this.journalWriter.close();
            this.journalWriter = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            Map.Entry<String, Entry> toEvict = this.lruEntries.entrySet().iterator().next();
            remove(toEvict.getKey());
        }
    }

    public void delete() throws IOException {
        close();
        deleteContents(this.directory);
    }

    private void validateKey(String key) {
        if (key.contains(" ") || key.contains("\n") || key.contains("\r")) {
            throw new IllegalArgumentException("keys must not contain spaces or newlines: \"" + key + "\"");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String inputStreamToString(InputStream in) throws IOException {
        return readFully(new InputStreamReader(in, UTF_8));
    }

    public final class Snapshot implements Closeable {
        private final InputStream[] ins;
        private final String key;
        private final long sequenceNumber;

        private Snapshot(String key, long sequenceNumber, InputStream[] ins) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.ins = ins;
        }

        /* synthetic */ Snapshot(DiskLruCache diskLruCache, String str, long j, InputStream[] inputStreamArr, Snapshot snapshot) {
            this(str, j, inputStreamArr);
        }

        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public InputStream getInputStream(int index) {
            return this.ins[index];
        }

        public String getString(int index) throws IOException {
            return DiskLruCache.inputStreamToString(getInputStream(index));
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            for (InputStream in : this.ins) {
                DiskLruCache.closeQuietly(in);
            }
        }
    }

    public final class Editor {
        private final Entry entry;
        private boolean hasErrors;

        private Editor(Entry entry) {
            this.entry = entry;
        }

        /* synthetic */ Editor(DiskLruCache diskLruCache, Entry entry, Editor editor) {
            this(entry);
        }

        public InputStream newInputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                }
                if (!this.entry.readable) {
                    return null;
                }
                return new FileInputStream(this.entry.getCleanFile(index));
            }
        }

        public String getString(int index) throws IOException {
            InputStream in = newInputStream(index);
            if (in != null) {
                return DiskLruCache.inputStreamToString(in);
            }
            return null;
        }

        public OutputStream newOutputStream(int index) throws IOException {
            FaultHidingOutputStream faultHidingOutputStream;
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                }
                faultHidingOutputStream = new FaultHidingOutputStream(this, new FileOutputStream(this.entry.getDirtyFile(index)), null);
            }
            return faultHidingOutputStream;
        }

        public void set(int index, String value) throws Throwable {
            Writer writer = null;
            try {
                Writer writer2 = new OutputStreamWriter(newOutputStream(index), DiskLruCache.UTF_8);
                try {
                    writer2.write(value);
                    DiskLruCache.closeQuietly(writer2);
                } catch (Throwable th) {
                    th = th;
                    writer = writer2;
                    DiskLruCache.closeQuietly(writer);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }

        public void commit() throws IOException {
            if (this.hasErrors) {
                DiskLruCache.this.completeEdit(this, false);
                DiskLruCache.this.remove(this.entry.key);
            } else {
                DiskLruCache.this.completeEdit(this, true);
            }
        }

        public void abort() throws IOException {
            DiskLruCache.this.completeEdit(this, false);
        }

        private class FaultHidingOutputStream extends FilterOutputStream {
            private FaultHidingOutputStream(OutputStream out) {
                super(out);
            }

            /* synthetic */ FaultHidingOutputStream(Editor editor, OutputStream outputStream, FaultHidingOutputStream faultHidingOutputStream) {
                this(outputStream);
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream
            public void write(int oneByte) {
                try {
                    this.out.write(oneByte);
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream
            public void write(byte[] buffer, int offset, int length) {
                try {
                    this.out.write(buffer, offset, length);
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
                try {
                    this.out.close();
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }

            @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
            public void flush() {
                try {
                    this.out.flush();
                } catch (IOException e) {
                    Editor.this.hasErrors = true;
                }
            }
        }
    }

    private final class Entry {
        private Editor currentEditor;
        private final String key;
        private final long[] lengths;
        private boolean readable;
        private long sequenceNumber;

        private Entry(String key) {
            this.key = key;
            this.lengths = new long[DiskLruCache.this.valueCount];
        }

        /* synthetic */ Entry(DiskLruCache diskLruCache, String str, Entry entry) {
            this(str);
        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            for (long size : this.lengths) {
                result.append(' ').append(size);
            }
            return result.toString();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLengths(String[] strings) throws IOException {
            if (strings.length != DiskLruCache.this.valueCount) {
                throw invalidLengths(strings);
            }
            for (int i = 0; i < strings.length; i++) {
                try {
                    this.lengths[i] = Long.parseLong(strings[i]);
                } catch (NumberFormatException e) {
                    throw invalidLengths(strings);
                }
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }

        public File getCleanFile(int i) {
            return new File(DiskLruCache.this.directory, String.valueOf(this.key) + "." + i);
        }

        public File getDirtyFile(int i) {
            return new File(DiskLruCache.this.directory, String.valueOf(this.key) + "." + i + ".tmp");
        }
    }
}
