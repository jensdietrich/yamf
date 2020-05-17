### Acceptance Test Module

Part of marking is to run tests. Those tests are usually defined outside the project that is to be marked. This module contains basic support for this setup.

 1. Actions (`JUnitActions`) how to run tests programmatically, and represent the test results (`TestResults`)
 2. Checks (`JUnitChecks`) to run checks against the test results
 
See also `mvn` module for how to run acceptance tests more easily if both the submission and the acceptance tests are maven projects.