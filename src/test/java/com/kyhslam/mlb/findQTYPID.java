package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.MLBCommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;

@SpringBootTest
@Transactional
public class findQTYPID {


    @Description("특정 월 부품 찾기")
    @Test
    void findQty() {

        StopWatch sw = new StopWatch();
        sw.start();

        ArrayList<PartInfoDTO> dataDTOList = new ArrayList<>();

        String year = "2025";
        String blockNo = "E321A";
        String qtyPID = "E321A_28";


        MLBCommonUtil.findDownLevelQtyPID(year, blockNo, qtyPID, dataDTOList);

       // ArrayList<PartInfoDTO> list = MLBCommonUtil.findPartWithYearBlockNo(year, blockNo);


        System.out.println("dataDTOList.size() = " + dataDTOList.size());

        /*for (int i = 0; i < dataDTOList.size(); i++) {
            PartInfoDTO dto = dataDTOList.get(i);
            System.out.println(dto.getParentPartNo() + " > " + dto.getPartNo() + " > " + dto.getCmt() + " :; " + dto.getQty());
        }*/


        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }

}
