package pt.unl.fct.di.novalincs.nohr.deductivedb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */

import static pt.unl.fct.di.novalincs.nohr.model.Model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.utils.CreatingMappings;

/**
 * Implements an {@link DeductiveDatabase} backed by a XSB Prolog system.
 *
 * @author Nuno Costa
 */
public class XSBDeductiveDatabase extends PrologDeductiveDatabase {
	private String aph;
	private String db;
	private String ontologyDesc;

	/**
	 * Constructs a {@link DeductiveDatabase} with the XSB Prolog system located
	 * in a given directory as underlying Prolog engine.
	 *
	 * @param binDirectory
	 *            the directory where the Prolog system that will be used as
	 *            underlying Prolog engine is located.
	 * @throws IPException
	 *             if some exception was thrown by the Interprolog API.
	 * @throws PrologEngineCreationException
	 *             if the creation of the underlying Prolog engine timed out.
	 *             That could mean that the Prolog system located at
	 *             {@code binDirectory} isn't an operational Prolog system.
	 * @throws IOException
	 */
	public XSBDeductiveDatabase(File binDirectory, Vocabulary vocabularyMapping) throws PrologEngineCreationException {
		super(binDirectory, "xsbmodule", new XSBFormatVisitor(), vocabularyMapping);
	}

	@Override
	protected PrologEngine createPrologEngine() {
		return new XSBSubprocessEngine(binDirectory.toPath().toAbsolutePath().toString());
	}

	@Override
	protected String failRule(Predicate pred) {
		return rule(atom(pred), atom(vocabulary, "fail")).accept(formatVisitor);
	}

	@Override
	public boolean hasWFS() {
		return true;
	}

	@Override
	protected void initializePrologEngine() {
		prologEngine.deterministicGoal("set_prolog_flag(unknown, fail)");
	}

	@Override
	protected void load() {

//		vedran();
		 giannis();

		if (!prologEngine.load_dynAbsolute(file.getAbsoluteFile()))
			throw new IPException("file not loaded");
	}

	public void vedran() {

		File dest = new File("C:\\Users\\VedranPC\\Desktop\\rules.txt");
		String ontologyDest = "http://www.semanticweb.org/vedranpc/ontologies/2017/7/crimes#";
		try {
			// connection to the database
			FileWriter out = new FileWriter(file, true);
			out.write(":- import odbc_open/3 from odbc_call.\n" + ":- import findall_odbc_sql/3 from odbc_call.\n" +
			// ":- import odbc_import/2 from odbc_call.\n"+
					":- import odbc_close/0 from odbc_call.\n" + ":- import odbc_data_sources/2 from odbc_call.\n"
					+ "?- odbc_open(test,root,root).\n");
			out.close();

		} catch (IOException e) {
			System.err.println("Greskaa!");
			e.printStackTrace();
		}

		CreatingMappings mapRule = new CreatingMappings("`", "test",
				"http://www.semanticweb.org/vedranpc/ontologies/2017/7/crimes#");

		mapRule.createPredicateMapping("crimesindex1", new String[] { "crimesID", "Primary_Type" }, "crimeType", true,
				dest, ontologyDest, "`");

		mapRule.createPredicateMapping("crimesindex1", new String[] { "crimesID" }, "crime", true, file, ontologyDest,
				"`");

		mapRule.createPredicateMapping("crimesindex1", new String[] { "District" }, "district", true, file,
				ontologyDest, "`");

		mapRule.createPredicateMapping("crimesindex1", new String[] { "crimesID", "Primary_Type" }, "crimeType", true,
				file, ontologyDest, "`");

		mapRule.createPredicateMapping("crimesindex1", new String[] { "crimesID", "District" }, "crimeDristrict", true,
				file, ontologyDest, "`");

		try {
			FileUtils.copyFile(file, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void giannis() {
		File dest = new File("G:\\Users\\gerochrisi.V2\\Desktop\\mappingStuff\\rules.txt");
		String ontologyDest = "http://www.semanticweb.org/gerochrisi/ontologies/2017/4/inventory-ontology#";
		try {
			// connection to the database
			FileWriter out = new FileWriter(file, true);
			out.write(":- import odbc_open/3 from odbc_call.\n" + ":- import findall_odbc_sql/3 from odbc_call.\n"
					+ ":- import odbc_close/0 from odbc_call.\n" + ":- import odbc_data_sources/2 from odbc_call.\n"
					+ "?- odbc_open('oracle_fi','NOC_USER','CrnbwXyaBMUVOh').\n" + ":- table 'doneconn'/2.\n"
					+ ":- table 'aoneconn'/2.\n");
			out.close();
		} catch (IOException e) {
			System.err.println("Mistake!");
			e.printStackTrace();
		}
		CreatingMappings mapRule = new CreatingMappings("\"", "NOC_USER",
				"http://www.semanticweb.org/gerochrisi/ontologies/2017/4/inventory-ontology#");

		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "Z_VENDOR" },
				"z_vendor", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "ER_TYPE" },
				"er_type", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "A_NETYPE" },
				"a_netype", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "Z_PHYSICALPORTID" }, "z_physicalportid", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "Z_NETYPE" },
				"z_netype", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "A_PHYSICALPORTID" }, "a_physicalportid", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "CONNECTION_TRAIL_INST_ID" }, "connection_trail_inst_id",
				true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "A_MEK" },
				"a_mek", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "A_VENDOR" },
				"a_vendor", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "LOGICALRESOURCENAME" }, "logicalresourcename", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "Z_MEK" },
				"z_mek", true, file, ontologyDest, "\"");

		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "ADDRESS" }, "address", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "SITEIST" }, "siteist", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "SITEMEK" }, "sitemek", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "LAT" }, "lat", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "LONGT" }, "longt", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "CLLI" }, "location_clli", true,
				file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "CONTACTS" }, "contacts", true,
				file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "POST_CODE_1" }, "post_code_1",
				true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "CITY" }, "city", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "STATE_PROV" }, "state_prov", true,
				file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "COUNTRY" }, "country", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "STATUS" }, "location_status", true,
				file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "LAST_MOD_TS" }, "last_mod_ts",
				true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK", "LAST_MOD_BY" }, "last_mod_by",
				true, file, ontologyDest, "\"");

		mapRule.createPredicateMapping("PT_NODES_MV",
				new String[] { "holdercomposite_inst_id", "HOLDERCOMPOSITEID", "HOLDER_COMPOSITE_INST_ID" },
				"holdercomposite_inst_id", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "PLACEID_INST_ID" },
				"placeid_inst_id", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "CLLI" }, "node_clli", true,
				file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "MODEL" }, "model", true,
				file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "HOLDERCOMPOSITEID" },
				"holdercompositeid", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "PHYSICALRESOURCEROLENAME" },
				"physicalresourcerolename", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "STATUS" }, "node_status",
				true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "LAST_MODIFIED_TS" },
				"last_modified_ts", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "LAST_MODIFIED_BY" },
				"last_modified_by", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "VENDOR" }, "vendor", true,
				file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID", "SITE" }, "site", true, file,
				ontologyDest, "\"");

		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "Z_MEK", "A_NETYPE" }, "oneconn", false,
				file, "", "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "A_MEK", "Z_NETYPE" }, "oneconn", false,
				file, "", "\"");

		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITEID" }, "Node", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEMEK" }, "Location", true, file,
				ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID" }, "Connection",
				true, file, ontologyDest, "\"");

		createObjectProperties("NOC_USER", "PT_NODES_MV", "PT_LOCATIONS_MV", "SITE", "SITEIST", "isLocated",
				"HOLDERCOMPOSITEID", "SITEMEK", file, ontologyDest, "\"");

		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "A_MEK", "Z_MEK" }, "isConnected", true,
				file, ontologyDest, "\"");
//		createObjectProperties("NOC_USER", "PT_NODES_MV", "PT_NODES_MV", "HOLDERCOMPOSITEID", "HOLDERCOMPOSITEID",
//				"isConnected", "HOLDERCOMPOSITEID", "HOLDERCOMPOSITEID", file, ontologyDest, "\"");

		try {
			FileUtils.copyFile(file, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * public void createDataProperties(String db, String table, String
	 * predicate, String column1, String column2,File file,String ontology,
	 * String apostrophe){ boolean isDL=!ontology.matches(""); String
	 * ontologyDest=""; String cl=""; if(isDL){ ontologyDest=ontology; cl=">"; }
	 * String aph="\\"+apostrophe; try { FileWriter out = new FileWriter
	 * (file,true); // out.write( // predicate(false, true, predicate) + vars(2)
	 * + " :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT "+ aph +
	 * column1 + aph + ", "+ aph + column2 + aph + " FROM "+ aph+db+aph + "."+
	 * aph+table+aph + " WHERE "+ aph+column1+aph + " = ? AND "+ aph+column2+aph
	 * + " = ? ', [X,Y]). \n"+ // predicate(false, true, predicate) + vars(2) +
	 * " :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT "+ aph + column1 +
	 * aph + ", "+ aph + column2 + aph + " FROM "+ aph+db+aph + "."+
	 * aph+table+aph + " WHERE "+ aph+column1+aph + " = ?', [X,Y]). \n"+ //
	 * predicate(false, true, predicate) + vars(2) +
	 * " :- var(X), nonvar(Y), findall_odbc_sql([Y],'SELECT "+ aph + column1 +
	 * aph + ", "+ aph + column2 + aph + " FROM "+ aph+db+aph + "."+
	 * aph+table+aph + " WHERE "+ aph+column2+aph + " = ?', [X,Y]). \n"+ //
	 * predicate(false, true, predicate) + vars(2) +
	 * " :- var(X), var(Y), findall_odbc_sql([],'SELECT "+ a + column1 + a +
	 * ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " ', [X,Y]). \n"+ // predicate(true, true, predicate) + vars(2) +
	 * " :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT "+ aph + column1
	 * + aph + ", "+ aph + column2 + aph + " FROM "+ aph+db+aph + "."+
	 * aph+table+aph + " WHERE "+ aph+column1+aph + " = ? AND "+ aph+column2+aph
	 * + " = ? ', [X,Y]). \n"+ // predicate(true, true, predicate) + vars(2) +
	 * " :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT "+ aph + column1 +
	 * aph + ", "+ aph + column2 + aph + " FROM "+ aph+db+aph + "."+
	 * aph+table+aph + " WHERE "+ aph+column1+aph + " = ? ', [X,Y]). \n"+ //
	 * predicate(true, true, predicate) + vars(2) +
	 * " :- var(X), nonvar(Y),findall_odbc_sql([Y],'SELECT "+ aph + column1 +
	 * aph + ", "+ aph + column2 + aph + " FROM "+ aph+db+aph + "."+
	 * aph+table+aph + " WHERE "+ aph+column2+aph + " = ? ', [X,Y]). \n"+ //
	 * predicate(true, true, predicate) + vars(2) +
	 * " :- var(X), var(Y), findall_odbc_sql([],'SELECT "+ a + column1 + a +
	 * ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " ', [X,Y]). \n"); out.close(); } catch (IOException e) {
	 * System.err.println("Mistake with createDataProperties.");
	 * e.printStackTrace(); } } private String vars(int n){ String vars="";
	 * for(int i=0;i<n;i++) vars=vars+"V"+i+","; return vars.substring(0,
	 * vars.length()-1); } private String predicate(boolean doubled, boolean
	 * isDL, String predicate){ String fullPred, type = doubled ? "d" : "a";
	 * 
	 * if(isDL) fullPred = "'"+ type + "<" + ontologyDesc + predicate + ">";
	 * else fullPred= "'"+ type + predicate +"'";
	 * 
	 * return fullPred; }
	 * 
	 * public void createNonDLDataProperties(String db, String table, String
	 * predicate, String column1, String column2,File file,String ontologyDest,
	 * String apostrophe){
	 * 
	 * String a="\\"+apostrophe; try { FileWriter out = new FileWriter
	 * (file,true); out.write("'a" + ontologyDest +predicate +
	 * "'(X,Y) :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT "+ a +
	 * column1 + a + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " WHERE "+ a+column1+a + " = ? AND "+ a+column2+a + " = ? ', [X,Y]). \n"+
	 * "'a" + ontologyDest +predicate +
	 * "'(X,Y) :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT "+ a + column1
	 * + a + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " WHERE "+ a+column1+a + " = ?', [X,Y]). \n"+ "'a" + ontologyDest
	 * +predicate +
	 * "'(X,Y) :- var(X), nonvar(Y), findall_odbc_sql([Y],'SELECT "+ a + column1
	 * + a + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " WHERE "+ a+column2+a + " = ?', [X,Y]). \n"+ // "'a" + ontologyDest
	 * +predicate + "'(X,Y) :- var(X), var(Y), findall_odbc_sql([],'SELECT "+ a
	 * + column1 + a + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a
	 * + " ', [X,Y]). \n"+ "'d" + ontologyDest +predicate +
	 * "'(X,Y) :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT "+ a +
	 * column1 + a + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " WHERE "+ a+column1+a + " = ? AND "+ a+column2+a + " = ? ', [X,Y]). \n"+
	 * "'d" + ontologyDest +predicate +
	 * "'(X,Y) :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT "+ a + column1
	 * + a + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " WHERE "+ a+column1+a + " = ? ', [X,Y]). \n"+ "'d" + ontologyDest
	 * +predicate + "'(X,Y) :- var(X), nonvar(Y),findall_odbc_sql([Y],'SELECT "+
	 * a + column1 + a + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+
	 * a+table+a + " WHERE "+ a+column2+a + " = ? ', [X,Y]). \n"); // "'d" +
	 * ontologyDest +predicate +
	 * "'(X,Y) :- var(X), var(Y), findall_odbc_sql([],'SELECT "+ a + column1 + a
	 * + ", "+ a + column2 + a + " FROM "+ a+db+a + "."+ a+table+a +
	 * " ', [X,Y]). \n"); out.close(); } catch (IOException e) {
	 * System.err.println("Mistake with createDataProperties.");
	 * e.printStackTrace(); } }
	 * 
	 * public static void createConcepts(String db, String table, String
	 * predicate, String column,File file,String ontology, String apostrophe){
	 * boolean isDL=!ontology.matches(""); String ontologyDest=""; String cl="";
	 * if(isDL){ ontologyDest=ontology; cl=">"; } String a="\\"+apostrophe; try
	 * { FileWriter out = new FileWriter (file,true); out.write("'a" +
	 * ontologyDest +predicate +
	 * ">'(X) :- nonvar(X),findall_odbc_sql([X],'SELECT "+ a + column + a +
	 * " FROM "+ a+db+a + "."+ a+table+a + " WHERE "+ a+column+a +
	 * " = ?', [X]). \n"+ "'a" + ontologyDest +predicate +
	 * ">'(X) :- var(X), findall_odbc_sql([],'SELECT "+ a + column + a +
	 * " FROM "+ a+db+a + "."+ a+table+a + " ', [X]). \n"+ "'d" + ontologyDest
	 * +predicate + ">'(X) :- nonvar(X), findall_odbc_sql([X],'SELECT "+ a +
	 * column + a + " FROM "+ a+db+a + "."+ a+table+a + " WHERE "+ a+column+a +
	 * " = ?', [X]). \n"+ "'d" + ontologyDest +predicate +
	 * ">'(X) :- var(X), findall_odbc_sql([],'SELECT "+ a + column + a +
	 * " FROM "+ a+db+a + "."+ a+table+a + " ', [X]). \n"); out.close(); } catch
	 * (IOException e) { System.err.println("Mistake with createConcepts.");
	 * e.printStackTrace(); } }
	 * 
	 */
	public void createObjectProperties(String db, String table1, String table2, String joinColumn1, String joinColumn2,
			String predicate, String column1, String column2, File file, String ontology, String apostrophe) {
		boolean isDL = !ontology.matches("");
		String ontologyDest = "";
		String cl = "";
		if (isDL) {
			ontologyDest = ontology;
			cl = ">";
		}
		String a = "\\" + apostrophe;
		try {
			FileWriter out = new FileWriter(file, true);
			out.write("'a<" + ontologyDest + predicate + ">'(X,Y) :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT "
					+ a + column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN "
					+ a + db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " WHERE " + a + column1 + a + " = ? AND " + a + column2
					+ a + " = ? ', [X,Y]). \n" + "'a<" + ontologyDest + predicate
					+ ">'(X,Y) :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT " + a + column1 + a + ", " + a
					+ column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " WHERE " + a + column1 + a + " = ?', [X,Y]). \n" + "'a<" + ontologyDest
					+ predicate + ">'(X,Y) :- var(X), nonvar(Y),findall_odbc_sql([Y],'SELECT " + a + column1 + a + ", "
					+ a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " WHERE " + a + column2 + a + " = ?', [X,Y]). \n" + "'a<" + ontologyDest
					+ predicate + ">'(X,Y) :- var(X), var(Y), findall_odbc_sql([],'SELECT " + a + column1 + a + ", " + a
					+ column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " ', [X,Y]). \n" + "'d<" + ontologyDest + predicate
					+ ">'(X,Y) :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT " + a + column1 + a + ", " + a
					+ column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " WHERE " + a + column1 + a + " = ? AND " + a + column2 + a
					+ " = ? ', [X,Y]). \n" + "'d<" + ontologyDest + predicate
					+ ">'(X,Y) :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT " + a + column1 + a + ", " + a
					+ column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " WHERE " + a + column1 + a + " = ? ', [X,Y]). \n" + "'d<" + ontologyDest
					+ predicate + ">'(X,Y) :- var(X), nonvar(Y), findall_odbc_sql([Y],'SELECT " + a + column1 + a + ", "
					+ a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " WHERE " + a + column2 + a + " = ? ', [X,Y]). \n" + "'d<" + ontologyDest
					+ predicate + ">'(X,Y) :- var(X), var(Y), findall_odbc_sql([],'SELECT " + a + column1 + a + ", " + a
					+ column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " ', [X,Y]). \n");
			out.close();
		} catch (IOException e) {
			System.err.println("Mistake with createDataProperties.");
			e.printStackTrace();
		}
	}

	@Override
	protected String tableDirective(Predicate pred) {
		return ":- table " + pred.accept(formatVisitor) + "/" + pred.getArity() + " as subsumptive.";
	}

}
