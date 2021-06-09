package test.nz.ac.wgtn.yamf.checks.mvn.reporting.audit;

import com.google.common.collect.Lists;
import nz.ac.wgtn.yamf.MarkingResultRecord;
import org.mockito.Mockito;
import java.util.List;
import static org.mockito.Mockito.when;

/**
 * @author jens dietrich
 */
public class MarkingResultRecordFactory {

    public static MarkingResultRecord create(String taskName, double mark, double maxMark) {
        MarkingResultRecord record = Mockito.mock(MarkingResultRecord.class);
        when(record.getName()).thenReturn(taskName);
        when(record.getMaxMark()).thenReturn(maxMark);
        when(record.getMark()).thenReturn(mark);
        return record;
    }

    public static List<List<MarkingResultRecord>> testData1() {
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

    // for 7 results, task1 fails (zero) and task3 also fails
    public static List<List<MarkingResultRecord>> testData2() {
        List<List<MarkingResultRecord>> allResults = Lists.newArrayList(
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",1,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",0,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",0,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",2,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",1,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",0,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",2,2),
                create("task2",0,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",1,2),
                create("task2",1,2),
                create("task3",0,2)
            ),
            Lists.newArrayList(
                create("task1",2,2),
                create("task2",0,2),
                create("task3",1,2)
            )
        );
        return allResults;
    }


    // all tasls get 0/0 -- 0 is used as a max mark for penalties and non-marked tasks,
    // the "too many" rules should not fire
    public static List<List<MarkingResultRecord>> testData3() {
        List<List<MarkingResultRecord>> allResults = Lists.newArrayList(
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                ),
                Lists.newArrayList(
                        create("task1",0,0)
                )
        );
        return allResults;
    }
}
