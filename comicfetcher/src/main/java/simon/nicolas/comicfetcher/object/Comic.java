package simon.nicolas.comicfetcher.object;

import java.io.Serializable;

/**
 * Simple Holder for the Comic data type
 */
public class Comic implements Serializable {
    private String              mDay;
    private String              mMonth;
    private String              mYear;

    private int                 mNumber;
    private String              mLink;
    private String              mNews;

    private String              mTitle;
    private String              mSafeTitle;

    private String              mTranscript;

    private String              mAlt;
    private String              mImageUrl;

    public void setDay(String day) {
        mDay = day;
    }

    public void setMonth(String month) {
        mMonth = month;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public void setNews(String news) {
        mNews = news;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSafeTitle(String safeTitle) {
        mSafeTitle = safeTitle;
    }

    public void setTranscript(String transcript) {
        mTranscript = transcript;
    }

    public void setAlt(String alt) {
        mAlt = alt;
    }

    public void setImageUrl(String url) {
        mImageUrl = url;
    }

    public String getDay() {
        return (mDay);
    }

    public String getMonth() {
        return (mMonth);
    }

    public String getYear() {
        return (mYear);
    }

    public int getNumber() {
        return (mNumber);
    }

    public String getLink() {
        return (mLink);
    }

    public String getNews() {
        return (mNews);
    }

    public String getTitle() {
        return (mTitle);
    }

    public String getSafeTitle() {
        return (mSafeTitle);
    }

    public String getTranscript() {
        return (mTranscript);
    }

    public String getAlt() {
        return (mAlt);
    }

    public String getImageUrl() {
        return (mImageUrl);
    }
}
