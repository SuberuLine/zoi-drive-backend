package com.zoi.drive.service;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserPlants;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zoi.drive.entity.vo.response.PlantsVO;

import java.util.List;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2025-04-05
*/
public interface IUserPlantsService extends IService<UserPlants> {

    Result<List<UserPlants>> getPlants();
}
