package test.nz.ac.wgtn.yamf.reporting.audit;

import com.google.common.collect.Lists;
import nz.ac.wgtn.yamf.MarkingResultRecord;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.when;

public class MarkingResultRecordFactory {

    public static MarkingResultRecord create(String taskName, double mark, double maxMark) {
        MarkingResultRecord record = Mockito.mock(MarkingResultRecord.class);
        when(record.getName()).thenReturn(taskName);
        when(record.getMaxMark()).thenReturn(maxMark);
        when(record.getMark()).thenReturn(mark);
        return record;
    }

    public static List<List<MarkingResultRecord>> testData() {
        List<List<MarkingResultRecord>> allResults = Lists.newArrayList(
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2)
            ),
            // reversed values !
            Lists.newArrayList(
                create("task1",2,2),
                create("task2",0,2)
            )
        );
        return allResults;
    }
}
