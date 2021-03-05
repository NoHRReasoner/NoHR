subOrganizationOf(?X,?Z) :- subOrganizationOf(?X,?Y), subOrganizationOf(?Y,?Z).
Chair(?X) :- Person(?X), headOf(?X,?Y), Department(?Y).
Director(?X) :- Person(?X), headOf(?X,?Y), Program(?Y).
Employee(?X) :- Person(?X), worksFor(?X,?Y), Organization(?Y).
Student(?X) :- Person(?X), takesCourse(?X,?Y), Course(?Y).
TeachingAssistant(?X) :- Person(?X), teachingAssistantOf(?X,?Y), Course(?Y).
Dean(?X) :- headOf(?X,?Y),  College(?Y).

Person(?X) :- age(?X,?Y).
Person(?X) :- emailAddress(?X,?Y).
Person(?X) :- telephone(?X,?Y).
Person(?X) :- title(?X,?Y).
