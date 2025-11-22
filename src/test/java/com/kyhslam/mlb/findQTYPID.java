package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.MLBCommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@SpringBootTest
@Transactional
public class findQTYPID {


    @Description("특정 월 부품 찾기")
    @Test
    void findQty() {

        ArrayList<PartInfoDTO> dataDTOList = new ArrayList<>();

        String year = "2025";
        String blockNo = "E321A";
        String qtyPID = "E321A_28";
        
        ArrayList<PartInfoDTO> list = MLBCommonUtil.findPartWithYearBlockNo(year, blockNo);


        //E321A_28

        System.out.println("list.size() = " + list.size());
        for (int i = 0; i < 100; i++) {
            PartInfoDTO parentDto = list.get(i);
            String parentOID = parentDto.getOid();

            //System.out.println(parentDto.getPartNo() + " - " + parentDto.getOid());

            MLBCommonUtil.findDownLevelQTY(parentDto, qtyPID, dataDTOList);
        }


        for (int i = 0; i < dataDTOList.size(); i++) {
            PartInfoDTO dto = dataDTOList.get(i);
            String parentOID = dto.getOid();
            System.out.println(dto.getParentPartNo() + " > " + dto.getPartNo() + " > " + dto.getCmt() + " :; " + dto.getQty());

        }

    }

}
