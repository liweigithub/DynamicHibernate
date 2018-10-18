package com.hyaroma.dao.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageBean<T> {
    private int  pageIndex;//索引
    private int totalPage;//总页数
    private int pageSize;//每页显示条数
    private int dataCount;// 总记录数
    private List<T> data;
    // 动态显示条
    private int start = 1;
    private int end = 10;

    private boolean nextPage;//是否有下一页
    private boolean prePage;//是否有上一页

    public PageBean(int pageIndex, int pageSize, int dataCount) {
        this.pageIndex = pageIndex;
        this.dataCount = dataCount;
        this.pageSize = pageSize;
        int currentPage = pageIndex;
        this.pageIndex = (pageIndex - 1) * pageSize;
        this.totalPage = (int) Math.ceil(dataCount * 1.0 / pageSize);

        //是否可以上一页下一页
        if (dataCount==0 || this.totalPage ==1){
            this.nextPage = false;
            this.prePage = false;
        }else if(currentPage<=1){
            this.prePage = false;
            this.nextPage = true;
        }else if(currentPage >= this.totalPage){
            this.prePage = true;
            this.nextPage = false;
        }else if(currentPage < this.totalPage){
            this.prePage = true;
            this.nextPage = true;
        }
        // 3 动态显示条
        // 3.1 初始化数据 -- 显示10个分页
        this.start = 1;
        this.end = 10;

        // 3.2 初始数据 ， totalPage = 4
        if (this.totalPage <= 10) {
            this.end = this.totalPage;
        } else {
            // totalPage = 22
            // 3.3 当前页 前4后5
            this.start = this.pageIndex - 4;
            this.end = this.pageIndex + 5;
            // * pageNum = 1
            if (this.start < 1) {
                this.start = 1;
                this.end = 10;
            }
            // * pageNum = 22
            if (this.end > this.totalPage) {
                this.end = this.totalPage;
                this.start = this.totalPage - 9;
            }
        }
    }
    public int getPageIndex() {
        if (this.pageIndex <= 0) {
            this.pageIndex = 1;
        }
        if (this.pageIndex >= this.totalPage) {
            this.pageIndex = this.totalPage;
        }
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getDataCount() {
        if (pageSize == Integer.MAX_VALUE){
            return data.size();
        }
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isNextPage() {

        return this.nextPage;
    }

    public boolean isPrePage() {
        return this.prePage;
    }

    /**
     * 用于在客户端不需要全部返回pagebean数据的时候使用，将新封装的dataModel数据以pagebean的返回形式返回
     * @param pageBean
     * @param dataModel
     * @param currentPage
     * @return
     */
    public static Map<String,Object> renderPageInfoData(PageBean pageBean, List<Map<String,Object>> dataModel, int  currentPage){
        int totalPage = pageBean.getTotalPage();//总页数
        int pageSize = pageBean.getPageSize();//每页显示条数
        int dataCount = pageBean.getDataCount();// 总记录数
        boolean nextPage = pageBean.isNextPage();//是否有下一页
        boolean prePage = pageBean.isPrePage();//是否有上一页
        int start =pageBean.getStart();
        int end =  pageBean.getEnd();
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("pageIndex",currentPage);//不是pageBean 里面的pageIndex
        data.put("totalPage",totalPage);
        data.put("pageSize",pageSize);
        data.put("dataCount",dataCount);
        data.put("nextPage",nextPage);
        data.put("prePage",prePage);
        data.put("start",start);
        data.put("end",end);
        data.put("list",dataModel);
        return data;
    }
    public static Map<String,Object> renderSimpleData(Object data){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("list",data);
        return map;
    }
    @Override
    public String toString() {
        return "PageBean{" +
                "pageIndex=" + pageIndex +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", dataCount=" + dataCount +
                ", data=" + data.toString() +
                ", start=" + start +
                ", end=" + end +
                ", nextPage=" + nextPage +
                ", prePage=" + prePage +
                '}';
    }
}
