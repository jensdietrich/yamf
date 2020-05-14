### Why Yet Another Marking Framework

1. Nobody needs yet another marking framework. But ..
2. Input-output tests are straight-forward and are best done with vanilla unit tests, or even just shell scripts.
3. It is more challenging to test complex designs in 200-400 level programming courses, where students have to come up with increasingly sophisticated designs,  have choices and there is no canonical correct solution. 
4. Static analysis can help to some extent to enforce constraints such as “is a certain design pattern used ?”, “does a method use code from a whitelisted library ?”, “are methods in a certain class synchronised”, “are components used under a permissive license ?”, “what is the test coverage of a project?” and so on.
5. Static analysis would even be useful for 100 level courses: “does method `foo` use a nested loop ?”. 
6. Some tasks will always be too difficult to test and mark automatically:  “Is the code comprehensively commented?”. Lets focus on the 80% we can do now, and acknowledge that there are gaps, and creates tools that support this.
7. Therefore, a useful marking system should address a mix of automated and manual checks. 
8. A particular challenge is ripple effects: some checks fail (method `X:foo(int[])` must sort the array) because previous checks have failed (class `X` must exist). A marking system should support and report this, if possible with instructions how to fix this (rename class to `X`, and carry on marking the rest). This is to avoid double jeopardy.


### Overview

This is a lightweight framework to design automated marking scripts. It uses the junit5 infrastructure, so writing marking scripts is matter of writing vanilla junit tests with an additional annotation for marking. 

1. Classes with checks in `nz.ac.vuw.yamf.checks` packages that can be used in tests, including static checks to test properties in configuration files etc, whether a class file has certain properties etc, and dynamic scripts to run scripts such as acceptance tests, and check the test outcomes. Those checks are implemented using static `assert*` methods, and are based on standard junit `assert*` methods.  Therefore, those checks are usable in any marking system supporting junit tests. 
2. A custom annotation `@Marking` (example: `@Marking(name="task 1",marks=2)`) to be used alongside the standard `@Test` annotation, and some classes to extract this information from test runs and pass this to reporters.
3. A custom annotation `@ManualMarkingIsRequired` (example: `@ManualMarkingIsRequired(instructions="check whether generated coverage reports exist")`) to indicate that something cannot be tested automatically.
4. Pluggable reporters in `nz.ac.vuw.yamf.reporting` to produce marking summaries in plain test and MS Word format.
5. Checks have the following outcomes that are reported:
    1. **success** - marks are allocated as specified in `@Test` 
    2. **failed**-- no marks are allocated
    3. **aborted** -- a precondition (Junit `assume*`) was violated, no mark is allocated, and the test is flagged for manual checking (and an issue `#todo` is created in the report), this is for instance the case if a file required for a check is missing, but this has already been checked and penalised earlier.
    4. **manual** -- manual marking is required, and an issue is created in the report (`#todo`) with instructions how to mark.
6. A utility `nz.ac.vuw.yamf.MarkingScriptBuilder` to set up a script that runs the checks using junit5, extracts marking-related information and generates reports
7. example(s) in `nz.ac.vuw.yamf.examples`


### Usage

As this is not in the central Maven repository, it is recommended to install this into the local repository by running `mvn install`, and then add the 
following dependency to your project (replace `$version` the version from `pom.xml` in this project): 

```xml
<dependency>
    <groupId>nz.ac.vuw.yamf</groupId>
    <artifactId>yamf-core</artifactId>
    <version>$version</version>
</dependency>
```

Particular checks, like the checks for Maven-projects, are located in other modules (`yamf-mvn` in this example) that also need to be added as dependencies.

For Maven - free use, create a Java project in the environment of your choice, and add the following libraries to the project:

* the jar file build from this project with `mvn package` (the jar will be in `/target`) 
* the dependencies of this project that can be extracted with `mvn dependency:copy-dependencies` (the jars will be in `target/dependencies`)

### Example 1: Marking a Maven Project

This example is about marking a project where students were required to use Maven. The full source code can be found in [examples/mvn-static](examples/mvn-static) (example submissions) and [src/main/java/nz/ac/vuw/jmkr/examples/mvn](src/main/java/nz/ac/vuw/jmkr/examples/mvn) (marking scheme and script), respectively. 

A typical project consists of:

* a __scheme__ -- basically a class with some tests
* a __runner__ -- a script to run the tests and produce a marking report  

__Step 1 -- Write the Marking Scheme, annotate tests with the additional @Marking Annotation__

```java
public class MarkingScheme {

    static File submission = null;

    @Test
    @Marking(name="Q1 -- project must contain a valid pom.xml file",marks = 1)
    public void testValidPom () throws Exception {
        File pom = new File(submission,"pom.xml");
        MVNChecks.assertIsPOM(pom);
    }

    @Test
    @Marking(name="Q2 -- project must have valid directory structure with sources in the right places",marks = 2)
    public void testSourceCodeFolderStructure () throws Exception {
        // using junit5s assertAll feature evaluates several assertions even if some fail, and will therefore generate more comprehensive reports
        // if partial marks are to be awarded for some of those asserts, then this question (test) should be split
        Assertions.assertAll(
            () -> MVNChecks.assertHasNonEmptySourceFolder(submission),
            () -> MVNChecks.assertHasNonEmptyTestSourceFolder(submission)
        );
    }
    ...
    @Test
    @Marking(name="Q6 -- tests must all succeed", marks = 2)
    public void testTestSuccess () throws Exception {
        MVNActions.test(submission,true);
        MVNChecks.assertHasNoFailingTests(submission);
    }
    ...
    @Test
    @ManualMarkingIsRequired(instructions="check comments manually, look for completness, correctness, spelling and grammar, 0.5 marks for minor gaps or violations")
    @Marking(name="Q8 -- comments should be comprehensive", marks = 1)
    public void testCodeComments () {}
}
```

Some noteworthy points: 

* the tests reference a project to be checked (`projectFolder`), this is not set in a fixture, but by a marking script (see below)
* there is no additional state to create test dependencies, however, it is sometimes handy to have this (e.g. to compile a project once in a marked test, and then check multiple questions using the results ). If this is required,
the standard (simple, annotation-based) junit5 feature to impose a predictable execution order of tests must be used, see https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order .
* the asserts are mainly provided by the checks implemented here, such as running `mvn` and checking the results of this command, or checking whether the `pom.xml` file in the project checked is valid with respect to a schema
* `testSourceCodeFolderStructure` shows how to use some of the new junit5 features
* the last test is missing, this is something that has to be marked manually. Including it with a special annotation `@ManualMarkingIsRequired` means that it can still be included in reports, where it can be flagged as a TODO for markers. Optionally, marking instructions can be included.

__Step 2 -- Write A Marking Script to run the tests, extending a provided superclass __

```java
public static void main(String[] args) throws Exception {
    new MarkingScriptBuilder()
        .submissions(new File("examples/mvn-static/submissions").listFiles())
        .markingScheme(MarkingScheme.class)
        .beforeMarkingEachProjectDo(submission -> MarkingScheme.submission=submission) // inject project folder !!
        .afterMarkingEachActionDo(submission -> System.out.println("Done marking " + submission.getAbsolutePath()))
        .reportTo(submission -> new MSWordReporter("example-mvn-marks-" + submission.getName() + ".doc"))
        .run();
}
```

Note the (fixture-like) `beforeMarkingEachProjectDo` method, here the current submission folder to be marked will be injected into the marking scheme. Reporters are registered to create marking reports.
There are several reporters to chose from, `MSWordReporter` produces a simple word file that can be manually edited by markers as needed, a generated sample report can be found [here](sample-reports/example-mvn-marks-submission1.doc).

### Example 2: Marking with Acceptance Tests

A common scenario is that marking is done with acceptance tests. This means that as part of a marking script, tests are executed and marks are allocated based on the outcomes of those tests. This example illustrates how to do this. It is based on a setup that there are submissions and a reference solution that is used to define the acceptance tests. Those acceptance tests are organised as junit tests (in this case junit5, but junit4 is also supported). 

Acceptance tests are run in separate processes / JVMs. The example illustrates this for both submissions and reference solution being organised as Maven projects. This can be customised to work for other project types, like Eclipse projects. The sources can be found in [examples/acceptancetests](examples/acceptancetests) (submissions and reference solution with acceptance tests) and [src/main/java/nz/ac/vuw/jmkr/examples/acceptancetests](src/main/java/nz/ac/vuw/jmkr/examples/acceptancetests) (marking scheme and script), respectively. 

Note how running acceptance tests is different from running the tests that are part of the submission, and example for this is included in example 1 (see `testTestSuccess()` in the marking scheme).

This is the marking script. It runs two different sets of vanilla just tests (organised as classes with tests methods annotated with `@Test`, see [examples/acceptancetests/reference-solution-with-tests/src/test/java/acceptancetests](examples/acceptancetests/reference-solution-with-tests/src/test/java/acceptancetests). The submission fails the second test as it does not handle overflows as requested in the assignment brief. The [generated report](sample-reports/example-acceptancetests-marks-submission1.doc) contains a reference to the detailed XML reports junit produces containing full details of why the test failed. 

The check contain standard junit assumptions. They are slightly different from junit assertions as test violating assumptions will be reported as skipped, not failed. For instance, the assumptions would be violated if their were not tests, for instance, if the acceptance test methods did not have a `@Test` annotation.


```java
public class MarkingScheme {

    // injected by the marking script
    static File submission = null;

    // the acceptance tests
    static File acceptanceTestProjectFolder = null;

    @BeforeAll
    public static void prepareAcceptanceTests () throws Exception {
        acceptanceTestProjectFolder = new File("examples/acceptancetests/reference-solution-with-tests");
        assert acceptanceTestProjectFolder.exists();
    }

    @Test
    @Marking(name="Q1 -- run simple acceptance tests",marks=5.0)
    public void runSimpleAcceptanceTests () throws Exception {
        TestResults results = MVNActions.acceptanceTestMvnProject("acceptancetests.TestCalculatorSimple",submission,acceptanceTestProjectFolder,true);
        Assumptions.assumeTrue(results.getTests() == 3);
        Assertions.assertSame(3,results.getTestsSuccessed(),"not all tests succeeded, see " + results.getReportLocation() + " for details");
    }

    @Test
    @Marking(name="Q2 -- run advanced acceptance tests to check overflow handing",marks=5.0)
    public void runAdvancedAcceptanceTests () throws Exception {
        TestResults results = MVNActions.acceptanceTestMvnProject("acceptancetests.TestCalculatorOverflow",submission,acceptanceTestProjectFolder,true);
        Assumptions.assumeTrue(results.getTests() == 1);
        Assertions.assertSame(1,results.getTestsSuccessed(),"not all tests succeeded, see " + results.getReportLocation() + " for details");
    }
}
```


### Limitations

This needs more checks being implemented.



