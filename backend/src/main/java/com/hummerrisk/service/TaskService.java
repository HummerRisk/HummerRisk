package com.hummerrisk.service;

import com.hummerrisk.base.domain.Account;
import com.hummerrisk.base.domain.AccountExample;
import com.hummerrisk.base.domain.Favorite;
import com.hummerrisk.base.domain.FavoriteExample;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.dto.AccountTreeDTO;
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
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private ServerMapper serverMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private PackageMapper packageMapper;

    public List<Favorite> listFavorites() {
        FavoriteExample example = new FavoriteExample();
        example.setOrderByClause("create_time desc");
        return favoriteMapper.selectByExample(example);
    }

    public AccountTreeDTO listAccounts() {
        AccountTreeDTO dto = new AccountTreeDTO();
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andPluginIdNotIn(PlatformUtils.getVulnPlugin());
        List<Account> accounts = accountMapper.selectByExample(accountExample);
        dto.setCloudAccount(accounts);
        return dto;
    }

}
