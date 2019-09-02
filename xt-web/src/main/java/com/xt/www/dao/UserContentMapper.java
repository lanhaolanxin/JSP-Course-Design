package com.xt.www.dao;


import com.xt.www.entity.UserContent;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserContentMapper extends Mapper<UserContent> {
    List<UserContent> findCategoryByUid(@Param("uid")long uid);

    /**
     *  插入文章并返回主键id 返回类型只是影响行数  id在UserContent对象中
     * @param userContent
     * @return
     */
    int inserContent(UserContent userContent);

    void updateCommentNum(@Param("id") Long id,@Param("commentNum") Integer commentNum);

    UserContent findUserContentByTitle(@Param("title") String title);

    List<UserContent> findAllNonPersonalContent();
    List<UserContent> findAllPersonalContent(@Param("uid")Long uid);
}
