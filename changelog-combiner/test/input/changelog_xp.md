# Changelog

## Features
 - Add Project endpoint to management port (#10113).
 - Add readiness check endpoint (#10096).
 - Only allow 1 task to run  (#9277).
 - add WebappResource to the management endpoint (#10135).

## Improvements
 - Allow to give unique name for task with descriptor (#10061).
 - Don't use hazelcast executor in task scheduling (#10122).
 - Improve Trace for Content and Node API (#10033).
 - Improve building of a content's page object (#10110).
 - Move ContentRelativePathResolver to content-studio (#10092).
 - Support running on Java 17 (#10145).
 - TypeScript: AggregationsResult improvements (#9857).
 - Virtual apps bad performance (#10015).
 - WidgetDescriptor is missing localized display name and description (#9714).

## Bugs
 - Admin Tool Descriptors fetch bad performance (#10158).
 - Aggregation type parameters should have defaults (#10101).
 - Aggregations type cast error (#10086).
 - Attachments "byLabel" fail if label is null  (#10008).
 - BooleanFilter clauses should accept Filter, not just Filter[] (#10071).
 - CMYK JPEG images fail to render (#10149).
 - Correctly build content's page object (#10106).
 - ErrorProne and SpotBug fixes (#10075).
 - Inaсcurate TypeScript type for RepoConnection.get with Arrays (#10044).
 - Incorrect type of DuplicateParams.dataProcessor in lib-node types (#10084).
 - JSDocs are missing for overloaded functions (#10012).
 - More detailed info in ContentNotFound exception (#10089).
 - MultipartItem does not report known size (#10104).
 - Pattern mapping does not work on project (#10085).
 - Race condition in Branch cache (#9980).
 - StatusServlet fails to render an error (#10067).
 - Task with archive context does not work (#10059).
 - XP Init may fail on slow server (#10051).
 - base:media must be abstract (#10099).
 - fragment config isn't indexed (#10047).
 - imageUrl calls content API one time too many (#10006).
 - lib-task list params should be optional (#10082).
