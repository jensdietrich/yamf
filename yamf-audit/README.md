### Audit Module

This module contains a special reporter that can be used like any other reporter in markings scripts,
but analyses marks for suspicious patterns.

To use this component, simply add the `AuditReporter` as a reporter to the marking script (note that marking scripts support multiple reporters). The constructor has a file parameter,
the audit report will be written to this file in plain text format. `AuditReporter` is based on rules, the following set 
of audit rules is used by default. 

#### `CheckTasksMarkedConsistency` 

This checks whether the same tasks are marked for all submissions, and errors are logged in the audit report if any inconsistencies are detected. 

#### `CheckTooManyFullMarksForSomeTasks`

Checks whether a large percentage (defined w.r.t. a threshold value) of submissions get full marks for some tasks. For each such task, a warning is added to the audit report. The threshold is a percentage value (0-100), the default value is 100. A custom value can be passed as a parameter to the constructor of the class implementing this rule.


#### `CheckTooManyZeroMarksForSomeTasks`

Checks whether a large percentage (defined w.r.t. a threshold value) of submissions get zero marks for some tasks. For each such task, a warning is added to the audit report. The threshold is a percentage value (0-100), the default value is 100. A custom value can be passed as a parameter to the constructor of the class implementing this rule.


#### `CheckDependenciesBetweenTasks`

Checks whether there are at least some (defined by a threshold value) submissions that fail (i.e. zero marks) for some task, and whenever a submission fails for this task, it also fails for some fixed subsequent task. This may indicate a dependency between those tasks and a missing assumption in the subsequent task. 

For instance, task1 may fail because a certain class does not exist. Then later an acceptance test fails in task2 because it references this very class. So the same issue in penalised twice. This could be handled by an assumption on the existance of this class in task2 that would flag this task to be manually inspected and marked. 

The threshold is by default set to 5, a custom value can be set as a parameter to the constructor of the class implementing this rule.

#### Customing `AuditReporter`

The rules listed above are the default rules. This can be overidden using the `AuditReporter::setRules` API, as shown in the following example. This uses the same rules, but custom threshold values.

```
AuditReporter reporter = new AuditReporter(new File("audit-report.txt"));
reporter.setRules(
	new CheckTasksMarkedConsistency(),
	new CheckTooManyFullMarksForSomeTasks(90),
	new CheckTooManyZeroMarksForSomeTasks(80),
	new CheckDependenciesBetweenTasks(10)
);
```

#### Implementing Additional Audit Rules

The interface to be implemented is `nz.ac.wgtn.yamf.reporting.audit.AuditRule`. The key method to implement is `List<Issue> apply (@Nonnull List<List<MarkingResultRecord>> allResults);` , the elements of the outer list represent marked submissions, the elements of the inner list represent marked tasks for each submission. 

Examples of additional audit rules that could be benefitial are be statistical rules that look at suspicious grade distributions. 
 