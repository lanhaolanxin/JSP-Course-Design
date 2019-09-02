package com.xt.www.service;

import com.xt.www.common.PageHelper;
import com.xt.www.entity.Comment;
import com.xt.www.entity.UserContent;

import java.util.List;


public interface UserContentService {
    /**
     * 查询所有Content并分页
     * @param content
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageHelper.Page<UserContent> findAll(UserContent content, Integer pageNum, Integer pageSize);
    PageHelper.Page<UserContent> findAll(UserContent content, Comment comment, Integer pageNum, Integer pageSize);
    PageHelper.Page<UserContent> findAllByUpvote(UserContent content, Integer pageNum, Integer pageSize);


    int inserContent(UserContent userContent);

    /**
     * 添加文章
     * @param content
     */
    /**
     * 添加文章
     * @param content
     */
    int addContent(UserContent content);

    /**
     * 根据用户id查询文章集合
     * @param uid
     * @return
     */
    List<UserContent> findByUserId(Long uid);

    /**
     * 查询所有文章
     * @return
     */
    List<UserContent> findAll();

    /**
     * 根据文章id查找文章
     * @param id
     * @return
     */
    UserContent findById(long id);
    /**
     * 根据文章id更新文章
     * @param content
     * @return
     */
    void updateById(UserContent userContent);

    List<UserContent> findCategoryByUid(Long id);

    PageHelper.Page<UserContent> findByCategory(String category, Long uid, Integer pageNum, Integer pageSize);

    /**
     * 根据用户id查询所有文章私密并分页
     * @param uid
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageHelper.Page<UserContent> findPersonal(Long uid ,Integer pageNum, Integer pageSize);
    PageHelper.Page<UserContent> findAll(Integer pageNum, Integer pageSize);
    /**
     * 根据文章id删除文章
     * @param cid
     */
    void deleteById(Long cid);

    void UpdateContentNum(UserContent userContent);

   UserContent findByUserContentName(String name);
}
