package com.kyhslam.service;


import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.repository.BlockHistoryRepository;
import com.kyhslam.util.PLMBlockUtil;
import com.kyhslam.util.PLMDBConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class BlockHistoryService {

    private final BlockHistoryRepository blockHistoryRepository;

    /**
     * 초기화
     * PLM BlockNo 기준정보 히스토리 저장
     */
    public void insertInit() {


        ArrayList<BlockHistoryDTO> list = new ArrayList<BlockHistoryDTO>();
        list = PLMBlockUtil.blockHistory_init();

        System.out.println("------------ init ------------");
        for (int i = 0; i < list.size(); i++) {
            BlockHistoryDTO dto = list.get(i);
            blockHistoryRepository.saveBlockHistory(dto);
        }
    }



    /**
     * BlockNo에 해당하는 데이터 조회
     * @param blockNo
     * @return
     */
    public ArrayList<BlockHistoryDTO> findByBlockNo(String blockNo) {
        ArrayList<BlockHistoryDTO> dto = blockHistoryRepository.findByBlockNo(blockNo);
        return dto;
    }

    /**
     * PLM에서 변경된거 찾아서 기존 이력데이터와 비교
     * 월~금 저녁 6시 20분
     */
    @Scheduled(cron = "0 20 18 * * 1-5")
    public void compareData() {

        //금일 변경된 PLM 데이터 조회
        ArrayList<BlockHistoryDTO> plmDataList = PLMBlockUtil.findByTodayBlockNo();

        for(int i=0; i < plmDataList.size();i++){
            BlockHistoryDTO data = plmDataList.get(i);
            String blockNo = data.getBlockNo();

            String pick = data.getPick();
            String pickName = data.getPickName();
            String cmt = data.getCmt();
            String qty = data.getQty();
            String color = data.getColor();

            ArrayList<BlockHistoryDTO> existList = blockHistoryRepository.findByBlockNo(blockNo);
            BlockHistoryDTO existData = existList.get(0);

            boolean compareFlag = false;

            if (existData != null && existList != null && existList.size() > 0) {

                String ePick = existData.getPick();
                String ePickName = existData.getPickName();
                String eCmt = existData.getCmt();
                String eQty = existData.getQty();
                String eColor = existData.getColor();

                if( !pick.equals(ePick) ){
                    compareFlag = true;
                }

                if( !ePickName.equals(ePickName) ){
                    compareFlag = true;
                }

                if( !eCmt.equals(eCmt) ){
                    compareFlag = true;
                }

                if( !eQty.equals(eQty) ){
                    compareFlag = true;
                }

                if( !eColor.equals(eColor) ){
                    compareFlag = true;
                }
                
                if( compareFlag == true ){
                    //변경사항이 있음. 메일 발송
                }
            }
        } // end for
        
        
        //전체 초기화 및 데이터 리셋

    }

}
