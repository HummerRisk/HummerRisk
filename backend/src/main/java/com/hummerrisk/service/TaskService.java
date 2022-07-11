package com.hummerrisk.service;

import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtTaskMapper;
import com.hummerrisk.commons.constants.TaskEnum;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.commons.utils.SessionUtils;
import com.hummerrisk.commons.utils.UUIDUtil;
import com.hummerrisk.controller.request.task.*;
import com.hummerrisk.dto.AccountTreeDTO;
import com.hummerrisk.dto.TaskRuleDTO;
import com.hummerrisk.dto.TaskTagGroupDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedList;
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
    @Resource
    private ExtTaskMapper extTaskMapper;

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
        List<AccountVo> accounts = extTaskMapper.selectAccountByExample(accountExample);
        dto.setCloudAccount(accounts);
        //漏洞
        AccountExample vulnExample = new AccountExample();
        vulnExample.createCriteria().andPluginIdIn(PlatformUtils.getVulnPlugin());
        vulnExample.setOrderByClause("create_time desc");
        List<AccountVo> vluns = extTaskMapper.selectVulnByExample(vulnExample);
        dto.setVulnAccount(vluns);
        //虚拟机
        ServerExample serverExample = new ServerExample();
        serverExample.setOrderByClause("create_time desc");
        List<ServerVo> servers = extTaskMapper.selectServerByExample(serverExample);
        dto.setServerAccount(servers);
        //镜像
        ImageExample imageExample = new ImageExample();
        imageExample.setOrderByClause("create_time desc");
        List<ImageVo> images = extTaskMapper.selectImageByExample(imageExample);
        dto.setImageAccount(images);
        //软件包
        PackageExample packageExample = new PackageExample();
        packageExample.setOrderByClause("create_time desc");
        List<PackageVo> packages = extTaskMapper.selectPackageByExample(packageExample);
        dto.setPackageAccount(packages);
        return dto;
    }

    public Favorite addOrDelFavorite(Favorite favorite) {
        FavoriteExample example = new FavoriteExample();
        example.createCriteria().andSourceIdEqualTo(favorite.getId());
        List<Favorite> list = favoriteMapper.selectByExample(example);
        if(list.size() > 0) {
            favoriteMapper.deleteByExample(example);
        } else {
            favorite.setSourceId(favorite.getId());
            favorite.setId(UUIDUtil.newUUID());
            favorite.setCreateTime(System.currentTimeMillis());
            favorite.setUpdateTime(System.currentTimeMillis());
            favorite.setCreator(SessionUtils.getUserId());
            favorite.setCreatorName(SessionUtils.getUser().getName());
            favoriteMapper.insertSelective(favorite);
        }
        return favorite;
    }

    public void deleteFavorite(String id) {
        favoriteMapper.deleteByPrimaryKey(id);
    }

    public List<RuleVo> allList(RuleVo ruleVo) {
        List<RuleVo> allList = new LinkedList<>();
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            allList = extTaskMapper.cloudRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.vulnAccount.getType())) {
            allList = extTaskMapper.vulnRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            allList = extTaskMapper.serverRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            allList = extTaskMapper.imageRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.packageAccount.getType())) {
            allList = extTaskMapper.packageRuleList(ruleVo);
        }
        if(ruleVo.getAccountType()!=null) allList.addAll(extTaskMapper.ruleTagList(ruleVo));
        if(ruleVo.getAccountType()!=null && StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType()))
            allList.addAll(extTaskMapper.ruleGroupList(ruleVo));
        return allList;
    }

    public List<RuleVo> ruleList(RuleVo ruleVo) {
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            return extTaskMapper.cloudRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.vulnAccount.getType())) {
            return extTaskMapper.vulnRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            return extTaskMapper.serverRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            return extTaskMapper.imageRuleList(ruleVo);
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.packageAccount.getType())) {
            return extTaskMapper.packageRuleList(ruleVo);
        }
        return new LinkedList<>();
    }

    public List<RuleVo> ruleTagList(RuleVo ruleVo) {
        if(ruleVo.getAccountType()!=null)
            return extTaskMapper.ruleTagList(ruleVo);
        return new LinkedList<>();
    }

    public List<RuleVo> ruleGroupList(RuleVo ruleVo) {
        if(ruleVo.getAccountType()!=null && StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType()))
            return extTaskMapper.ruleGroupList(ruleVo);
        return new LinkedList<>();
    }

    public TaskRuleDTO detailRule(RuleVo ruleVo) {
        TaskRuleDTO ruleDTO = new TaskRuleDTO();
        if (StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.cloudAccount.getType())) {
            ruleDTO.setRuleDTO(extTaskMapper.cloudDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.vulnAccount.getType())) {
            ruleDTO.setRuleDTO(extTaskMapper.vulnDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.serverAccount.getType())) {
            ruleDTO.setServerRuleDTO(extTaskMapper.serverDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.imageAccount.getType())) {
            ruleDTO.setImageRuleDTO(extTaskMapper.imageDetailRule(ruleVo));
        } else if(StringUtils.equalsIgnoreCase(ruleVo.getAccountType(), TaskEnum.packageAccount.getType())) {
            ruleDTO.setPackageRuleDTO(extTaskMapper.packageDetailRule(ruleVo));
        }
        return ruleDTO;
    }

    public List<TaskTagGroupDTO> detailTag(RuleVo ruleVo) {
        return extTaskMapper.detailTag(ruleVo);
    }

    public List<TaskTagGroupDTO> detailGroup(RuleVo ruleVo) {
        return extTaskMapper.detailGroup(ruleVo);
    }

}
