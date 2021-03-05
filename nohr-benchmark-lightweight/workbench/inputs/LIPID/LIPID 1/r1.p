LC_Clavulone_derivative(X1) :- Glycerophosphonotidylcholine(X1), not Glycerophosphotidylglycerophosphate(X1).
Propyl(X1) :- Carbonyl_Compound_Group(X1), Alkenyl_Group(X1), LC_num1Z-alkenylglycerophosphoglycerophosphomonoradylglycerol(X2).
LC_CDP-Glycerol(X1) :- LC_Straight_chain_fatty_acid(X1), LC_C28_bile_acid_derivative(X1).
LC_Isoprenoid(X1) :- p19(X1, X2).
LC_Bufanolide_derivative(X1) :- Organic_Group(X1), Cyclopentanone(X1).
LC_Lysoglycosphingolipid(X1) :- LC_C24_bile_acid_structural_derivative(X1), not LC_Alkylacylglycerophosphoglycerol(X1).
p15(X1, X2, X3) :- p3(X1, X2), LC_Cholesterol(X3), Seco-Ergostatriene(X4).
Glycerophosphatidylinositol_Diphosphates(X1) :- p11(X1, X2), LC_Fatty_ester(X2).
LC_num1Z-alkenyl_2-acylglycerophosphoserine(X1) :- p19(X1, X2).
LC_Non-ribosomal_peptide_polyketide_hybrid(X1) :- p11(X1, X2).
hasAllyl_Ether_Chain(X1, X2) :- p13(X1, X2), not LC_Fatty_acyl_homoserine_lactone(X1).
LC_Alkylacylglycerophosphoglycerophosphate(X1) :- LC_Hydrocarbon(X1), LC_Monocyclic_aromatic_polyketides(X1), LC_Acylaminosugar(X1), p9(X1, X2, X3).
Seco-Ergostatriene(X1) :- p2(X1, X2).
Hydroxy_Compound(X1) :- p7(X1, X2).
p19(X1, X2) :- Amine_Group(X1), p17(X2, X3).
LC_C25_isoprenoid_par_sesterterpene_par_(X1) :- LC_Sphinganine(X1).
Campestane_fissile_variant(X1) :- LC_num1-alkylglycosylglycerophospholipid(X1).
LC_N-acylsphinganine_par_dihydroceramide_par_(X1) :- LC_Vitamin_D2(X1), not Organic_Nitrogen_Group(X1).
Estrane(X1) :- proximal_Alkenyl(X1).
p20(X1, X2, X3) :- LC_C29_bile_acid_structural_derivative(X1), LC_Lysoglycosphingolipid(X2), LC_Dolichol_monophosphate(X3), Hydroxy_Compound(X4), not Cyclopentanone(X1), not LC_Alkenylacylglycerophosphoglycerophosphomonoradylglycerol(X3), not LC_Wax_ester_mycolic_acid(X4).
p12(X1) :- LC_Glycosylmonoacylglycerol(X1), hasDehydrophytosphinganine_Chain(X2, X3), not LC_Glycosyldiradylglycerol(X1).
Dimeric_Glycan_Group(X1) :- LC_C15_isoprenoid_par_sesquiterpene_par_(X1), not LC_Fatty_acyl_ACP(X1).
p2(X1, X2) :- Neutral_Glycan_series(X1), Polyatomic_Entity(X2), LC_num1Z-alkenyl_2-acylglycerophosphoglycerol(X3).
Glycerophosphotidylglycerol(X1) :- LC_Fatty_acid(X1).
p5(X1) :- LC_Oxygenated_alpha_mycolic_acid(X1), LC_Benzofuranoids(X2), p5(X1).
p11(X1, X2) :- LC_Oxo_fatty_acid(X1), LC_Amphoteric_glycosphingolipid(X2), Cyclic_Ether_Group(X3).
Secondary_Amine(X1) :- p8(X1, X2), Alkyl_Chain_Derivative(X1), not LC_C29_bile_acid_derivative(X1).
p15(X1, X2, X3) :- LC_Vitamin_D4(X1), Cyclic_Ether_Group(X2), hasDehydrophytosphinganine_Chain(X3, X4).
LC_Anthocyanidins(X1) :- Cholane_fissile_variant(X1).
LC_num1Z-alkenylglycerophosphoethanolamine(X1) :- LC_GalNAcb1-4Galb1-4Glc-_par_Ganglio_series_par_(X1), LC_Monoacylglycerophosphoinositol(X1).
p8(X1, X2) :- Biomolecule(X1), LC_C24-propyl_sterol_structural_derivative(X2), LC_Ceramide_1-phosphate(X3).
LC_Ergosterol_structural_derivative(X1) :- LC_C25_bile_acid_derivative(X1), not Iso-Penthyl_Derivative(X1), not LC_Diacylglycerophosphoinositol_trisphosphate(X1).
p3(X1, X2) :- LC_Neutral_glycosphingolipid(X1), LC_Solanidine(X2), hasMeromycolic_Chain(X3, X4).
p9(X1, X2, X3) :- p12(X1), p1(X2, X3), LC_C24_bile_acid_structural_derivative(X4), not LC_num1Z-alkenyl_2-acylglycerophosphate(X4), not p10(X1, X4, X2), not LC_Alkenylacylglycerophosphoglycerophosphomonoradylglycerol(X4).
LC_Monoacylglycerophosphoglycerol(X1) :- LC_Brassinolide(X1).
p3(X1, X2) :- Bicyclic_Heterocycle_System(X1), LC_Depsides_and_depsidones(X2), p2(X3, X4), not has4-hydroxysphinganine_Chain(X4, X1).
Amine_Group(X1) :- p3(X1, X2), not LC_Monoacylglycerophosphoglycerol(X1).
p17(X1, X2) :- LC_Ganglioside(X1), p3(X2, X3).
p6(X1, X2) :- LC_Phytoprenol_diphosphate(X1), hasGlycerol_Group(X2, X3).
LC_Glycerophosphoethanolamine(X1) :- Thiol_Group(X1), p9(X1, X2, X2), not p18(X1).
p10(X1, X2, X3) :- LC_Dialkylglycerophosphoethanolamine(X1), LC_C23_bile_acid_derivative(X2), LC_CDP-Alkenylacylglycerol(X3), LC_Diacylglycerophosphodiradylglycerol(X4).
p16(X1, X2) :- p7(X1, X2), LC_Eicosatrienoic_acid_derivative(X3), Isoprenoid_ring_derivative(X2), LC_N-acylsphinganine_par_dihydroceramide_par_(X4).
Gorgostane(X1) :- Carboxylic_Acid-CoA(X1), p13(X1, X2), LC_Benzopyranoids(X3).
LC_Sphingoid_base_homolog_variant(X1) :- p9(X1, X2, X3), not p20(X1, X2, X3), not Campestano-lactone(X2), not LC_Mono-par-1Z-alkenyl-par-glycerol(X1).
Carbon_Chain_Group(X1) :- p2(X1, X2), p15(X1, X2, X3).
Biomolecule(X1) :- Bufanolide(X1), p11(X1, X2), p3(X1, X2), Glycerophosphatidic_acid(X1).
p19(X1, X2) :- p3(X1, X2), p11(X3, X4).
p20(X1, X2, X3) :- p4(X1, X2, X3), LC_Spirostanol_structural_derivative(X4).
LC_num1-alkyl_glycerophosphocholine(X1) :- LC_num1Z-alkenylglycerophosphoinositol(X1).
Alkyl_Halide_Group(X1) :- LC_Diacylglycerophosphoglycerophosphodiradylglycerol(X1), LC_Monoacylglycerophosphoglycerophosphate(X1).
Tertiary_Amine(X1) :- LC_Eicosatetraenoic_acid_derivative(X1).
LC_C28_bile_acid_derivative(X1) :- LC_Brassinolide_derivative(X1), not LC_Jasmonic_acid(X1), not LC_Spirostanol(X1), not LC_Ganglioside(X1).
p20(X1, X2, X3) :- hasGlycerol_Group(X1, X2), p3(X3, X4), not LC_Methyl_branched_fatty_acid(X4).
LC_C25_bile_acid_derivative(X1) :- p7(X1, X2), p16(X1, X2), LC_Dialkylglycerophosphoinositol(X4), LC_Dialkylglycerophosphoinositol(X5).
p12(X1) :- LC_C22_bile_acid(X1), LC_Linear_tetracycline(X2).
LC_Alkylacylglycerophosphoethanolamine(X1) :- LC_N-acylsphingosine_par_ceramide_par_(X1), Ethyl(X2).
Tocopherol_ring(X1) :- p14(X1), not LC_C25_bile_acid_structural_derivative(X1), not Propyl-Iso-Octyl(X1).
LC_Hydroxy_fatty_acid(X1) :- Neo-lacto_series(X1), p20(X1, X2, X3).
LC_Vitamin_D2_derivative(X1) :- LC_Dialkylmonoacylglycerols(X1), p14(X1), not LC_C5_isoprenoid(X2).
LC_Sphinganine(X1) :- LC_Gorgosterol_derivative(X1), not LC_Glycerophospholipid(X1), not Octyl_Derivative(X1), not LC_Vitamin_D2(X1).
LC_Gal-_par_Gala_series_par_(X1) :- p7(X1, X2), p2(X1, X2), not Propyl-Iso-Octyl(X3).
LC_Steroid_conjugate(X1) :- Alkenyl_Group(X1).
LC_Amphoteric_glycosphingolipid(X1) :- LC_C30_isoprenoid_par_triterpene_par_(X1), not Ganglio_series(X1), not LC_num1-alkyl_2-acylglycerophosphoglycerophosphate(X1), not LC_num1-alkyl_glycerophosphoglycerophosphate(X1).
LC_Alpha_mycolic_acid(X1) :- Glycerophosphatidylinositol_Monophosphate(X1).
LC_Monoacylglycerophosphomonoradylglycerol(X1) :- LC_Alkylacylglycerophosphoserine(X1).
LC_Gorgosterol_derivative(X1) :- Carbonyl_Compound_Group(X1).
LC_Ubiquinone(X1) :- Isoglobo_series(X1), Lactose(X2), Carbon_Chain_Group(X2).
topObjectProperty(X1, X2) :- Sphingoid_Base_Chain_Of(X1, X2).
p19(X1, X2) :- LC_Monoacylglycerophosphoinositol(X1), LC_Biflavonoids_and_polyflavonoids(X2), p6(X3, X4), not LC_Stigmasterol_derivative(X3).
LC_Hexaacylaminosugar(X1) :- LC_Alkenylacylglycosylglycerophospholipid(X1).
Sulfonic_Acid_Group(X1) :- LC_C18_steroid_par_estrogen_par__structural_derivative(X1).
LC_Alkenylacylglycerophosphoinositolglycan(X1) :- Furospirostane(X1).
LC_Steroid_conjugate(X1) :- LC_num1Z-alkenylglycerophosphoglycerophosphomonoradylglycerol(X1), LC_Alkylacylglycerophosphoglycerophosphate(X2).
p10(X1, X2, X3) :- LC_num1Z-alkenyl_2-acylglycerophosphocholine(X1), LC_C24-ethyl_stigmasterol_derivative(X2), Glycerophosphonotidylcholine(X3), hasAcyl_Estolide_Chain(X4, X5).
Terminal_Methoxy(X1) :- p16(X1, X2), LC_Glycerophosphoethanolamine(X2).
Sphing-4-nine_Chain_Of(X1, X2) :- hasChemical_Part(X1, X2), not LC_Methyl_branched_fatty_acid(X1).
Arthro_series(X1) :- Cycloalkane_Group(X1), p11(X1, X2), not p1(X1, X2).
Carboxylic_Acid-Carnitine(X1) :- LC_Vitamin_D2(X1), not LC_Cycloartanol_derivative(X1), not LC_Vitamin_D7_derivative(X1), not LC_C40_isoprenoid_par_tetraterpene_par_(X1).
num5_membered_Heterocyclic_Group(X1) :- LC_Flavonoid(X1), Acyl_Chain_Of(X1, X2).
LC_Solanidine_structural_derivative(X1) :- p9(X1, X2, X3), not distal_Alkenyl(X1).
LC_Cholesterol(X1) :- LC_Furospirostanol_derivative(X1).
p8(X1, X2) :- LC_Vitamin_D7(X1), p14(X2), LC_Galb1-3GlcNAcb1-3Galb1-4Glc-_par_Lacto_series_par_(X3), not p1(X1, X3), not LC_Vitamin_D5_derivative(X3), not LC_Stigmasterol_structural_derivative(X1).
LC_Alkenylacylglycerophosphoinositolglycan(X1) :- p2(X1, X2).
Ganglioside_series(X1) :- p11(X1, X2), Independent_Continuant_Entity(X1).
Carboxylic_Acid_Ester_Group(X1) :- p9(X1, X2, X3).
LC_num1Z-alkenylglycerophosphoinositolglycan(X1) :- Sulfuric_Acid(X1), LC_CDP-Glycerol(X2), LC_Diacylglycerophosphoinositolglycan(X1).
hasAllyl_Ether_Chain(X1, X2) :- Carboxylic_Acid_Ester_Group(X1), LC_Calysterol(X2).
LC_num1Z-alkenyldiacylglycerols(X1) :- p16(X1, X2).
p19(X1, X2) :- p2(X1, X2), Phosphate_Group_Of(X3, X4).
LC_CDP-diacylglycerol(X1) :- hasSphinganine_Chain(X1, X2).
p17(X1, X2) :- LC_Methyl_branched_fatty_acid(X1), p13(X2, X3).
p2(X1, X2) :- p17(X1, X2), LC_Flavans_Flavanols_and_Leucoanthocyanidins(X3), LC_C22_bile_acid(X1), LC_Hydrocarbon(X2).
LC_Heptaacylaminosugar(X1) :- LC_Amphoteric_glycosphingolipid(X1), not LC_C24-methyl_ergosterol_derivative(X1).
LC_Lipoxin(X1) :- LC_Diacylglycerophosphoglycerol(X1), p16(X1, X2), not LC_Cardanolide_structural_derivative(X3).
LC_Phosphosphingolipid(X1) :- LC_Dibenzofurans_griseofulvins_dibenzopyrans_and_xanthones(X1).
Carboxylic_Acid_derivative_Group(X1) :- p8(X1, X2), LC_Monoacylglycerophosphoserine(X1).
LC_Alkenylacylglycerophosphoglycerophosphomonoradylglycerol(X1) :- LC_Vitamin_D5_structural_derivative(X1), LC_Epoxyeicosatrienoic_acid(X2).
p7(X1, X2) :- LC_Wax_diester(X1), LC_Bufanolide_derivative(X2), LC_Dolichol(X3).
LC_C18_steroid_par_estrogen_par__structural_derivative(X1) :- p18(X1), Glycerophosphatidylinositol_Diphosphates(X2).
p17(X1, X2) :- LC_Non-ribosomal_peptide_polyketide_hybrid(X1), LC_Vitamin_D5_structural_derivative(X2), p4(X3, X4, X5).
%f1 end
