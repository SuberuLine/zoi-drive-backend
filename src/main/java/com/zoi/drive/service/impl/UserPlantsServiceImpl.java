package com.zoi.drive.service.impl;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserPlants;
import com.zoi.drive.entity.vo.response.PlantsVO;
import com.zoi.drive.mapper.UserPlantsMapper;
import com.zoi.drive.service.IUserPlantsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2025-04-05
*/
@Service
public class UserPlantsServiceImpl extends ServiceImpl<UserPlantsMapper, UserPlants> implements IUserPlantsService {

    @Resource
    private UserPlantsMapper userPlantsMapper;

    @Override
    public Result<List<UserPlants>> getPlants() {
        return Result.success(userPlantsMapper.selectList(null));
    }
}
