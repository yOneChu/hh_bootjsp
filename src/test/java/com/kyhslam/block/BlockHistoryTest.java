package com.kyhslam.block;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.repository.BlockHistoryRepository;
import com.kyhslam.repository.MyBatisBlockRepository;
import com.kyhslam.service.BlockHistoryService;
import com.kyhslam.util.PLMBlockUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class BlockHistoryTest {

    @Autowired
    BlockHistoryService service;

    @Autowired
    BlockHistoryRepository blockHistoryRepository;

    @Autowired
    MyBatisBlockRepository myBatisBlockRepository;


    @Description("Block기준정보들 정리해서 테이블에 셋팅")
    @Commit
    @Test
    void save() {

        service.insertInit();
    }


    @Test
    void findBlockNo() {
        ArrayList<BlockHistoryDTO> findBlockDto = myBatisBlockRepository.findByBlockNo("B121A03");
        //System.out.println("findBlockDto = " + findBlockDto.toString());

        for(int i=0;i<findBlockDto.size();i++){
            System.out.println(findBlockDto.get(i).getBlockNo());
        }

    }

    @Description("전체조회")
    @Test
    void findAll() {
        List<BlockHistoryDTO> list = service.findAll();
        for(int i=0;i<list.size();i++){
            BlockHistoryDTO dto = list.get(i);
            System.out.println(dto.toString());
        }
    }


    @Description("금일날짜에 수정된 BlockNo History 찾기")
    @Test
    void findPLMBlockNo() {
        //현재날짜 구하기
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String todayValue = now.format(formatter);

        System.out.println("todayValue = " + todayValue);

        PLMBlockUtil.findByTodayBlockNo();
    }


    /**
     * 임시사용 데이터 구분자 "|"로 변경
     */
    @Test
    @Commit
    void findVersionAndUpdate() {
        //findByBlockNoVersion

        ArrayList<BlockHistoryDTO> list = blockHistoryRepository.findByBlockNoVersion("3");
        for(int k=0;k<list.size();k++){
            BlockHistoryDTO dto = list.get(k);
            //System.out.println("dto = " + dto);

            String blockNo = dto.getBlockNo();

            List<String> pickList = Arrays.stream(dto.getPick().split("-")).collect(Collectors.toList());
            List<String> pickNameList = Arrays.stream(dto.getPickName().split("-")).collect(Collectors.toList());
            List<String> qtyList = Arrays.stream(dto.getQty().split("-")).collect(Collectors.toList());
            List<String> cmtList = Arrays.stream(dto.getCmt().split("-")).collect(Collectors.toList());
            List<String> colorList = Arrays.stream(dto.getColor().split("-")).collect(Collectors.toList());

            //PICK
            String pick = "";
            for (int i = 0; i < pickList.size(); i++) {
                String temp = pickList.get(i);
                pick += temp + "|";
            }
            dto.setPick(pick);

            //PICKNAME
            String pickName = "";
            for (int i = 0; i < pickNameList.size(); i++) {
                String temp = pickNameList.get(i);
                pickName += temp + "|";
            }
            dto.setPickName(pickName);


            //QTY
            String qty = "";
            for (int i = 0; i < qtyList.size(); i++) {
                String temp = qtyList.get(i);
                qty += temp + "|";
            }
            dto.setQty(qty);

            //CMT
            String cmt = "";
            for (int i = 0; i < cmtList.size(); i++) {
                String temp = cmtList.get(i);
                cmt += temp + "|";
            }
            dto.setCmt(cmt);

            //COLOR
            String color = "";
            for (int i = 0; i < colorList.size(); i++) {
                String temp = colorList.get(i);
                color += temp + "|";
            }
            dto.setColor(color);



            blockHistoryRepository.updateBlockHistory(dto);

            System.out.println(blockNo + " :: pick = " + dto.getPick() + " ---- " + dto.getPickName());
        } // end for
        
        
    }


}
