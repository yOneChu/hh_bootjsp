package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimContext;
import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * PID 테스트용 실행 환경 (SimContext 확장)
 * SimContext 는 코드 특성 name@ 값을 항상 "name [코드ID]" 로 주므로,
 * codeFieldDisplay=false(운영 etcConfig.xml 과 동일) 이면 " [코드ID]" 를 떼고 코드명만 돌려준다.
 */
public class PidTestContext extends SimContext {

    private final boolean codeFieldDisplay;
    private final Map<String, Map<String, String>> nameOnlyCache = new HashMap<String, Map<String, String>>();

    public PidTestContext(SimDb db, SimPidRepository pidRepository, boolean saveErrorLog, boolean codeFieldDisplay) {
        super(db, pidRepository, saveErrorLog);
        this.codeFieldDisplay = codeFieldDisplay;
    }

    @Override
    public Map<String, String> getCodeNames(String ouid) throws Exception {
        Map<String, String> names = super.getCodeNames(ouid);
        if (codeFieldDisplay || names == null) {
            return names;
        }

        Map<String, String> nameOnly = nameOnlyCache.get(ouid);
        if (nameOnly == null) {
            nameOnly = new HashMap<String, String>();
            for (Map.Entry<String, String> e : names.entrySet()) {
                String v = e.getValue();
                nameOnly.put(e.getKey(), v == null ? null : v.replaceFirst(" \\[[^\\[\\]]*\\]$", ""));
            }
            nameOnlyCache.put(ouid, nameOnly);
        }
        return nameOnly;
    }
}
