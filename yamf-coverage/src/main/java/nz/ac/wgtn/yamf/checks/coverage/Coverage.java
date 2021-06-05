package nz.ac.wgtn.yamf.checks.coverage;

import com.google.common.base.Preconditions;
import java.text.NumberFormat;
import java.util.Objects;

/**
 * Data structure to describe coverage.
 * @author jens dietrich
 */
public class Coverage {

    public static final NumberFormat PERCENTAGE_FORMAT = NumberFormat.getPercentInstance();

    static {
        PERCENTAGE_FORMAT.setMinimumFractionDigits(2);
    }

    private int coveredCount = 0;
    private int uncoveredCount = 0;
    private String testedClass = null;
    private CoverageType type = null;


    public Coverage(String testedClass,int coveredCount, int uncoveredCount,CoverageType type) {
        Preconditions.checkArgument(coveredCount>=0);
        Preconditions.checkArgument(uncoveredCount>=0);
        Preconditions.checkArgument(testedClass!=null);
        Preconditions.checkArgument(type!=null);
        this.coveredCount = coveredCount;
        this.uncoveredCount = uncoveredCount;
        this.testedClass = testedClass;
        this.type = type;
    }

    public double getValue() {
        return ((double)coveredCount / ((double)coveredCount)+((double)uncoveredCount));
    }

    public String getPercentage() {
        return PERCENTAGE_FORMAT.format(getValue());
    }

    public int getCoveredCount() {
        return coveredCount;
    }

    public void setCoveredCount(int coveredCount) {
        this.coveredCount = coveredCount;
    }

    public int getUncoveredCount() {
        return uncoveredCount;
    }

    public void setUncoveredCount(int uncoveredCount) {
        this.uncoveredCount = uncoveredCount;
    }

    public String getTestedClass() {
        return testedClass;
    }

    public void setTestedClass(String testedClass) {
        this.testedClass = testedClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coverage coverage = (Coverage) o;
        return Objects.equals(coveredCount, coverage.coveredCount) &&
                Objects.equals(uncoveredCount, coverage.uncoveredCount) &&
                Objects.equals(testedClass, coverage.testedClass) &&
                Objects.equals(type, coverage.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coveredCount, uncoveredCount, testedClass,type);
    }
}
