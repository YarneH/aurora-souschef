package com.aurora.souschefprocessor.task.sectiondivider;

public class ResultAndAlteredTextPair {
    private String mResult;
    private String mAlteredText;

    public ResultAndAlteredTextPair(String result, String alteredText) {
        this.mResult = result;
        this.mAlteredText = alteredText;
    }

    public String getResult() {
        return mResult;
    }

    public String getAlteredText() {
        return mAlteredText;
    }
}
