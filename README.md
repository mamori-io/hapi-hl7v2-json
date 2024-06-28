HAPI HL7 v2 to JSON converter
=======================================

A simple utility to convert a HAPI HL7 v2 message to JSON.

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


## CI/CD

## How to use

````java
    // Parse/Create a message using HAPI
    HapiContext context = new DefaultHapiContext();
    Parser p = context.getPipeParser();
    Message m = p.parse(....);

    // Run the visitor...
    JSONVisitor visitor = new JSONVisitor(m.getName(), m.getVersion());
    MessageVisitors.visit(m, visitor);
    JSONObject json = visitor.getPayload(); <-- produces JSON
   
    System.out.println(json);
````
## Learn more about HAPI

[HAPI](https://hapifhir.github.io/hapi-hl7v2/)


## License

This project is Open Source, licensed under the Apache Software License 2.0.
