package io.bdrc.assetmanager.testrun;


import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.iaudit.Outcome;


import java.util.Map;

/**
 * Maps audit tool Outcome to AssetManager TestRunOutcome
 */
public class OutcomeMapper {

    private static final Map<Integer, Integer> auditToolToAssetManagerMap = Map.ofEntries(
        Map.entry(Outcome.NOT_RUN, TestRunOutcome.NOT_RUN),
        Map.entry(Outcome.PASS, TestRunOutcome.PASS),
        Map.entry(Outcome.FAIL, TestRunOutcome.FAIL),
        Map.entry(Outcome.SYS_EXC, TestRunOutcome.SYS_EXC),
        Map.entry(LibOutcome.ROOT_NOT_FOUND, TestRunOutcome.ROOT_NOT_FOUND),
        Map.entry(LibOutcome.FILE_SEQUENCE, TestRunOutcome.FILE_SEQUENCE),
        Map.entry(LibOutcome.DIR_FAILS_SEQUENCE, TestRunOutcome.DIR_FAILS_SEQUENCE),
        Map.entry(LibOutcome.FILES_IN_MAIN_FOLDER, TestRunOutcome.FILES_IN_MAIN_FOLDER),
        Map.entry(LibOutcome.DIR_IN_IMAGES_FOLDER, TestRunOutcome.DIR_IN_IMAGES_FOLDER),
        Map.entry(LibOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER, TestRunOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER),
        Map.entry(LibOutcome.FILE_COUNT, TestRunOutcome.FILE_COUNT),
        Map.entry(LibOutcome.DUP_SEQUENCE, TestRunOutcome.DUP_SEQUENCE),
        Map.entry(LibOutcome.DUP_SEQUENCE_FOLDER, TestRunOutcome.DUP_SEQUENCE_FOLDER)
    );

    private static final Map<Integer, Integer> assetmanagerToAuditToolMap = Map.ofEntries(
            Map.entry(TestRunOutcome.NOT_RUN, Outcome.NOT_RUN),
            Map.entry(TestRunOutcome.PASS, Outcome.PASS),
            Map.entry(TestRunOutcome.FAIL, Outcome.FAIL),
            Map.entry(TestRunOutcome.SYS_EXC, Outcome.SYS_EXC),
            Map.entry(TestRunOutcome.ROOT_NOT_FOUND, LibOutcome.ROOT_NOT_FOUND),
            Map.entry(TestRunOutcome.FILE_SEQUENCE, LibOutcome.FILE_SEQUENCE),
            Map.entry(TestRunOutcome.DIR_FAILS_SEQUENCE, LibOutcome.DIR_FAILS_SEQUENCE),
            Map.entry(TestRunOutcome.FILES_IN_MAIN_FOLDER, LibOutcome.FILES_IN_MAIN_FOLDER),
            Map.entry(TestRunOutcome.DIR_IN_IMAGES_FOLDER, LibOutcome.DIR_IN_IMAGES_FOLDER),
            Map.entry(TestRunOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER, LibOutcome.DIR_FAILS_DIR_IN_IMAGES_FOLDER),
            Map.entry(TestRunOutcome.FILE_COUNT, LibOutcome.FILE_COUNT),
            Map.entry(TestRunOutcome.DUP_SEQUENCE, LibOutcome.DUP_SEQUENCE),
            Map.entry(TestRunOutcome.DUP_SEQUENCE_FOLDER, LibOutcome.DUP_SEQUENCE_FOLDER)
    );

    public static Integer AssetManagerOutcomeFromLibTestOutcome(Integer outcome)
    {
        if (auditToolToAssetManagerMap.containsKey(outcome)) {
            return auditToolToAssetManagerMap.get(outcome);
        }
        return TestRunOutcome.UNKNOWN;
    }

    public static Integer LibTestOutcomeFromAssetManagerOutcome(Integer testRunOutcome) {
        if (assetmanagerToAuditToolMap.containsKey(testRunOutcome)) {
            return assetmanagerToAuditToolMap.get(testRunOutcome);
        }
        return LibOutcome.UNKNOWN;
    }
}
