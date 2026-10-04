package com.kyhslam.bomCal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 호기 BOM 계산 결과 조회 (SubaeManager.bomCalculate 의 조회 전용 독립 실행 버전)
 * DB 에 저장하지 않고, 원본이 PARTOFEBOM / VARIABLEPART_NEW 에 저장할 내용을 출력한다.
 *
 * 실행 예)
 *   java -cp "classes;ojdbc6.jar" dyna.bomCal.bomCalMain 204201L11
 *   java -cp "classes;ojdbc6.jar" dyna.bomCal.bomCalMain 204201L11 C,M
 *   java -cp "classes;ojdbc6.jar" dyna.bomCal.bomCalMain 204201L11 C,M,F,1,2,3 all
 *
 * 인자 : hogi [blockOptList(콤마구분, 기본 C,M,F,1,2,3)] [all]
 *   all : 제품에 이미 계산완료로 표시된 블록옵션도 계산한다. (원본은 제외)
 *
 * 승인(RLS) 버전 : 원본은 중단되지만, 조회 전용이므로 계산한다.
 *   - wip 가 없으면 승인된 최신 버전(id$last)의 공사정보/제품/층 정보를 사용한다.
 *   - 공사정보가 RLS 면 all 과 같이 계산완료 블록옵션도 계산한다.
 *
 * DB 접속 : PLMDBConnection.getConnection() (JAVA 방식 PID 중 일부는 CommonDBConnection 도 사용)
 *
 * 층/제품 연결 테이블 코드는 BomConsts 상수 (FLOOR_TABLE_CODE, ELVANDFLOOR_ASSO_TABLE_CODE, ELVANDPRODUCT_ASSO_TABLE_CODE)
 */
public class bomCalMain {

    public static final List<String> DEFAULT_OPT_LIST = Arrays.asList("C", "M", "F", "1", "2", "3");

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            //System.out.println("usage : bomCalMain <hogi> [blockOptList ex) C,M,F,1,2,3] [all]");
            //return;
        }

        String hogi = "TEST-632563"; //args[0].trim();
        List<String> optList = args.length > 1 && !args[1].trim().isEmpty() ? splitArg(args[1]) : DEFAULT_OPT_LIST;
        boolean ignoreCalculated = args.length > 2 && "all".equalsIgnoreCase(args[2].trim());

        long start = System.currentTimeMillis();
        BomCalResult result = bomCalculate(hogi, optList, ignoreCalculated);
        printResult(result);
        System.out.println("elapsed : " + (System.currentTimeMillis() - start) + "ms");
    }

    /** 호기의 wip 공사정보(없으면 승인된 최신 버전)로 BOM 계산 (조회 전용) */
    public static BomCalResult bomCalculate(String hogi, List<String> optList, boolean ignoreCalculated) throws Exception {
        try (BomDb db = BomDb.open()) {
            BomContext ctx = new BomContext(db, new BomPidRepository());

            String elvOuid = ctx.getSpecLoader().getConsOuid(hogi);
            if (elvOuid == null)
                throw new Exception("공사정보가 없습니다. hogi=" + hogi);

            return new BomCalManager(ctx, elvOuid).bomCalculate(optList, ignoreCalculated);
        }
    }

    /** 결과 콘솔 출력 (탭 구분) */
    public static void printResult(BomCalResult r) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("hogi             : " + r.getHogi());
        System.out.println("elvOuid          : " + r.getElvOuid() + " (status=" + r.getElvStatus() + ", floors=" + r.getFloorCount() + ")");
        System.out.println("productOuid      : " + r.getProductOuid());
        System.out.println("inputOptList     : " + r.getInputOptList());
        System.out.println("blockOptList4Calc: " + r.getBlockOptList4Calc());
        for (String note : r.getNotes())
            System.out.println("[NOTE] " + note);
        if (r.getCalcError() != null)
            System.out.println("[CALC-ERROR] (원본은 여기서 BOM 계산 실패) " + r.getCalcError());

        System.out.println();
        System.out.println("---- 1Level (PARTOFEBOM) : " + r.getLevel1List().size() + " rows, insert 대상 " + countInsert1(r) + " rows");
        System.out.println(String.join("\t", "ACTION", "SEQ", "PARTNO", "PARTNAME", "B_NO", "PICK", "QTY", "MBOM", "COLOR", "G_L_CODE", "SPEC", "PART_SIZE", "CMT"));
        for (BomCalResult.Level1Row row : r.getLevel1List()) {
            System.out.println(String.join("\t", nvl(row.action), nvl(row.seq), nvl(row.partNo), oneLine(row.partName), nvl(row.blockNo), nvl(row.pick),
                    nvl(row.qty), nvl(row.mBom), oneLine(row.color), nvl(row.glCode), nvl(row.spec), nvl(row.partSize), oneLine(row.cmt)));
        }

        System.out.println();
        System.out.println("---- 2Level (VARIABLEPART_NEW) : " + r.getLevel2List().size() + " rows, insert 대상 " + countInsert2(r) + " rows");
        System.out.println(String.join("\t", "ACTION", "ASSOOUID", "PARENT_PARTNO", "PARTNO", "PARTNAME", "B_NO", "QTY", "COLOR", "CMT"));
        for (BomCalResult.Level2Row row : r.getLevel2List()) {
            System.out.println(String.join("\t", nvl(row.action), nvl(row.assoOuid), nvl(row.parentPartNo), nvl(row.partNo), oneLine(row.partName), nvl(row.blockNo),
                    nvl(row.qty), oneLine(row.color), oneLine(row.cmt)));
        }

        System.out.println();
        System.out.println("---- PID ERROR : " + r.getPidErrors().size());
        for (String err : r.getPidErrors())
            System.out.println(err);
        System.out.println("==================================================");
    }

    private static long countInsert1(BomCalResult r) {
        return r.getLevel1List().stream().filter(o -> BomCalResult.ACTION_INSERT.equals(o.action)).count();
    }

    private static long countInsert2(BomCalResult r) {
        return r.getLevel2List().stream().filter(o -> BomCalResult.ACTION_INSERT.equals(o.action)).count();
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    /** 주석의 줄바꿈은 " / " 로 표시 */
    private static String oneLine(String s) {
        return s == null ? "" : s.replace("\r", "").replace("\n", " / ").replace("\t", " ");
    }

    private static List<String> splitArg(String arg) {
        List<String> list = new ArrayList<String>();
        for (String s : arg.split(",")) {
            if (!s.trim().isEmpty())
                list.add(s.trim().toUpperCase());
        }
        return list;
    }
}
