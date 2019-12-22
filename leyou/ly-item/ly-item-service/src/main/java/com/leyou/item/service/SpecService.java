package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.DTO.SpecGroupDTO;
import com.leyou.item.DTO.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecService {

    @Autowired
    private TbSpecGroupService specGroupService;

    /**
     * 根据分类id 查询 分组的集合
     * @param categoryId
     * @return
     */
    public List<SpecGroupDTO> findGroupListByCategoryId(Long categoryId) {
//        select * from tb_spec_group where cid = ?
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecGroup::getCid,categoryId);
        List<TbSpecGroup> tbSpecGroupList = specGroupService.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbSpecGroupList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSpecGroupList,SpecGroupDTO.class);
    }

    @Autowired
    private TbSpecParamService tbSpecParamService;
    /**
     * 根据条件查询 规格参数的名字
     * @param groupId
     * @return
     */
    public List<SpecParamDTO> findParamList(Long groupId,Long categoryId,Boolean searching) {
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();
        if(groupId != null){
            queryWrapper.lambda().eq(TbSpecParam::getGroupId,groupId);
        }
        if(categoryId != null){
            queryWrapper.lambda().eq(TbSpecParam::getCid,categoryId);
        }
        if(searching != null){
            queryWrapper.lambda().eq(TbSpecParam::getSearching,searching);
        }
        List<TbSpecParam> specParamList = tbSpecParamService.list(queryWrapper);
        if(CollectionUtils.isEmpty(specParamList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(specParamList,SpecParamDTO.class);
    }

    /**
     * 保存规格组
     * @param group
     * @return
     */
    public void saveGroup(TbSpecGroup group) {
        boolean b = specGroupService.save(group);
        if(!b){
           throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 查询规格参数组，及组内参数
     * @param cid 商品分类id
     * @return 规格组及组内参数
     */
    public List<SpecGroupDTO> findSpecsByCid(Long cid) {
//        查询 cid分类下 的所有分组信息集合
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecGroup::getCid,cid);
        List<TbSpecGroup> tbSpecGroupList = specGroupService.list(queryWrapper);
        List<SpecGroupDTO> specGroupDTOList = BeanHelper.copyWithCollection(tbSpecGroupList, SpecGroupDTO.class);
//      查询分类下 所有的规格参数
        QueryWrapper<TbSpecParam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(TbSpecParam::getCid,cid);
        List<TbSpecParam> tbSpecParamList = tbSpecParamService.list(queryWrapper1);
        List<SpecParamDTO> specParamDTOList = BeanHelper.copyWithCollection(tbSpecParamList, SpecParamDTO.class);
//      组成 map key- groupid value-规格参数结合
        Map<Long, List<SpecParamDTO>> paramsMap = specParamDTOList.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));
        for (SpecGroupDTO specGroupDTO : specGroupDTOList) {
            Long gid = specGroupDTO.getId();
            specGroupDTO.setParams(paramsMap.get(gid));
        }
        return specGroupDTOList;
    }


}
