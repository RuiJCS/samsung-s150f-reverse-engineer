package com.samsungimaging.connectionmanager.app.pullservice.datatype;

import com.samsungimaging.connectionmanager.app.pullservice.controller.DeviceController;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class DSCCamera {
    private static Trace.Tag TAG = Trace.Tag.RVF;
    private ArrayList<String> mDialModeMenuItems = new ArrayList<>();
    private ArrayList<DSCResolution> mResolutionMenuItems = new ArrayList<>();
    private ArrayList<DSCMenuItem> mResolutionMenuItemsDim = new ArrayList<>();
    private ArrayList<String> mFlashMenuItems = new ArrayList<>();
    private ArrayList<String> mLedTimeMenuItems = new ArrayList<>();
    private ArrayList<String> mFlashDisplayMenuItems = new ArrayList<>();
    private ArrayList<String> mStreamQualityMenuItems = new ArrayList<>();
    private ArrayList<DSCMenuItem> mStreamUrlMenuItems = new ArrayList<>();
    private ArrayList<String> mEVMenuItems = new ArrayList<>();
    private ArrayList<String> mISOMenuItems = new ArrayList<>();
    private ArrayList<String> mWBMenuItems = new ArrayList<>();
    private ArrayList<String> mMeteringMenuItems = new ArrayList<>();
    private ArrayList<String> mAFModeMenuItems = new ArrayList<>();
    private ArrayList<String> mAFAreaMenuItems = new ArrayList<>();
    private ArrayList<DSCMenuItem> mAFAreaMenuItemsDim = new ArrayList<>();
    private ArrayList<String> mDriveMenuItems = new ArrayList<>();
    private ArrayList<String> mShutterSpeedMenuItems = new ArrayList<>();
    private ArrayList<String> mApertureMenuItems = new ArrayList<>();
    private ArrayList<String> mQualityMenuItems = new ArrayList<>();
    private ArrayList<DSCMovieResolution> mMovieResolutionMenuItems = new ArrayList<>();
    private ArrayList<String> mFileSaveMenuItems = new ArrayList<>();
    private ArrayList<DSCMenuItem> mFileSaveMenuItemsDim = new ArrayList<>();
    private ArrayList<DSCResolution> mRatioOffsetMenuItems = new ArrayList<>();
    private ArrayList<String> mTouchAFMenuItems = new ArrayList<>();
    private ArrayList<DSCMenuItem> mTouchAFMenuItemsDim = new ArrayList<>();
    private boolean isConnected = false;
    private String mVersionPrefix = "";
    private String mVersion = "";
    private String mDialModeValue = "Auto";
    private String mDefaultResolutionIndex = "0";
    private String mDefaultFlash = "";
    private String mCurrentLedTimeMenuItem = "Off";
    private String mCurrentFlashDisplay = DeviceController.FlashMode.AUTO;
    private String mCurrentStreamQuality = "high";
    private String mDefaultFocusState = "mf";
    private String mDefaultZoom = "0";
    private String mMaxZoom = "0";
    private String mMinZoom = "0";
    private String mAvailShots = "";
    private String mRotation = "";
    private String mCurrentStreamUrl = "";
    private String mAFShotResult = "";
    private String mAFPriority = "0";
    private String mEVValue = "";
    private String mISOValue = "";
    private String mWBValue = "";
    private String mMeteringValue = "";
    private String mAFModeValue = "";
    private String mAFAreaValue = "MULTI AF";
    private String mDriveValue = "Single";
    private String mShutterSpeedValue = "";
    private String mApertureValue = "";
    private String mQualityValue = "";
    private String mSaveValue = "";
    private String mRatioValue = "";
    private String mMovieResolutionValue = "";
    private String mMovieResolutionRatio = "";
    private String mMultiAFMatrixSizeValue = "";
    private String mRemainRecTimeValue = "0";
    private String mFileSaveValue = "Camera";
    private String mMovieRecordTime = "0";
    private DSCResolution mRatioOffsetValue = null;
    private String mTouchAFValue = "Touch AF";
    private DSCPosition mTouchAFPosition = new DSCPosition(50, 50);
    private String mVideoOutValue = "NTSC";
    private boolean mIsTouchAFMovie = false;
    private boolean mIsResizeMode = false;
    private String mFlashStrobeStatus = "";
    private String mCardStatusValue = "External";

    public ArrayList<String> getDialModeMenuItems() {
        return this.mDialModeMenuItems;
    }

    public void setDialModeMenuItems(ArrayList<String> dialModeMenuItems) {
        this.mDialModeMenuItems = dialModeMenuItems;
    }

    public ArrayList<DSCResolution> getResolutionMenuItems() {
        return this.mResolutionMenuItems;
    }

    protected void setResolutionMenuItems(ArrayList<DSCResolution> resolutionMenuItems) {
        this.mResolutionMenuItems = resolutionMenuItems;
    }

    public ArrayList<DSCMenuItem> getResolutionMenuItemsDim() {
        return this.mResolutionMenuItemsDim;
    }

    public void setResolutionMenuItemsDim(ArrayList<DSCMenuItem> resolutionMenuItemsDim) {
        this.mResolutionMenuItemsDim = resolutionMenuItemsDim;
    }

    public ArrayList<String> getFlashMenuItems() {
        return this.mFlashMenuItems;
    }

    protected void setFlashMenuItems(ArrayList<String> flashMenuItems) {
        this.mFlashMenuItems = flashMenuItems;
    }

    public ArrayList<String> getLedTimeMenuItems() {
        return this.mLedTimeMenuItems;
    }

    public void setLedTimeMenuItems(ArrayList<String> ledTimeMenuItems) {
        this.mLedTimeMenuItems = ledTimeMenuItems;
    }

    public ArrayList<String> getFlashDisplayMenuItems() {
        return this.mFlashDisplayMenuItems;
    }

    protected void setFlashDisplayMenuItems(ArrayList<String> flashDisplayMenuItems) {
        this.mFlashDisplayMenuItems = flashDisplayMenuItems;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public String getVersionPrefix() {
        return this.mVersionPrefix;
    }

    public void setVersionPrefix(String versionPrefix) {
        this.mVersionPrefix = versionPrefix;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public void setVersion(String version) {
        this.mVersion = version;
    }

    public String getDialModeValue() {
        return this.mDialModeValue;
    }

    public void setDialModeValue(String dialModeValue) {
        this.mDialModeValue = dialModeValue;
    }

    public String getDefaultResolutionIndex() {
        return this.mDefaultResolutionIndex;
    }

    public void setDefaultResolutionIndex(String defaultResolutionIndex) {
        this.mDefaultResolutionIndex = defaultResolutionIndex;
    }

    public String getDefaultFlash() {
        return this.mDefaultFlash;
    }

    public int getDefaultFlashIndex() {
        for (int i = 0; i < getFlashMenuItems().size(); i++) {
            if (getFlashMenuItems().get(i).toUpperCase(Locale.ENGLISH).equals(getDefaultFlash().toUpperCase(Locale.ENGLISH))) {
                int index = i;
                return index;
            }
        }
        return 0;
    }

    public void setDefaultFlash(String defaultFlash) {
        this.mDefaultFlash = defaultFlash;
    }

    public String getCurrentFlashDisplay() {
        return this.mCurrentFlashDisplay;
    }

    public int getCurrentFlashDisplayIndex() {
        for (int i = 0; i < getFlashDisplayMenuItems().size(); i++) {
            if (getFlashDisplayMenuItems().get(i).equals(getCurrentFlashDisplay())) {
                int index = i;
                return index;
            }
        }
        return 0;
    }

    public void setCurrentFlashDisplay(String currentFlashDisplay) {
        this.mCurrentFlashDisplay = currentFlashDisplay;
    }

    public String getDefaultFocusState() {
        return this.mDefaultFocusState;
    }

    public String getCurrentStreamQuality() {
        return this.mCurrentStreamQuality;
    }

    public void setCurrentStreamQuality(String currentStreamQuality) {
        this.mCurrentStreamQuality = currentStreamQuality;
    }

    public void setDefaultFocusState(String defaultFocusState) {
        this.mDefaultFocusState = defaultFocusState;
    }

    public String getDefaultZoom() {
        return this.mDefaultZoom;
    }

    public void setDefaultZoom(String defaultZoom) {
        this.mDefaultZoom = defaultZoom;
    }

    public String getMaxZoom() {
        return this.mMaxZoom;
    }

    protected void setMaxZoom(String maxZoom) {
        this.mMaxZoom = maxZoom;
    }

    public String getMinZoom() {
        return this.mMinZoom;
    }

    public void setMinZoom(String minZoom) {
        this.mMinZoom = minZoom;
    }

    public String getAvailShots() {
        return this.mAvailShots;
    }

    public void setAvailShots(String availShots) {
        this.mAvailShots = availShots;
    }

    public String getRotation() {
        return this.mRotation;
    }

    protected void setRotation(String rotation) {
        this.mRotation = rotation;
    }

    public ArrayList<String> getStreamQualityMenuItems() {
        return this.mStreamQualityMenuItems;
    }

    protected void setStreamQualityMenuItems(ArrayList<String> streamQualityMenuItems) {
        this.mStreamQualityMenuItems = streamQualityMenuItems;
    }

    public ArrayList<DSCMenuItem> getStreamUrlMenuItems() {
        return this.mStreamUrlMenuItems;
    }

    protected void setStreamUrlMenuItems(ArrayList<DSCMenuItem> streamUrlMenuItems) {
        this.mStreamUrlMenuItems = streamUrlMenuItems;
    }

    public ArrayList<String> getEVMenuItems() {
        return this.mEVMenuItems;
    }

    public void setEVMenuItems(ArrayList<String> EVMenuItems) {
        this.mEVMenuItems = EVMenuItems;
    }

    public ArrayList<String> getISOMenuItems() {
        return this.mISOMenuItems;
    }

    public void setISOMenuItems(ArrayList<String> isoMenuItems) {
        this.mISOMenuItems = isoMenuItems;
    }

    public ArrayList<String> getWBMenuItems() {
        return this.mWBMenuItems;
    }

    public void setWBMenuItems(ArrayList<String> wbMenuItems) {
        this.mWBMenuItems = wbMenuItems;
    }

    public ArrayList<String> getMeteringMenuItems() {
        return this.mMeteringMenuItems;
    }

    public void setMeteringMenuItems(ArrayList<String> meteringMenuItems) {
        this.mMeteringMenuItems = meteringMenuItems;
    }

    public ArrayList<String> getAFModeMenuItems() {
        return this.mAFModeMenuItems;
    }

    public void setAFModeMenuItems(ArrayList<String> afModeMenuItems) {
        this.mAFModeMenuItems = afModeMenuItems;
    }

    public ArrayList<String> getAFAreaMenuItems() {
        return this.mAFAreaMenuItems;
    }

    public void setAFAreaMenuItems(ArrayList<String> afAreaMenuItems) {
        this.mAFAreaMenuItems = afAreaMenuItems;
    }

    public ArrayList<DSCMenuItem> getAFAreaMenuItemsDim() {
        return this.mAFAreaMenuItemsDim;
    }

    public void setAFAreaMenuItemsDim(ArrayList<DSCMenuItem> aFAreaMenuItemsDim) {
        this.mAFAreaMenuItemsDim = aFAreaMenuItemsDim;
    }

    public ArrayList<String> getDriveMenuItems() {
        return this.mDriveMenuItems;
    }

    public void setDriveMenuItems(ArrayList<String> driveMenuItems) {
        this.mDriveMenuItems = driveMenuItems;
    }

    public ArrayList<String> getShutterSpeedMenuItems() {
        return this.mShutterSpeedMenuItems;
    }

    public void setShutterSpeedMenuItems(ArrayList<String> shutterSpeedMenuItems) {
        this.mShutterSpeedMenuItems = shutterSpeedMenuItems;
    }

    public ArrayList<String> getApertureMenuItems() {
        return this.mApertureMenuItems;
    }

    public void setApertureMenuItems(ArrayList<String> apertureMenuItems) {
        this.mApertureMenuItems = apertureMenuItems;
    }

    public ArrayList<String> getQualityMenuItems() {
        return this.mQualityMenuItems;
    }

    public void setQualityMenuItems(ArrayList<String> qualityMenuItems) {
        this.mQualityMenuItems = qualityMenuItems;
    }

    public ArrayList<DSCMovieResolution> getMovieResolutionMenuItems() {
        return this.mMovieResolutionMenuItems;
    }

    public void setMovieResolutionMenuItems(ArrayList<DSCMovieResolution> movieResolutionMenuItems) {
        this.mMovieResolutionMenuItems = movieResolutionMenuItems;
    }

    public ArrayList<String> getFileSaveMenuItems() {
        return this.mFileSaveMenuItems;
    }

    public void setFileSaveMenuItems(ArrayList<String> fileSaveMenuItems) {
        this.mFileSaveMenuItems = fileSaveMenuItems;
    }

    public ArrayList<DSCMenuItem> getFileSaveMenuItemsDim() {
        return this.mFileSaveMenuItemsDim;
    }

    public void setFileSaveMenuItemsDim(ArrayList<DSCMenuItem> fileSaveMenuItemsDim) {
        this.mFileSaveMenuItemsDim = fileSaveMenuItemsDim;
    }

    public ArrayList<DSCResolution> getRatioOffsetMenuItems() {
        return this.mRatioOffsetMenuItems;
    }

    public void setRatioOffsetMenuItems(ArrayList<DSCResolution> ratioOffsetMenuItems) {
        this.mRatioOffsetMenuItems = ratioOffsetMenuItems;
    }

    public ArrayList<String> getTouchAFMenuItems() {
        return this.mTouchAFMenuItems;
    }

    public void setTouchAFMenuItems(ArrayList<String> touchAFMenuItems) {
        this.mTouchAFMenuItems = touchAFMenuItems;
    }

    public ArrayList<DSCMenuItem> getTouchAFMenuItemsDim() {
        return this.mTouchAFMenuItemsDim;
    }

    public void setTouchAFMenuItemsDim(ArrayList<DSCMenuItem> touchAFMenuItemsDim) {
        this.mTouchAFMenuItemsDim = touchAFMenuItemsDim;
    }

    public String getCurrentStreamUrl() {
        return this.mCurrentStreamUrl;
    }

    protected void setCurrentStreamUrl(String currentStreamUrl) {
        this.mCurrentStreamUrl = currentStreamUrl;
    }

    public String getCurrentLEDTimeMenuItem() {
        return this.mCurrentLedTimeMenuItem;
    }

    public void setCurrentLEDTimeMenuItem(String currentLedTimeMenuItem) {
        this.mCurrentLedTimeMenuItem = currentLedTimeMenuItem;
    }

    public String getAFShotResult() {
        return this.mAFShotResult;
    }

    public void setAFShotResult(String afShotResult) {
        this.mAFShotResult = afShotResult;
    }

    public String getAFPriority() {
        return this.mAFPriority;
    }

    public void setAFPriority(String afPriority) {
        this.mAFPriority = afPriority;
    }

    public String getEVValue() {
        return this.mEVValue;
    }

    public void setEVValue(String evValue) {
        this.mEVValue = evValue;
    }

    public String getISOValue() {
        return this.mISOValue;
    }

    public void setISOValue(String isoValue) {
        this.mISOValue = isoValue;
    }

    public String getWBValue() {
        return this.mWBValue;
    }

    public void setWBValue(String wbValue) {
        this.mWBValue = wbValue;
    }

    public String getMeteringValue() {
        return this.mMeteringValue;
    }

    public void setMeteringValue(String meteringValue) {
        this.mMeteringValue = meteringValue;
    }

    public String getAFModeValue() {
        return this.mAFModeValue;
    }

    public void setAFModeValue(String afModeValue) {
        this.mAFModeValue = afModeValue;
    }

    public String getAFAreaValue() {
        return this.mAFAreaValue;
    }

    public void setAFAreaValue(String afAreaValue) {
        this.mAFAreaValue = afAreaValue;
    }

    public String getDriveValue() {
        Trace.d(TAG, "start getDriveValue() mDriveValue : " + this.mDriveValue);
        return this.mDriveValue;
    }

    public void setDriveValue(String driveValue) {
        Trace.d(TAG, "start setDriveValue() driveValue : " + driveValue);
        this.mDriveValue = driveValue;
    }

    public String getShutterSpeedValue() {
        return this.mShutterSpeedValue;
    }

    public void setShutterSpeedValue(String shutterSpeedValue) {
        this.mShutterSpeedValue = shutterSpeedValue;
    }

    public String getApertureValue() {
        return this.mApertureValue;
    }

    public void setApertureValue(String apertureValue) {
        this.mApertureValue = apertureValue;
    }

    public String getQualityValue() {
        return this.mQualityValue;
    }

    public void setQualityValue(String qualityValue) {
        this.mQualityValue = qualityValue;
    }

    public String getSaveValue() {
        return this.mSaveValue;
    }

    public void setSaveValue(String saveValue) {
        this.mSaveValue = saveValue;
    }

    public String getRatioValue() {
        return this.mRatioValue;
    }

    public void setRatioValue(String ratioValue) {
        this.mRatioValue = ratioValue;
    }

    public String getMovieResolutionValue() {
        return this.mMovieResolutionValue;
    }

    public void setMovieResolutionValue(String movieResolutionValue) {
        this.mMovieResolutionValue = movieResolutionValue;
    }

    public String getMovieResolutionRatio() {
        return this.mMovieResolutionRatio;
    }

    public void setMovieResolutionRatio(String movieResolutionRatio) {
        this.mMovieResolutionRatio = movieResolutionRatio;
    }

    public String getMultiAFMatrixSizeValue() {
        return this.mMultiAFMatrixSizeValue;
    }

    public void setMultiAFMatrixSizeValue(String multiAFMatrixSizeValue) {
        this.mMultiAFMatrixSizeValue = multiAFMatrixSizeValue;
    }

    public String getRemainRecTimeValue() {
        return this.mRemainRecTimeValue;
    }

    public void setRemainRecTimeValue(String remainRecTimeValue) {
        this.mRemainRecTimeValue = remainRecTimeValue;
    }

    public String getFileSaveValue() {
        return this.mFileSaveValue;
    }

    public void setFileSaveValue(String fileSaveValue) {
        this.mFileSaveValue = fileSaveValue;
    }

    public String getMovieRecordTime() {
        return this.mMovieRecordTime;
    }

    public void setMovieRecordTime(String movieRecordTime) {
        this.mMovieRecordTime = movieRecordTime;
    }

    public DSCResolution getRatioOffsetValue() {
        return this.mRatioOffsetValue;
    }

    public void setRatioOffsetValue(DSCResolution ratioOffsetValue) {
        this.mRatioOffsetValue = ratioOffsetValue;
    }

    public String getTouchAFValue() {
        return this.mTouchAFValue;
    }

    public void setTouchAFValue(String touchAFValue) {
        this.mTouchAFValue = touchAFValue;
    }

    public DSCPosition getTouchAFPosition() {
        return this.mTouchAFPosition;
    }

    public void setTouchAFPosition(DSCPosition touchAFPosition) {
        this.mTouchAFPosition = touchAFPosition;
    }

    public String getVideoOutValue() {
        return this.mVideoOutValue;
    }

    public void setVideoOutValue(String videoOutValue) {
        this.mVideoOutValue = videoOutValue;
    }

    public void setTouchAFMovie(boolean state) {
        this.mIsTouchAFMovie = state;
    }

    public boolean isTouchAFMovie() {
        return this.mIsTouchAFMovie;
    }

    public void setResizeMode(boolean state) {
        this.mIsResizeMode = state;
    }

    public boolean isResizeMode() {
        return this.mIsResizeMode;
    }

    public void setFlashStrobeStatus(String status) {
        this.mFlashStrobeStatus = status;
    }

    public String getFlashStrobeStatus() {
        return this.mFlashStrobeStatus;
    }

    public String getCardStatusValue() {
        return this.mCardStatusValue;
    }

    public void setCardStatusValue(String cardStatusValue) {
        this.mCardStatusValue = cardStatusValue;
    }
}
