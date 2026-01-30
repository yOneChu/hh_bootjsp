package com.kyhslam.service;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.ProductPlanC;
import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.repository.PlanCRepository;
import com.kyhslam.util.searchListBasedOnCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanCService {

    private final PlanCRepository repository;

    @Transactional
    public void partSave(PartPlanC partPlanC) {
        repository.partSave(partPlanC);
    }

    @Transactional
    public void productSave(ProductPlanC productPlanC) {
        repository.productSave(productPlanC);
    }


    public List<PartPlanC> findAll() {
        List<PartPlanC> list = repository.findAll();
        return list;
    }


    public List<ProductPlanC> findProductAll() {
        List<ProductPlanC> list = repository.findProductAll();
        return list;
    }


    //원가절감실적조회
    public static ArrayList findSAPIF(String PARTNO, String start_date_day, String end_date_day) {

        //현재날짜 구하기
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        String g_l_code = "";
        String chk_detail = "";
        String chk_general = "";
        String EL_ATYP = "";
        String EL_ASPD_1 = "";
        String EL_ASPD_2 = "";
        String EL_AMAN_1 = "";
        String EL_AMAN_2 = "";
        String EL_ECSF = "";
        String EL_ETM = "";
        String EL_ECJJ_1 = "";
        String EL_ECJJ_2 = "";
        //String PARTNO = "";
        String BLOCKNO = "";
        String specList = "";

        specList = "";
        specList += "EL_ATYP\n";   // 기종
        specList += "ARKTX\n"; // 사양
        specList += "EL_ASPSC\n";  //생산거점
        specList += "EL_AMDLR\n"; //모듈러


        ArrayList data = new ArrayList();
        try {

            HashMap<String, HogiExportDTO> tempExportInfoMap = new HashMap<>();

            boolean isOnlyElv = false;
            boolean isGeneralElv = false;

            System.out.println("PARTNO = " + PARTNO);

            HashMap resultHM = searchListBasedOnCondition.searchListBasedOnCondition(g_l_code, EL_ATYP, EL_ASPD_1, EL_ASPD_2, EL_AMAN_1, EL_AMAN_2, EL_ECSF, EL_ETM, EL_ECJJ_1, EL_ECJJ_2, PARTNO, BLOCKNO, start_date_day, end_date_day, specList, isOnlyElv, isGeneralElv);
            data = (ArrayList) resultHM.get("data");
            System.out.println("data.size == " + data.size());

        } catch (Exception e) {

        } finally {

        }

        return data;
    }
}
