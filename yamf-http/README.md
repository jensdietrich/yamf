### MVN (Maven) Module

This module contains support for checking Maven projects.

 1. Actions (`MVNActions`) to programmatically run `mvn` commands (`compile`, `test`, ..) and run acceptance tests are extract the `TestResults` (see acceptancetests module) from the generated sure-fire reports
 2. Checks (`MVNChecks`) with various check on Maven projects, including
     1. use of standard project layout
     2. validity of `pom.xml`
     3. compliance of group and artifact id to naming patterns
     4. dependencies
     5. plugins