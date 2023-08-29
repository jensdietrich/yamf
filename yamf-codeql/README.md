### Core CodeQL

This module contains actions and checks based on static analysis performed with
[codeql](https://codeql.github.com/docs/codeql-cli/). This requires that the codeql CLI is installed locally, and `codeql` is in the path, 
instructions can be found [here](https://codeql.github.com/docs/codeql-cli/).

#### codeql initialisation

The module communicates with an existing codeql installation on the local machine. For this to work, the environment variable `CODEQL_HOME` must be set, and the command 
`$CODEQL_HOME/codeql` must be executable. 

It is also possible to programmatically set up codeql integration by setting the value of `nz.ac.wgtn.yamf.checks.codeql.CodeQLActions::CODEQL_CMD` to the codeql executable. 

To check whether the integration is working, use `nz.ac.wgtn.yamf.checks.codeql.CodeQLActions::checkCodeQLAvailability` -- the boolean returned is used in 
assumptions to guard checks. 



