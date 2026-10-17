package com.samsungimaging.connectionmanager.util;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public abstract class AsyncTask<Params, Progress, Result> {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$util$AsyncTask$Status = null;
    private static final int CORE_POOL_SIZE = 5;
    private static final int KEEP_ALIVE = 1;
    private static final String LOG_TAG = "AsyncTask";
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final int MESSAGE_POST_RESULT = 1;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() { // from class: com.samsungimaging.connectionmanager.util.AsyncTask.1
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + this.mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(10);
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 128, 1, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor(null);
    public static final Executor DUAL_THREAD_EXECUTOR = Executors.newFixedThreadPool(2, sThreadFactory);
    private static final InternalHandler sHandler = new InternalHandler(0 == true ? 1 : 0);
    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;
    private volatile Status mStatus = Status.PENDING;
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
    private final WorkerRunnable<Params, Result> mWorker = new WorkerRunnable<Params, Result>() { // from class: com.samsungimaging.connectionmanager.util.AsyncTask.2
        @Override // java.util.concurrent.Callable
        public Result call() throws Exception {
            AsyncTask.this.mTaskInvoked.set(true);
            Process.setThreadPriority(10);
            return (Result) AsyncTask.this.postResult(AsyncTask.this.doInBackground(this.mParams));
        }
    };
    private final FutureTask<Result> mFuture = new FutureTask<Result>(this.mWorker) { // from class: com.samsungimaging.connectionmanager.util.AsyncTask.3
        @Override // java.util.concurrent.FutureTask
        protected void done() {
            try {
                AsyncTask.this.postResultIfNotInvoked(get());
            } catch (InterruptedException e) {
                Log.w(AsyncTask.LOG_TAG, e);
            } catch (CancellationException e2) {
                AsyncTask.this.postResultIfNotInvoked(null);
            } catch (ExecutionException e3) {
                throw new RuntimeException("An error occured while executing doInBackground()", e3.getCause());
            }
        }
    };

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static Status[] valuesCustom() {
            Status[] statusArrValuesCustom = values();
            int length = statusArrValuesCustom.length;
            Status[] statusArr = new Status[length];
            System.arraycopy(statusArrValuesCustom, 0, statusArr, 0, length);
            return statusArr;
        }
    }

    protected abstract Result doInBackground(Params... paramsArr);

    static /* synthetic */ int[] $SWITCH_TABLE$com$samsungimaging$connectionmanager$util$AsyncTask$Status() {
        int[] iArr = $SWITCH_TABLE$com$samsungimaging$connectionmanager$util$AsyncTask$Status;
        if (iArr == null) {
            iArr = new int[Status.valuesCustom().length];
            try {
                iArr[Status.FINISHED.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Status.PENDING.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Status.RUNNING.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            $SWITCH_TABLE$com$samsungimaging$connectionmanager$util$AsyncTask$Status = iArr;
        }
        return iArr;
    }

    @TargetApi(11)
    private static class SerialExecutor implements Executor {
        Runnable mActive;
        final ArrayDeque<Runnable> mTasks;

        private SerialExecutor() {
            this.mTasks = new ArrayDeque<>();
        }

        /* synthetic */ SerialExecutor(SerialExecutor serialExecutor) {
            this();
        }

        @Override // java.util.concurrent.Executor
        public synchronized void execute(final Runnable r) {
            this.mTasks.offer(new Runnable() { // from class: com.samsungimaging.connectionmanager.util.AsyncTask.SerialExecutor.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        r.run();
                    } finally {
                        SerialExecutor.this.scheduleNext();
                    }
                }
            });
            if (this.mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            Runnable runnablePoll = this.mTasks.poll();
            this.mActive = runnablePoll;
            if (runnablePoll != null) {
                AsyncTask.THREAD_POOL_EXECUTOR.execute(this.mActive);
            }
        }
    }

    public static void init() {
        sHandler.getLooper();
    }

    public static void setDefaultExecutor(Executor exec) {
        sDefaultExecutor = exec;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postResultIfNotInvoked(Result result) {
        boolean wasTaskInvoked = this.mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Result postResult(Result result) {
        Message message = sHandler.obtainMessage(1, new AsyncTaskResult(this, result));
        message.sendToTarget();
        return result;
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... progressArr) {
    }

    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }

    public final boolean isCancelled() {
        return this.mCancelled.get();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        this.mCancelled.set(true);
        return this.mFuture.cancel(mayInterruptIfRunning);
    }

    public final Result get() throws ExecutionException, InterruptedException {
        return this.mFuture.get();
    }

    public final Result get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        return this.mFuture.get(timeout, unit);
    }

    public final AsyncTask<Params, Progress, Result> execute(Params... paramsArr) {
        return executeOnExecutor(sDefaultExecutor, paramsArr);
    }

    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... paramsArr) {
        if (this.mStatus != Status.PENDING) {
            switch ($SWITCH_TABLE$com$samsungimaging$connectionmanager$util$AsyncTask$Status()[this.mStatus.ordinal()]) {
                case 2:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case 3:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        }
        this.mStatus = Status.RUNNING;
        onPreExecute();
        this.mWorker.mParams = paramsArr;
        exec.execute(this.mFuture);
        return this;
    }

    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    protected final void publishProgress(Progress... progressArr) {
        if (!isCancelled()) {
            sHandler.obtainMessage(2, new AsyncTaskResult(this, progressArr)).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        this.mStatus = Status.FINISHED;
    }

    private static class InternalHandler extends Handler {
        private InternalHandler() {
        }

        /* synthetic */ InternalHandler(InternalHandler internalHandler) {
            this();
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
            switch (msg.what) {
                case 1:
                    result.mTask.finish(result.mData[0]);
                    break;
                case 2:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;

        private WorkerRunnable() {
        }

        /* synthetic */ WorkerRunnable(WorkerRunnable workerRunnable) {
            this();
        }
    }

    private static class AsyncTaskResult<Data> {
        final Data[] mData;
        final AsyncTask mTask;

        AsyncTaskResult(AsyncTask task, Data... dataArr) {
            this.mTask = task;
            this.mData = dataArr;
        }
    }
}
