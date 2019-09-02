import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
public class PagePojo {
    @Getter
    @Setter
    private int totalNum;//当前表中的条目总数
    @Getter
    @Setter
    private int currentPage;//当前页的位置
    @Getter
    @Setter
    private int totalPage;//总页数
    @Getter
    @Setter
    private int pageSize;//每页包含的记录数
    @Getter
    @Setter
    private int startIndex;//检索的开始位置
    @Getter
    @Setter
    private int totalSelect;//总共检索的树木

    public void count(int totalNum,int totalPage,int totalSelect,int pageSize,int startIndex,int currentPage){
        int totalPageNumTemp =this.totalNum/this.pageSize;
       int plus =  (this.totalNum%this.pageSize==0)? 0 :1;
       totalPageNumTemp+=plus;
       if (totalPageNumTemp<=0){
           totalPageNumTemp=1;
       }
       this.totalPage = totalPageNumTemp;
       if (this.totalPage<this.currentPage){
           this.currentPage = this.totalPage;
       }
       if (this.currentPage<1){
           this.currentPage = 1;
       }
       this.startIndex = this.currentPage*this.pageSize;
        this.totalSelect = this.pageSize;
    }

}
