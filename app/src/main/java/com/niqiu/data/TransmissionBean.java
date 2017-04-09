package com.niqiu.data;

import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by 毅东 on 2015/11/15.
 */
public class TransmissionBean {

    private User user;
    private int percent;
    private int fileSize;
    private int fileCount;
    public TextView fileText, percentText;
    public ProgressBar progressBar;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public TextView getFileText() {
        return fileText;
    }

    public void setFileText(TextView fileText) {
        this.fileText = fileText;
    }

    public TextView getPercentText() {
        return percentText;
    }

    public void setPercentText(TextView percentText) {
        this.percentText = percentText;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
