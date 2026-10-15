package com.kyhslam.util.wonga;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 원가절감 실적조회 (ListOfBasedOnDrwNumber) 조회 로직.
 * dyna.plmetc.product.SearchListOfBasedCondition 을 WAS(Spring DataSource, SvServer) 없이
 * PLMDBConnection 으로 실행할 수 있도록 옮긴 클래스.
 */
public class DrwNumberSearchService implements Callable<DrwNumberSearchResult> {

    public static final int TIMEOUT_SECONDS = 300; // 제한 시간 300초

    private final DrwNumberSearchCondition cond;

    /**
     * 조회조건 검증/전처리 후 제한 시간(300초) 내에서 조회한다. (_mid.jsp + _right.jsp)
     *
     * @throws IllegalArgumentException 조회조건 오류
     * @throws TimeoutException         제한 시간 초과
     */
    public static DrwNumberSearchResult search(DrwNumberSearchCondition cond) throws Exception {

        cond.validate();

        DrwNumberSearchCondition normalized = cond.normalize();
        System.out.println("ListOfBasedOnDrwNumber - " + normalized);

        ExecutorService es = Executors.newSingleThreadExecutor();
        try {
            Future<DrwNumberSearchResult> future = es.submit(new DrwNumberSearchService(normalized));
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw (e.getCause() instanceof Exception) ? (Exception) e.getCause() : e;
        } finally {
            es.shutdownNow();
        }
    }

    /**
     * @param cond normalize() 된 조회조건
     */
    public DrwNumberSearchService(DrwNumberSearchCondition cond) {
        this.cond = cond;
    }

    @Override
    public DrwNumberSearchResult call() throws Exception {

        if (cond.chkBom) // 현장 BOM 추출 클릭 시
        {
            return searchBomOnly();
        }
        else if (!cond.chkDetail && !cond.isOnlyElv()
                && DrwNumberSearchCondition.isEmpty(cond.partNo)
                && DrwNumberSearchCondition.isEmpty(cond.blockNo))
        {
            return searchByGLCode();
        }
        else
        {
            return searchByCondition();
        }
    }

    /**
     * 도면번호(G_L_CODE) 만으로 조회 - SearchListOfBasedCondition#searchListBasedOnCondition(4 params)
     */
    private DrwNumberSearchResult searchByGLCode() throws Exception {

        System.out.println("### DrwNumberSearchService : search by G_L_CODE...");

        StringBuilder sql = new StringBuilder();
        sql.append(" select * from( ");
        sql.append(" select crdat ERP전송일자,woknum 공사번호, decode(matnr,woknum,'*', matnr) 상위자재, idnrk 자재번호,menge 수량,dzeinr 도면번호 from ");
        sql.append(" ( ");
        sql.append(" select a.*,row_number() over(partition by woknum,matnr,idnrk,matkl order by nvl(a.wokver,-1) desc, a.crdat desc, a.seqno desc) rn from eai_bom a, ");
        sql.append(" ( ");
        sql.append(" select woknum first_woknum,matnr first_matnr, idnrk first_idnrk,matkl first_matkl from eai_bom frist ");
        sql.append(" where dzeinr like ?||'%' and crdat between ? and ? ");
        sql.append(" and prctyp in('A','N') and tp_status = 'T' and woknum not like 'TEST-%' ");
        if (cond.chkGeneral)
            sql.append(" and woknum like '______L__' ");
        sql.append(" ) s ");
        sql.append(" where a.woknum = s.first_woknum ");
        sql.append(" and   a.matnr = s.first_matnr ");
        sql.append(" and   a.idnrk = s.first_idnrk ");
        sql.append(" and   a.matkl = s.first_matkl ");
        sql.append(" and   (A.CTYP is null or a.ctyp <> 'T') ");
        sql.append(" and   a.tp_status='T' ");
        sql.append(" ) PDM where rn=1 ");
        sql.append(" and prctyp<>'D' ");
        sql.append(" and tp_status='T' ");
        sql.append(" union ");
        sql.append(" select crdat ERP전송일자,woknum 공사번호, decode(matnr,woknum,'*', matnr) 상위자재, idnrk 자재번호,menge 수량,dzeinr 도면번호 from ");
        sql.append(" ( ");
        sql.append(" select a.*,row_number() over(partition by woknum,matnr,idnrk,matkl order by nvl(a.wokver,-1) desc, a.crdat desc, a.seqno desc) rn from eai_bom_body a, ");
        sql.append(" ( ");
        sql.append(" select woknum first_woknum,matnr first_matnr, idnrk first_idnrk,matkl first_matkl from eai_bom_body frist ");
        sql.append(" where dzeinr like ?||'%' and crdat between ? and ? ");
        sql.append(" and prctyp in('A','N')  and woknum not like 'TEST-%' ");
        if (cond.chkGeneral)
            sql.append(" and woknum like '______L__' ");
        sql.append(" ) s ");
        sql.append(" where a.woknum = s.first_woknum ");
        sql.append(" and   a.matnr = s.first_matnr ");
        sql.append(" and   a.idnrk = s.first_idnrk ");
        sql.append(" and   a.matkl = s.first_matkl ");
        sql.append(" and   (A.CTYP is null or a.ctyp <> 'T') ");
        sql.append(" ) PLM where rn=1 ");
        sql.append(" and prctyp<>'D') order by ERP전송일자 ");
        sql.append(" FETCH NEXT " + DrwNumberSearchResult.MAX_ROWS + " ROWS ONLY ");

        Connection con = null;
        PreparedStatement stat = null;
        try {
            con = PLMDBConnection.getConnection();
            stat = con.prepareStatement(sql.toString());
            stat.setString(1, cond.gLCode);
            stat.setString(2, cond.startDate);
            stat.setString(3, cond.endDate);
            stat.setString(4, cond.gLCode);
            stat.setString(5, cond.startDate);
            stat.setString(6, cond.endDate);
            return execute(stat);
        } finally {
            PLMDBConnection.disconnect(con, stat, null);
        }
    }

    /**
     * 사양/자재번호/블럭번호 조건 조회 - SearchListOfBasedCondition#searchListBasedOnCondition(17 params)
     */
    private DrwNumberSearchResult searchByCondition() throws Exception {

        System.out.println("### DrwNumberSearchService : search by condition...");

        Connection con = null;
        PreparedStatement stat = null;
        try {
            con = PLMDBConnection.getConnection();

            String specQuery = SpecQueryBuilder.getSpecQuery(con, SpecQueryBuilder.ELV_PRODUCT_CLS, cond.specList);

            StringBuilder sql = new StringBuilder();
            appendConditionQuery(sql, specQuery, "eai_bom", true);
            sql.append(" union ");
            appendConditionQuery(sql, specQuery, "eai_bom_body", false);
            sql.append(" FETCH NEXT " + DrwNumberSearchResult.MAX_ROWS + " ROWS ONLY ");

            System.out.println(sql.toString());

            stat = con.prepareStatement(sql.toString());
            return execute(stat);
        } finally {
            PLMDBConnection.disconnect(con, stat, null);
        }
    }

    /**
     * eai_bom / eai_bom_body 각각에 대한 조건 조회 구문.
     * eai_bom 은 tp_status = 'T' 조건이 추가로 붙는다.
     */
    private void appendConditionQuery(StringBuilder sql, String specQuery, String bomTable, boolean checkTpStatus) {

        sql.append(" select ");
        sql.append(" crdat ERP전송일자, ");
        sql.append(" woknum 공사번호, ");
        sql.append(" decode(bom.matnr,bom.woknum,'*', bom.matnr) 상위자재, ");
        sql.append(" idnrk 자재번호, ");
        sql.append(" menge 수량, ");
        sql.append(" dzeinr 도면번호, ");
        sql.append(" matkl 블럭번호, ");
        sql.append(" md$desc 공사명, ");
        sql.append(specQuery);
        sql.append(" c.manager_m 기계담당자, ");
        sql.append(" c.manager_e 전기담당자");
        sql.append(" from ( ");
        sql.append(" select crdat,woknum,matnr, idnrk,menge,dzeinr,matkl from ");
        sql.append(" ( ");
        sql.append(" select /*+ INDEX_COMBINE(A) */ a.*,row_number() over(partition by woknum,matnr,idnrk,matkl order by nvl(a.wokver,-1) desc, a.crdat desc, a.seqno desc) rn from " + bomTable + " a, ");
        sql.append(" (select woknum first_woknum,matnr first_matnr, idnrk first_idnrk,matkl first_matkl ");
        sql.append(" from " + bomTable + " first   ");
        sql.append(" where ");

        if (!DrwNumberSearchCondition.isEmpty(cond.partNo)) {
            sql.append(" ( ");
            appendPartNoCondition(sql, cond.partNo);
            sql.append(" ) and ");
        }

        if (!DrwNumberSearchCondition.isEmpty(cond.gLCode))
            sql.append(" dzeinr like '" + cond.gLCode + "%' and ");

        if (!DrwNumberSearchCondition.isEmpty(cond.blockNo)) {
            if (cond.blockNo.contains(",")) {
                String[] blockNos = cond.blockNo.replace("\r\n", "").replace("\n", "").split(",");
                sql.append(" matkl in ( ");
                for (int i = 0; i < blockNos.length; i++) {
                    if (i == blockNos.length - 1)
                        sql.append("'" + blockNos[i].trim() + "' ) and ");
                    else
                        sql.append("'" + blockNos[i].trim() + "', ");
                }
            } else {
                sql.append(" matkl = '" + cond.blockNo.trim() + "' and ");
            }
        }

        sql.append(" prctyp in ('A','N') and ");
        sql.append(" crdat between '" + cond.startDate + "' and '" + cond.endDate + "' ");
        if (checkTpStatus)
            sql.append(" and tp_status = 'T' ");
        sql.append(" and woknum not like 'TEST-%' ");
        if (cond.chkGeneral)
            sql.append(" and woknum like '______L__' ");
        sql.append(" ) s"); // 기간내 등록된 자재들을 먼저 탐색
        sql.append(" where a.woknum = s.first_woknum ");
        sql.append(" and   a.matnr = s.first_matnr ");
        sql.append(" and   a.idnrk = s.first_idnrk ");
        sql.append(" and   a.matkl = s.first_matkl ");
        sql.append(" and   (A.CTYP is null or a.ctyp <> 'T') "); // 주석만 바뀐 레코드는 필요없음.
        if (checkTpStatus)
            sql.append(" and   a.tp_status='T' ");
        sql.append(" )where rn=1 "); // woknum, idnrk, matkl 의 최신데이터를 뽑음
        sql.append(" and prctyp<>'D' "); // 최신 데이터가 D 플래그이면 무시
        if (checkTpStatus)
            sql.append(" and tp_status='T' ");
        sql.append(" ) bom ");

        if (!cond.isOnlyElv())
            sql.append(" left outer  ");
        sql.append(" join elv_info$vf c ");
        sql.append(" on bom.woknum = c.md$number ");
        sql.append(" where ");
        sql.append(" c.vf$ouid is null or ");
        sql.append(" ( ");
        sql.append(" c.vf$ouid in (select id$last from elv_info$id where id$ouid=vf$identity) ");

        if (!DrwNumberSearchCondition.isEmpty(cond.elAtyp)) {
            String[] atyps = cond.elAtyp.split(",");
            sql.append(" and c.EL_ATYP in (select ouid from doscoditm where ");
            for (int i = 0; i < atyps.length; i++) {
                if (i > 0)
                    sql.append(" or ");
                sql.append(" des like '%" + atyps[i].trim() + "%' ");
            }
            sql.append(" ) ");
        }

        if (!DrwNumberSearchCondition.isEmpty(cond.elAspd1))
            sql.append(" and to_number(decode(trim(c.EL_ASPD),null,0,cod(c.EL_ASPD))) >= " + cond.elAspd1.trim() + " ");
        if (!DrwNumberSearchCondition.isEmpty(cond.elAspd2))
            sql.append(" and to_number(decode(trim(c.EL_ASPD),null,0,cod(c.EL_ASPD))) <= " + cond.elAspd2.trim() + " ");

        String amanExpr = "to_number(decode(trim(c.EL_AMAN),null,0,' ',0,' 11',0,' 21',0,' 24',0,' 26',0,'08',0,'09',0,'13M',0,'17L',0,c.EL_AMAN))";
        if (!DrwNumberSearchCondition.isEmpty(cond.elAman1))
            sql.append(" and " + amanExpr + " >= " + cond.elAman1.trim() + " ");
        if (!DrwNumberSearchCondition.isEmpty(cond.elAman2))
            sql.append(" and " + amanExpr + " <= " + cond.elAman2.trim() + " ");

        if (!DrwNumberSearchCondition.isEmpty(cond.elEcsf))
            sql.append(" and c.EL_ECSF in (select ouid from doscoditm where des like '%" + cond.elEcsf.trim() + "%') ");
        if (!DrwNumberSearchCondition.isEmpty(cond.elEtm))
            sql.append(" and c.EL_ETM in (select ouid from doscoditm where des like '%" + cond.elEtm.trim() + "%') ");

        if (!DrwNumberSearchCondition.isEmpty(cond.elEcjj1))
            sql.append(" and to_number(decode(trim(c.EL_ECJJ),null,0,c.EL_ECJJ)) >= " + cond.elEcjj1.trim() + " ");
        if (!DrwNumberSearchCondition.isEmpty(cond.elEcjj2))
            sql.append(" and to_number(decode(trim(c.EL_ECJJ),null,0,c.EL_ECJJ)) <= " + cond.elEcjj2.trim() + " ");

        sql.append(" ) ");
    }

    /**
     * 현장 BOM 추출 - SearchListOfBasedCondition#searchListBasedOnCondition_only
     */
    private DrwNumberSearchResult searchBomOnly() throws Exception {

        System.out.println("### DrwNumberSearchService : search site BOM...");

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM (");
        sql.append(" select crdat ERP전송일자, woknum 공사번호, decode(PLM.matnr,woknum,'*', PLM.matnr) 상위자재, PLM.idnrk 자재번호,menge 수량,dzeinr 도면번호, matkl 블럭번호, md$desc 공사명 ");
        sql.append(" , (SELECT LISTAGG(b.TEXT, CHR(13) || CHR(10)) WITHIN GROUP (ORDER BY b.SERIAL) ");
        sql.append(" 	FROM EAI_BOM_CMT b");
        sql.append(" 	WHERE PLM.AENNR = b.AENNR AND PLM.WOKNUM = b.WOKNUM AND PLM.MATNR = b.MATNR AND PLM.IDNRK = b.IDNRK ) AS 주석");
        sql.append(" from ");
        sql.append(" ( ");
        sql.append(" select a.*,row_number() over(partition by woknum,matnr,idnrk,matkl order by nvl(a.wokver,-1) desc, a.crdat desc, a.seqno desc) rn from eai_bom_body a, ");
        sql.append(" ( ");
        sql.append(" select woknum first_woknum,matnr first_matnr, idnrk first_idnrk,matkl first_matkl from eai_bom_body frist ");
        sql.append(" where crdat between ? and ? ");
        sql.append(" and prctyp in('A','N')  and woknum not like 'TEST-%' ");
        if (cond.chkGeneral)
            sql.append(" and woknum like '______L__' ");
        if (!DrwNumberSearchCondition.isEmpty(cond.partNo)) {
            sql.append(" and ( ");
            appendPartNoCondition(sql, cond.partNo);
            sql.append(" ) ");
        }
        sql.append(" ) s ");
        sql.append(" where a.woknum = s.first_woknum ");
        sql.append(" and   a.matnr = s.first_matnr ");
        sql.append(" and   a.idnrk = s.first_idnrk ");
        sql.append(" and   a.matkl = s.first_matkl ");
        sql.append(" and   (A.CTYP is null or a.ctyp <> 'T') ");
        sql.append(" ) PLM ");
        sql.append(" join elv_info$vf c ");
        sql.append(" on PLM.woknum = c.md$number ");
        sql.append(" where ");
        sql.append(" (c.vf$ouid is null or c.vf$ouid in (select id$last from elv_info$id where id$ouid=vf$identity))");
        sql.append(" AND rn=1 ");
        sql.append(" and prctyp<>'D') ");
        sql.append(" order by ERP전송일자 ");
        sql.append(" FETCH NEXT " + DrwNumberSearchResult.MAX_ROWS + " ROWS ONLY ");

        System.out.println("################## sql : " + sql.toString());

        Connection con = null;
        PreparedStatement stat = null;
        try {
            con = PLMDBConnection.getConnection();
            stat = con.prepareStatement(sql.toString());
            stat.setString(1, cond.startDate);
            stat.setString(2, cond.endDate);
            return execute(stat);
        } finally {
            PLMDBConnection.disconnect(con, stat, null);
        }
    }

    /**
     * 자재번호 조건. 콤마로 여러 건 입력 시 일반 자재는 in (...), '%' 로 끝나는 자재는 like 로 처리.
     */
    private static void appendPartNoCondition(StringBuilder sql, String partNo) {

        if (partNo.indexOf(",") == -1) {
            if (partNo.contains("%"))
                sql.append(" idnrk like '" + partNo.trim() + "' ");
            else
                sql.append(" idnrk = '" + partNo.trim() + "' ");
            return;
        }

        String[] partNos = partNo.replace("\r\n", "").replace("\n", "").split(",");
        List<String> partNoNormal = new ArrayList<String>();
        List<String> partNoLike = new ArrayList<String>();

        for (String p : partNos) {
            String partno = p.trim();

            // 공백 및 오류 데이터는 pass
            if ("".equals(partno) || partno.length() > 20) continue;

            // LIKE문 (%는 마지막에만 사용 가능)
            if (partno.endsWith("%"))
                partNoLike.add(partno);
            else
                partNoNormal.add(partno);
        }

        if (!partNoNormal.isEmpty()) {
            sql.append(" idnrk in (");
            for (int i = 0; i < partNoNormal.size(); i++) {
                if (i > 0)
                    sql.append(" , ");
                sql.append(" '" + partNoNormal.get(i) + "' ");
            }
            sql.append(" )");
        }

        for (int i = 0; i < partNoLike.size(); i++) {
            if (i > 0 || !partNoNormal.isEmpty())
                sql.append(" or ");
            sql.append(" idnrk like '" + partNoLike.get(i) + "'");
        }
    }

    private static DrwNumberSearchResult execute(PreparedStatement stat) throws Exception {

        DrwNumberSearchResult result = new DrwNumberSearchResult();
        ResultSet rs = null;
        try {
            rs = stat.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                result.head.add(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                List<String> row = new ArrayList<String>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                result.data.add(row);

                result.mengeSum += parseDouble(rs.getString("수량"));
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
        }
        return result;
    }

    private static double parseDouble(String str) {
        if (str == null || str.equals("N") || str.equals(""))
            return 0;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
