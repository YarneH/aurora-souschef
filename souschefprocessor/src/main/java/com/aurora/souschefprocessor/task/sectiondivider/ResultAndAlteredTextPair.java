package com.aurora.souschefprocessor.task.sectiondivider;

/**
 * A helper class for the SplitToMainSectionsTask, it is a dataclass that stores two strings:
 * mResult = the detected result
 * mAlteredText = the original text without the detected result
 */
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
