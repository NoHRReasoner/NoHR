:- abolish_all_tables.
:- set_prolog_flag(unknown,fail).
:- table aShpmtCountry/2.
:- table aPrepackaged/1.
:- table aPartialInspection/1.
:- table aExpeditableImporter/2.
:- table dRegisteredProducer/2.
:- table dHTSChapter/2.
:- table dEURegisteredProducer/1.
:- table dBulk/1.
:- table dHTSHeading/2.
:- table dEUCountry/1.
:- table aBulk/1.
:- table aLowRiskEUCommodity/1.
:- table dShpmtImporter/2.
:- table dEdibleVegetable/1.
:- table nPrepackaged/1.
:- table dRandomInspection/1.
:- table dShpmtCountry/2.
:- table aTariffCharge/2.
:- table aSuspectedBadGuy/1.
:- table dShpmtDeclHTSCode/2.
:- table dAdmissibleImporter/1.
:- table nGrapeTomato/1.
:- table aRegisteredProducer/2.
:- table aEURegisteredProducer/1.
:- table aCommodCountry/2.
:- table aAdmissibleImporter/1.
:- table dGrapeTomato/1.
:- table dPartialInspection/1.
:- table aHTSChapter/2.
:- table aCompliantShpmt/1.
:- table aShpmtDeclHTSCode/2.
:- table dShpmtCommod/2.
:- table aShpmtCommod/2.
:- table dPrepackaged/1.
:- table dCompliantShpmt/1.
:- table dCherryTomato/1.
:- table aEdibleVegetable/1.
:- table dApprovedImporterOf/2.
:- table aShpmtProducer/2.
:- table nCherryTomato/1.
:- table dTariffCharge/2.
:- table dFullInspection/1.
:- table nBulk/1.
:- table dSuspectedBadGuy/1.
:- table dShpmtProducer/2.
:- table aGrapeTomato/1.
:- table aEUCountry/1.
:- table dCommodCountry/2.
:- table aFullInspection/1.
:- table aTomato/1.
:- table aHTSHeading/2.
:- table aApprovedImporterOf/2.
:- table dTomato/1.
:- table dExpeditableImporter/2.
:- table dHTSCode/2.
:- table aRandomInspection/1.
:- table aShpmtImporter/2.
:- table dLowRiskEUCommodity/1.
:- table aHTSCode/2.
:- table aCherryTomato/1.
aEUCountry(cslovakia).
aBulk(cc3).
aEURegisteredProducer(X1) :- aRegisteredProducer(X1, X2), aEUCountry(X2).
dGrapeTomato(cc2) :- tnot nGrapeTomato(cc2).
aEdibleVegetable(cc3).
aShpmtCommod(cs2, cc2).
dShpmtImporter(cs1, ci1).
aEdibleVegetable(X1) :- aTomato(X1).
aShpmtCountry(cs3, cportugal).
dBulk(cc1) :- tnot nBulk(cc1).
aShpmtImporter(cs1, ci1).
aTomato(X1) :- aCherryTomato(X1).
dEdibleVegetable(cc2).
aShpmtDeclHTSCode(cs1, ch7022).
aEdibleVegetable(cc1).
aEdibleVegetable(cc2).
aShpmtDeclHTSCode(cs2, ch7022).
nGrapeTomato(X1) :- aCherryTomato(X1).
dShpmtCountry(cs3, cportugal).
nPrepackaged(X1) :- aBulk(X1).
dShpmtCommod(cs2, cc2).
dLowRiskEUCommodity(X1) :- dCommodCountry(X1, X2), dEUCountry(X2), dExpeditableImporter(X1, X3).
dShpmtCommod(cs3, cc3).
dEUCountry(cslovakia).
aEUCountry(cportugal).
dShpmtImporter(cs3, ci3).
dEdibleVegetable(cc1).
dShpmtImporter(cs2, ci2).
dShpmtCountry(cs2, cportugal).
dEUCountry(cportugal).
dCherryTomato(cc1) :- tnot nCherryTomato(cc1).
dTomato(cc1).
aShpmtImporter(cs3, ci3).
aShpmtCommod(cs1, cc1).
aLowRiskEUCommodity(X1) :- aCommodCountry(X1, X2), aEUCountry(X2), aExpeditableImporter(X1, X3).
aPrepackaged(cc2).
aCherryTomato(cc1).
aTomato(cc3).
dTomato(cc3).
aGrapeTomato(cc3).
aShpmtProducer(cs3, cp1).
dTomato(cc2).
nBulk(X1) :- aPrepackaged(X1).
dShpmtDeclHTSCode(cs1, ch7022).
dTomato(X1) :- dGrapeTomato(X1).
dRegisteredProducer(cp2, cslovakia).
aShpmtImporter(cs2, ci2).
aRegisteredProducer(cp2, cslovakia).
dRegisteredProducer(cp1, cportugal).
nCherryTomato(X1) :- aGrapeTomato(X1).
aShpmtCommod(cs3, cc3).
dEdibleVegetable(cc3).
dTomato(X1) :- dCherryTomato(X1).
dShpmtDeclHTSCode(cs3, ch7021).
dBulk(cc3) :- tnot nBulk(cc3).
dEURegisteredProducer(cp2).
dShpmtDeclHTSCode(cs2, ch7022).
aShpmtCountry(cs2, cportugal).
aEURegisteredProducer(cp1).
aTomato(cc1).
aBulk(cc1).
aShpmtDeclHTSCode(cs3, ch7021).
dShpmtProducer(cs3, cp1).
dGrapeTomato(cc3) :- tnot nGrapeTomato(cc3).
dEURegisteredProducer(X1) :- dRegisteredProducer(X1, X2), dEUCountry(X2).
aRegisteredProducer(cp1, cportugal).
aTomato(cc2).
aTomato(X1) :- aGrapeTomato(X1).
dEURegisteredProducer(cp1).
dShpmtCommod(cs1, cc1).
aGrapeTomato(cc2).
dEdibleVegetable(X1) :- dTomato(X1).
dPrepackaged(cc2) :- tnot nPrepackaged(cc2).
aEURegisteredProducer(cp2).
aCompliantShpmt(X) :- aShpmtCommod(X, Y), aHTSCode(Y, Z), aShpmtDeclHTSCode(X, Z).
dSuspectedBadGuy(X) :- fail.
aSuspectedBadGuy(X) :- fail.
dHTSCode(X, ch7021) :- dGrapeTomato(X).
dCommodCountry(X, Y) :- dShpmtCommod(Z, X), dShpmtCountry(Z, Y).
dTariffCharge(X, 0) :- dCherryTomato(X), dBulk(X).
aPartialInspection(X) :- aRandomInspection(X).
dCompliantShpmt(X) :- dShpmtCommod(X, Y), dHTSCode(Y, Z), dShpmtDeclHTSCode(X, Z).
dApprovedImporterOf(ci3, X) :- dGrapeTomato(X).
dTariffCharge(X, 50) :- dCherryTomato(X), dPrepackaged(X).
dLowRiskEUCommodity(Y) :- fail.
dSuspectedBadGuy(ci1).
aRandomInspection(X) :- aShpmtCommod(X, Y), aRandom(Y).
dApprovedImporterOf(ci2, X) :- dEdibleVegetable(X).
aFullInspection(X) :- aShpmtCommod(X, Y), aTomato(Y), aShpmtCountry(X, cslovakia).
dFullInspection(X) :- dShpmtCommod(X, Y), tnot aCompliantShpmt(X).
aHTSHeading(X, 702) :- aTomato(X).
dFullInspection(X) :- dShpmtCommod(X, Y), dTomato(Y), dShpmtCountry(X, cslovakia).
dTariffCharge(X, 40) :- dGrapeTomato(X), dBulk(X).
aPartialInspection(X) :- aShpmtCommod(X, Y), tnot dLowRiskEUCommodity(Y).
aFullInspection(X) :- aShpmtCommod(X, Y), tnot dCompliantShpmt(X).
dPartialInspection(X) :- dRandomInspection(X).
dCompliantShpmt(X) :- fail.
aCommodCountry(X, Y) :- aShpmtCommod(Z, X), aShpmtCountry(Z, Y).
dPartialInspection(X) :- dShpmtCommod(X, Y), tnot aLowRiskEUCommodity(Y).
aApprovedImporterOf(ci2, X) :- aEdibleVegetable(X).
aHTSChapter(X, 7) :- aEdibleVegetable(X).
aLowRiskEUCommodity(Y) :- fail.
dHTSChapter(X, 7) :- dEdibleVegetable(X).
aTariffCharge(X, 50) :- aCherryTomato(X), aPrepackaged(X).
aTariffCharge(X, 100) :- aGrapeTomato(X), aPrepackaged(X).
aTariffCharge(X, 40) :- aGrapeTomato(X), aBulk(X).
aHTSCode(X, ch7022) :- aCherryTomato(X).
aAdmissibleImporter(X) :- aShpmtImporter(Y, X), tnot dSuspectedBadGuy(X).
dHTSCode(X, ch7022) :- dCherryTomato(X).
aApprovedImporterOf(ci3, X) :- aGrapeTomato(X).
aHTSCode(X, ch7021) :- aGrapeTomato(X).
dRandomInspection(X) :- dShpmtCommod(X, Y), dRandom(Y).
aTariffCharge(X, 0) :- aCherryTomato(X), aBulk(X).
aSuspectedBadGuy(ci1).
aExpeditableImporter(X, Y) :- aShpmtCommod(Z, X), aShpmtImporter(Z, Y), aAdmissibleImporter(Y), aApprovedImporterOf(Y, X).
dTariffCharge(X, 100) :- dGrapeTomato(X), dPrepackaged(X).
aCompliantShpmt(X) :- fail.
dAdmissibleImporter(X) :- dShpmtImporter(Y, X), tnot aSuspectedBadGuy(X).
dHTSHeading(X, 702) :- dTomato(X).
dExpeditableImporter(X, Y) :- dShpmtCommod(Z, X), dShpmtImporter(Z, Y), dAdmissibleImporter(Y), dApprovedImporterOf(Y, X).
