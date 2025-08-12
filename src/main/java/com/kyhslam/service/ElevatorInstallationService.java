package com.kyhslam.service;

import com.kyhslam.dto.ELVInfoAPI;
import com.kyhslam.repository.mybatis.ElevatorInstallationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("ElevatorInstallationService")
@RequiredArgsConstructor
public class ElevatorInstallationService {

    private final ElevatorInstallationMapper elvMapper;

    public void save(HashMap<String, String> param) {
        elvMapper.saveElvInstall(param);
    }

}
