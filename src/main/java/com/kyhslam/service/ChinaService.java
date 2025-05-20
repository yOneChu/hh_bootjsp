package com.kyhslam.service;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.ChinaCommonUtil;
import com.kyhslam.util.SendMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
@Slf4j
public class ChinaService {


    /**
     * 중국 당일 릴리즈 된 자재들 Save
     * 배치는 밤에 돌아서 법인자재(전날)와 PLM자재(당일) 비교해야 됨
     * 법인자재(전날) VS PLM자재(당일)
     */
    @Scheduled(cron = "0 55 6 * * 1-5")
    public void releasedPartsSave() {

        log.info("releasedPartsSave === Start " );
        System.out.println("releasedPartsSave === Start ");
        ArrayList<PartInfoDTO> result = new ArrayList<>();

        //1. 법인PDM에서 릴리즈 된 자재 조회
        result = ChinaCommonUtil.findReleasedParts();

        ArrayList<PartInfoDTO> modList = new ArrayList<>();
        //2. 법인PDM에서 뽑은 자재와 일치하는 자재에 PLM에 있는지 조회
        ChinaCommonUtil.matchingParts(result, modList);

        for(int i=0; i<modList.size(); i++){
            PartInfoDTO dto = modList.get(i);

            //PLM에 매칭되어있는거 찾아서 저장
            ChinaCommonUtil.chinaPartSave(dto);
        }

        //관련데이터 메일보내기
        if(modList != null && modList.size() > 0){
            SendMail.sendChinaPart(modList);
        }

        log.info("releasedPartsSave === End " );
        System.out.println("releasedPartsSave === End ");
    }
}
