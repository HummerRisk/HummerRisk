package com.hummerrisk.service;

import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.domain.*;
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
        //云账号
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andPluginIdNotIn(PlatformUtils.getVulnPlugin());
        accountExample.setOrderByClause("create_time desc");
        List<Account> accounts = accountMapper.selectByExample(accountExample);
        dto.setCloudAccount(accounts);
        //漏洞
        AccountExample vulnExample = new AccountExample();
        vulnExample.createCriteria().andPluginIdIn(PlatformUtils.getVulnPlugin());
        vulnExample.setOrderByClause("create_time desc");
        List<Account> vluns = accountMapper.selectByExample(vulnExample);
        dto.setVulnAccount(vluns);
        //虚拟机
        ServerExample serverExample = new ServerExample();
        serverExample.setOrderByClause("create_time desc");
        List<Server> servers = serverMapper.selectByExample(serverExample);
        dto.setServerAccount(servers);
        //镜像
        ImageExample imageExample = new ImageExample();
        imageExample.setOrderByClause("create_time desc");
        List<Image> images = imageMapper.selectByExample(imageExample);
        dto.setImageAccount(images);
        //软件包
        PackageExample packageExample = new PackageExample();
        packageExample.setOrderByClause("create_time desc");
        List<Package> packages = packageMapper.selectByExample(packageExample);
        dto.setPackageAccount(packages);
        return dto;
    }

}
