### ByteCode Checks Module

This module contains support for performing some basic bytecode checks.

 1. the action (`JByteCodeActions::getClass`) build an instance of `JClass` from a byte code file (.class)
 2. Checks (`JByteCodeChecks`) contains various check for such instance, e.fg. whether certain fields or methods are present, whether the class extends a certain superclass etc
 
__Limitations and ideas:__ 

   1. Checks are local at the moment (e.g. for inheritance, only the direct superclass is checked)
   2. Extend this and build a full inheritance hierachy and a simple callgraph (CHA) at some stage.
   3. Generic type parameters are ignored, only the descriptors are used. This could be extended to take signatures into account. 
   4. Complement this with a source code analysis to check the use of certain source code elements, e.g. (nested) loops.