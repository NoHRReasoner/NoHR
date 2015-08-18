/**
 * This is an implementation of A Query Tool for EL with Non-monotonic rules as protege plugin. Everything is quite simple and contains two main parts
 * + union logger: 1) local.translate - package which contains the core of the tool, there is all magic of translating ontologies to rules and
 * combining them with other rules. 2) hybrid.query - package which contains: 2.1) model - core of communication between view, translator and XSB 2.2)
 * views - ui for protege plugin every package has it own description.
 */
