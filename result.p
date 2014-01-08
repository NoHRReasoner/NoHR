:- abolish_all_tables.
:- set_prolog_flag(unknown,fail).
:- table a9d5ed678fe57bcca610140957afab571/1.
:- table a7fc56270e7a70fa81a5935b72eacbe29/1.
:- table afd3a7fd23e81bd85bd8e4a036ab66b97/1.
:- table af623e75af30e62bbd73d6df5b50bb7b5/1.

a7fc56270e7a70fa81a5935b72eacbe29(X1) :- afd3a7fd23e81bd85bd8e4a036ab66b97(X1).
afd3a7fd23e81bd85bd8e4a036ab66b97(X1) :- a4b43b0aee35624cd95b910189b3dc231(X1, X2), a9d5ed678fe57bcca610140957afab571(X2), a4b43b0aee35624cd95b910189b3dc231(X1, X3), a0d61f8370cad1d412f80b84d143e1257(X3).
af623e75af30e62bbd73d6df5b50bb7b5(X1) :- afd3a7fd23e81bd85bd8e4a036ab66b97(X1).
a9d5ed678fe57bcca610140957afab571(X1) :- afd3a7fd23e81bd85bd8e4a036ab66b97(X1).
