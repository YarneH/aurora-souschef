package com.aurora.souschefprocessor.task.sectiondivider;

/**
 * A helper class for the SplitToMainSectionsTask, it is a dataclass that stores two strings:
 * {@link #mResult} = the detected result
 * {@link #mAlteredText} = the original text without the detected result
 */
class ResultAndAlteredTextPair {
    private String mResult;
    private String mAlteredText;

    ResultAndAlteredTextPair(String result, String alteredText) {
        this.mResult = result;
        this.mAlteredText = alteredText;
    }

    String getResult() {
        return mResult;
    }

    String getAlteredText() {
        return mAlteredText;
    }
}
