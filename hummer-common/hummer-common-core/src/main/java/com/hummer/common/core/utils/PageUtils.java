package com.hummer.common.core.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 分页工具类
 *
 * @author harris1943
 */
public class PageUtils extends PageHelper
{
    public static <T> Pager<T> setPageInfo(Page page, T obj) {
        try {
            Pager<T> pager = new Pager<>();
            pager.setListObject(obj);
            pager.setPageCount(page.getPages());
            pager.setItemCount(page.getTotal());
            return pager;
        } catch (Exception e) {
            throw new RuntimeException("Error saving current page number data！");
        }
    }
}
