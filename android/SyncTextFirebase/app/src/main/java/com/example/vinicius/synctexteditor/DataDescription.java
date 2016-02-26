package com.example.vinicius.synctexteditor;

public class DataDescription {
    private String fileName;
    private long fileSize;
    private String data64Encoded;

    @SuppressWarnings("unused")
    private DataDescription(){
    }

    DataDescription(String fileName, long fileSize, String data64Encoded) {
        this.fileName = fileName;
        this.data64Encoded = data64Encoded;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getData64Encoded() {
        return data64Encoded;
    }
}
