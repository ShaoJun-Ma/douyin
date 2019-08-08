package com.msj.douyin.pojo;

import javax.persistence.*;

@Table(name = "search_records")
public class SearchRecords {
    @Id
    private String id;

    /**
     * 搜索的内容
     */
    private String content;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取搜索的内容
     *
     * @return content - 搜索的内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置搜索的内容
     *
     * @param content 搜索的内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}