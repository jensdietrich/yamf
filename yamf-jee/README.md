### JEE Module

This module contains support for checking JEE projects.

 1. Actions (`JEEActions`) to extract servlet mappings from a JEE project as a map, both the`web.xml`- and the annotation-based approach are supported.
 2. Checks (`JEEChecks`) with various check on JEE projects, including
     1. whether a class is a servlet
     2. whether a servlet implements handlers for some HTTP methods (`doGet` etc)
