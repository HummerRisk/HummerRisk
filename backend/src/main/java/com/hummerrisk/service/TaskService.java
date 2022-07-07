package com.hummerrisk.service;

import com.hummerrisk.base.domain.Favorite;
import com.hummerrisk.base.domain.FavoriteExample;
import com.hummerrisk.base.domain.Rule;
import com.hummerrisk.base.domain.RuleExample;
import com.hummerrisk.base.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskService {

    @Resource
    private FavoriteMapper favoriteMapper;

    public List<Favorite> listFavorites() {
        FavoriteExample example = new FavoriteExample();
        example.setOrderByClause("create_time desc");
        return favoriteMapper.selectByExample(example);
    }

}
