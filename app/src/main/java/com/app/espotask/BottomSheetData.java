package com.app.espotask;
public class BottomSheetData {
    private String title;
    private String noticeTitle;
    private String notice1;
    private String notice2;
    private String confirmButtonText;

    public BottomSheetData(String title, String noticeTitle, String notice1, String notice2, String confirmButtonText) {
        this.title = title;
        this.noticeTitle = noticeTitle;
        this.notice1 = notice1;
        this.notice2 = notice2;
        this.confirmButtonText = confirmButtonText;
    }

    public String getTitle() {
        return title;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public String getNotice1() {
        return notice1;
    }

    public String getNotice2() {
        return notice2;
    }

    public String getConfirmButtonText() {
        return confirmButtonText;
    }
}
