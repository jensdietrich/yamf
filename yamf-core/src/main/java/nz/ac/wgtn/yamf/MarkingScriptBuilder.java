package nz.ac.wgtn.yamf;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.reporting.Reporter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

/**
 * Marking script builder.
 * @author jens dietrich
 */
public class MarkingScriptBuilder {


    private Consumer<File> beforeMarkingEachProject = (projectFolder) -> {};
    private Consumer<File> afterMarkingEachProject = (projectFolder) -> {};
    private Predicate<File> skipMarkingProjectCondition = (projectFolder) -> false;
    private Runnable beforeMarkingAllProjects = () -> {};
    private Runnable afterMarkingAllProjects = () -> {};
    private List<Function<File, Reporter>> reporterFactories = new ArrayList<>();
    private File[] submissions = null;
    private Class markingScheme = null;

    // this is not to rely on the correct static fixture, if true (default), all reference static fields will be reset to null
    private boolean hardResetMarkingSchemeBetweenSubmissions = true;
    private boolean configureLogging = true;
    private Level logLevel = Level.INFO;

    public MarkingScriptBuilder beforeMarkingEachProjectDo(Consumer<File> action) {
        this.beforeMarkingEachProject = action;
        return this;
    }

    public MarkingScriptBuilder afterMarkingEachActionDo(Consumer<File> action) {
        this.afterMarkingEachProject = action;
        return this;
    }

    public MarkingScriptBuilder beforeMarkingAllProjectsDo(Runnable action) {
        this.beforeMarkingAllProjects = action;
        return this;
    }

    public MarkingScriptBuilder skipMarkingProjectIf(Predicate<File> condition) {
        this.skipMarkingProjectCondition = condition;
        return this;
    }

    public MarkingScriptBuilder afterMarkingAllProjectsDo(Runnable action) {
        this.afterMarkingAllProjects = action;
        return this;
    }

    public MarkingScriptBuilder hardResetMarkingSchemeBetweenSubmissions(boolean value) {
        this.hardResetMarkingSchemeBetweenSubmissions = value;
        return this;
    }

    // the function maps submissions to reporters
    public MarkingScriptBuilder reportTo(Function<File,Reporter> reporter) {
        this.reporterFactories.add(reporter);
        return this;
    }

    public MarkingScriptBuilder submissions(File[] submissions) {
        this.submissions = submissions;
        return this;
    }

    public MarkingScriptBuilder submissions(Collection<File> submissions) {
        this.submissions = submissions.toArray(new File[submissions.size()]);
        return this;
    }

    public MarkingScriptBuilder markingScheme(Class markingScheme) {
        this.markingScheme = markingScheme;
        return this;
    }

    public MarkingScriptBuilder logLevel(Level logLevel) {
        Preconditions.checkNotNull(logLevel);
        this.logLevel = logLevel;
        return this;
    }

    public MarkingScriptBuilder configureLogging(boolean value) {
        this.configureLogging = value;
        return this;
    }

    public void run() throws Exception {
        Preconditions.checkState(this.submissions!=null,"submissions to mark must be set");
        Preconditions.checkState(this.markingScheme!=null,"marking scheme must be set");
        Preconditions.checkState(!this.reporterFactories.isEmpty(),"at least one reporter must be set");

        if (this.configureLogging) {
            System.out.println("Configuring logging");
            Configurator.initialize(new DefaultConfiguration());
            Configurator.setRootLevel(this.logLevel);
        }

        Logger logger = LogManager.getLogger("marking-script");


        beforeMarkingAllProjects.run();
        Set<Reporter> reporters = new LinkedHashSet<>();
        for (File projectFolder:submissions)   {
            if (projectFolder.isDirectory()) {
                if (!skipMarkingProjectCondition.test(projectFolder)) {

                    // hard-reset static fields in marking scheme
                    if (hardResetMarkingSchemeBetweenSubmissions) {
                        resetScheme(logger,markingScheme);
                    }

                    beforeMarkingEachProject.accept(projectFolder);
                    // junit boilerplate code
                    List<DiscoverySelector> selectors = new ArrayList<>();

                    selectors.add(selectClass(markingScheme));
                    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                        .request()
                        .selectors(selectors).build();
                    Launcher launcher = LauncherFactory.create();
                    MarkingTestExecutionListener listener = new MarkingTestExecutionListener();
                    launcher.registerTestExecutionListeners(listener);
                    launcher.execute(request);
                    List<MarkingResultRecord> results = listener.getResults();
                    for (Function<File, Reporter> reporterFactory : reporterFactories) {
                        Reporter reporter = reporterFactory.apply(projectFolder);
                        reporters.add(reporter);
                        try {
                            reporter.generateReport(projectFolder, results);
                        }
                        catch (Exception x) {
                            logger.error("Error generating report using reporter " + reporter.toString(),x);
                        }
                    }
                    afterMarkingEachProject.accept(projectFolder);
                }
                else {
                    logger.info("Skipping project " + projectFolder);
                }
            }
        }

        for (Reporter reporter:reporters) {
            reporter.finish();
        }

        afterMarkingAllProjects.run();
    }

    // reset static fields to avoid state being transferred between evaluations
    // this would typically be done in the static fixture of the schemes but if forgotten,
    // this can have dire consequences
    private void resetScheme(Logger logger,Class markingScheme) {
        for (Field field:markingScheme.getDeclaredFields()) {
            String name = markingScheme.getName() + "::" + field.getName();
            if (Modifier.isStatic(field.getModifiers()) && !field.getType().isPrimitive()) {
                logger.info("resetting marking scheme field: " + name);
                try {
                    field.setAccessible(true);
                    field.set(null,null);
                } catch (IllegalAccessException e) {
                    logger.error("cannot reset field between evaluations:" + name );
                }
            }
        }
    }

}
