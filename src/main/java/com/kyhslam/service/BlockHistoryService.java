package com.kyhslam.service;


import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.repository.BlockHistoryRepository;
import com.kyhslam.util.PLMBlockUtil;
import com.kyhslam.util.SendMail;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("BlockHistoryService")
@RequiredArgsConstructor
public class BlockHistoryService {

    private final BlockHistoryRepository blockHistoryRepository;

    /**
     * 초기화
     * PLM BlockNo 기준정보 히스토리 저장
     */
    public void insertInit() {


        ArrayList<BlockHistoryDTO> list = new ArrayList<BlockHistoryDTO>();
        list = PLMBlockUtil.blockHistory_init(); //PLM에서 전체 조회

        System.out.println("------------ init ------------");
        for (int i = 0; i < list.size(); i++) {
            BlockHistoryDTO dto = list.get(i);
            blockHistoryRepository.saveBlockHistory(dto, "1");
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

    //findOneByBlockNo
    public ArrayList<BlockHistoryDTO> findOneByBlockNo(String blockNo) {
        ArrayList<BlockHistoryDTO> dto = blockHistoryRepository.findOneByBlockNo(blockNo);
        System.out.println("findOneByBlockNo : dto = " + dto);
        return dto;
    }

    /**
     * 전체조회
     * @return
     */
    public List<BlockHistoryDTO> findAll() {
        List<BlockHistoryDTO> result = blockHistoryRepository.findAll();
        //System.out.println("result = " + result);


        return result;
    }

    /**
     * PLM에서 변경된거 찾아서 기존 이력데이터와 비교
     * 월~금 오전 7시 10분
     */
    @Scheduled(cron = "0 59 07 * * 1-5")
    public void compareData() {

        ArrayList<BlockHistoryDTO> mailDataList = new ArrayList<>();

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

            //백업되있는 데이터 조회
            ArrayList<BlockHistoryDTO> existList = blockHistoryRepository.findByBlockNo(blockNo);
            BlockHistoryDTO existData = existList.get(0);


            //메일발송 위해 데이터 리스트에 저장
            mailDataList.add(existData);

            //버전업해서 DB저장
            String eVersion = existData.getVersion();
            int modVersion = Integer.parseInt(eVersion);
            blockHistoryRepository.saveBlockHistory(data, String.valueOf((modVersion + 1)));


           /*
            boolean compareFlag = false;

            if (existData != null && existList != null && existList.size() > 0) {

                String ePick = existData.getPick();
                String ePickName = existData.getPickName();
                String eCmt = existData.getCmt();
                String eQty = existData.getQty();
                String eColor = existData.getColor();
                String eVersion = existData.getVersion();
                int modVersion = Integer.parseInt(eVersion);

                if( !pick.equals(ePick) ){
                    compareFlag = true;
                }

                if( !pickName.equals(ePickName) ){
                    compareFlag = true;
                }

                if( !cmt.equals(eCmt) ){
                    compareFlag = true;
                }

                if( !qty.equals(eQty) ){
                    compareFlag = true;
                }

                if( !color.equals(eColor) ){
                    compareFlag = true;
                }

                if( compareFlag == true ){
                    //메일발송 위해 데이터 리스트에 저장
                    mailDataList.add(existData);

                    //버전업해서 DB저장
                    blockHistoryRepository.saveBlockHistory(data, String.valueOf((modVersion + 1)));
                }
            }*/


        } // end for

        if(mailDataList != null && mailDataList.size() > 0) {
            //메일발송
            SendMail.sendBlockHistory(mailDataList);
        }
    }

}
