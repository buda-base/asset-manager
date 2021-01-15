package io.bdrc.assetmanager.testrun;

import io.bdrc.am.audit.iaudit.Outcome;

import java.util.EnumMap;

/**
 * Maps audit tool Outcome to AssetManager TestRunOutcome
 */
public class OutcomeMapper {
    private static EnumMap<Outcome, TestRunOutcome> auditToolToAssetManagerMap = new EnumMap<>(Outcome.class);
    private static EnumMap<TestRunOutcome, Outcome> assetmanagerToAuditToolMap = new EnumMap<>(TestRunOutcome.class);

    private OutcomeMapper() {

        auditToolToAssetManagerMap.put(Outcome.NOT_RUN, TestRunOutcome.NOT_RUN);
        auditToolToAssetManagerMap.put(Outcome.PASS, TestRunOutcome.PASS);
        auditToolToAssetManagerMap.put(Outcome.FAIL, TestRunOutcome.FAIL);
        auditToolToAssetManagerMap.put(Outcome.SYS_EXC, TestRunOutcome.SYS_EXC);
        auditToolToAssetManagerMap.put(Outcome.ROOT_NOT_FOUND, TestRunOutcome.ROOT_NOT_FOUND);
        auditToolToAssetManagerMap.put(Outcome.FILE_SEQUENCE, TestRunOutcome.FILE_SEQUENCE);
        auditToolToAssetManagerMap.put(Outcome.DIR_FAILS_SEQUENCE, TestRunOutcome.DIR_FAILS_SEQUENCE);
        auditToolToAssetManagerMap.put(Outcome.FILES_IN_MAIN_FOLDER, TestRunOutcome.FILES_IN_MAIN_FOLDER);
        auditToolToAssetManagerMap.put(Outcome.DIR_IN_IMAGES_FOLDER, TestRunOutcome.DIR_IN_IMAGES_FOLDER);
        auditToolToAssetManagerMap.put(Outcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER, TestRunOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER);
        auditToolToAssetManagerMap.put(Outcome.FILE_COUNT, TestRunOutcome.FILE_COUNT);
        auditToolToAssetManagerMap.put(Outcome.DUP_SEQUENCE, TestRunOutcome.DUP_SEQUENCE);
        auditToolToAssetManagerMap.put(Outcome.DUP_SEQUENCE_FOLDER, TestRunOutcome.DUP_SEQUENCE_FOLDER);

        assetmanagerToAuditToolMap.put(TestRunOutcome.NOT_RUN, Outcome.NOT_RUN);
        assetmanagerToAuditToolMap.put(TestRunOutcome.PASS, Outcome.PASS);
        assetmanagerToAuditToolMap.put(TestRunOutcome.FAIL, Outcome.FAIL);
        assetmanagerToAuditToolMap.put(TestRunOutcome.SYS_EXC, Outcome.SYS_EXC);
        assetmanagerToAuditToolMap.put(TestRunOutcome.ROOT_NOT_FOUND, Outcome.ROOT_NOT_FOUND);
        assetmanagerToAuditToolMap.put(TestRunOutcome.FILE_SEQUENCE, Outcome.FILE_SEQUENCE);
        assetmanagerToAuditToolMap.put(TestRunOutcome.DIR_FAILS_SEQUENCE, Outcome.DIR_FAILS_SEQUENCE);
        assetmanagerToAuditToolMap.put(TestRunOutcome.FILES_IN_MAIN_FOLDER, Outcome.FILES_IN_MAIN_FOLDER);
        assetmanagerToAuditToolMap.put(TestRunOutcome.DIR_IN_IMAGES_FOLDER, Outcome.DIR_IN_IMAGES_FOLDER);
        assetmanagerToAuditToolMap.put(TestRunOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER, Outcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER);
        assetmanagerToAuditToolMap.put(TestRunOutcome.FILE_COUNT, Outcome.FILE_COUNT);
        assetmanagerToAuditToolMap.put(TestRunOutcome.DUP_SEQUENCE, Outcome.DUP_SEQUENCE);
        assetmanagerToAuditToolMap.put(TestRunOutcome.DUP_SEQUENCE_FOLDER, Outcome.DUP_SEQUENCE_FOLDER);
    }

    private static OutcomeMapper _instance = null;

    public static OutcomeMapper getInstance() {
        if (_instance == null) {
            _instance = new OutcomeMapper();
        }
        return _instance;
    }

    public static TestRunOutcome fromOutcome(Outcome outcome)
    {
        if (auditToolToAssetManagerMap.containsKey(outcome)) {
            return auditToolToAssetManagerMap.get(outcome);
        }
        return TestRunOutcome.UNKNOWN;
    }

    public static Outcome fromTestRunOutcome(TestRunOutcome testRunOutcome) {
        return assetmanagerToAuditToolMap.get(testRunOutcome);
    }
}
