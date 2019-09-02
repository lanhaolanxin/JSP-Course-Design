package com.xt.www.service;

import com.xt.www.common.PageHelper.Page;
import com.xt.www.entity.UserContent;
public interface SolrService {
    /**
     * 根据关键子搜索文章并分页
     * @param keyword
     * @return
     * */
    Page<UserContent> findByKeyWords(String keyWords,Integer pageNum,Integer pageSize);

    void  addUserContent(UserContent userContent);

    void updateUserContent(UserContent userContent);

    void deleteById(Long id);

}
