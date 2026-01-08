# Source Connectors Extension

This extension is run during compilation to collect all classes that implement the `SourceConnector` interface and register them with the application.

It makes it easy for the Scheduler to access a map of source connector classes: `Map<String, Class<? extends SourceConnector>> sourceConnectorClasses;`