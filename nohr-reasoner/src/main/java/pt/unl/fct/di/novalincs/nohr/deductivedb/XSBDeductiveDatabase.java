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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;

import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.Term;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRScanner;
import pt.unl.fct.di.novalincs.nohr.parsing.TokenType;
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

		vedran();
//		 giannis();

		if (!prologEngine.load_dynAbsolute(file.getAbsoluteFile()))
			throw new IPException("file not loaded");
	}

	public void vedran() {

		File dest = new File("/home/vedran/Desktop/rules.P");
//		File dest = new File("C:\\Users\\VedranPC\\Desktop\\rules.P");
		
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
			out.write(//"?- odbc_open('oracle preprod fi','NOC_USER','noc_user').\n" + ":- table 'doneconn'/2.\n"
					 "?-odbc_open('oracle_fi','NOC_USER','CrnbwXyaBMUVOh').\n" +
					// ":- table 'doneconn'/2.\n"
					":- table 'aoneconn'/2.\n"
					+ ":- table 'aactiveConnection'/2.\n");
			out.close();
		} catch (IOException e) {
			System.err.println("Mistake!");
			e.printStackTrace();
		}
		CreatingMappings mapRule = new CreatingMappings("\"", "NOC_USER",
				"http://www.semanticweb.org/gerochrisi/ontologies/2017/4/inventory-ontology#");

		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "Z_VENDOR" },
				new boolean[] { true, false }, "z_vendor", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "ER_TYPE" },
				new boolean[] { true, false }, "er_type", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "A_NETYPE" },
				new boolean[] { true, false }, "a_netype", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "Z_PHYSICALPORTID" }, new boolean[] { true, true },
				"z_physicalportid", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "Z_NETYPE" },
				new boolean[] { true, false }, "z_netype", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "A_PHYSICALPORTID" }, new boolean[] { true, true },
				"a_physicalportid", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "CONNECTION_TRAIL_INST_ID" }, new boolean[] { true, true },
				"connection_trail_inst_id", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "A_MEK" },
				new boolean[] { true, false }, "a_mek", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "A_VENDOR" },
				new boolean[] { true, false }, "a_vendor", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV",
				new String[] { "CONNECTION_TRAIL_INST_ID", "LOGICALRESOURCENAME" }, new boolean[] { true, false },
				"logicalresourcename", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID", "Z_MEK" },
				new boolean[] { true, false }, "z_mek", true, file, ontologyDest, "\"");

		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "ADDRESS" },
				new boolean[] { true, false }, "address", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "SITEIST" },
				new boolean[] { true, true }, "siteist", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "SITEMEK" },
				new boolean[] { true, false }, "sitemek", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "LAT" },
				new boolean[] { true, false }, "lat", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "LONGT" },
				new boolean[] { true, false }, "longt", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "CLLI" },
				new boolean[] { true, false }, "location_clli", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "CONTACTS" },
				new boolean[] { true, false }, "contacts", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "POST_CODE_1" },
				new boolean[] { true, false }, "post_code_1", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "CITY" },
				new boolean[] { true, false }, "city", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "STATE_PROV" },
				new boolean[] { true, false }, "state_prov", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "COUNTRY" },
				new boolean[] { true, false }, "country", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "STATUS" },
				new boolean[] { true, false }, "location_status", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "LAST_MOD_TS" },
				new boolean[] { true, false }, "last_mod_ts", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST", "LAST_MOD_BY" },
				new boolean[] { true, false }, "last_mod_by", true, file, ontologyDest, "\"");

		// mapRule.createPredicateMapping("PT_NODES_MV",
		// new String[] {"HOLDERCOMPOSITEID", "HOLDERCOMPOSITE_INST_ID" },
		// new boolean[] { false, true }, "holdercomposite_inst_id", true, file,
		// ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "PLACEID_INST_ID" },
				new boolean[] { true, true }, "placeid_inst_id", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "CLLI" },
				new boolean[] { true, false }, "node_clli", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "MODEL" },
				new boolean[] { true, false }, "model", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "HOLDERCOMPOSITEID" },
				new boolean[] { true, false }, "holdercompositeid", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV",
				new String[] { "HOLDERCOMPOSITE_INST_ID", "PHYSICALRESOURCEROLENAME" }, new boolean[] { true, false },
				"physicalresourcerolename", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "STATUS" },
				new boolean[] { true, false }, "node_status", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "LAST_MODIFIED_TS" },
				new boolean[] { true, false }, "last_modified_ts", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "LAST_MODIFIED_BY" },
				new boolean[] { true, false }, "last_modified_by", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "VENDOR" },
				new boolean[] { true, false }, "vendor", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID", "SITE" },
				new boolean[] { true, true }, "site", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_NODES_MV",
				new String[] { "HOLDERCOMPOSITE_INST_ID", "HOLDERCOMPOSITE_INST_ID" }, new boolean[] { true, true },
				"holdercomposite_inst_id", true, file, ontologyDest, "\"");

		// typeConn
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "A_MEK", "Z_MEK", "A_NETYPE", "Z_NETYPE" },
				new boolean[] { false, false, false, false }, "typeConn", false, file, "", "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "Z_MEK", "A_MEK", "Z_NETYPE", "A_NETYPE" },
				new boolean[] { false, false, false, false }, "typeConn", false, file, "", "\"");

		// vendorConn
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "A_MEK", "Z_MEK", "A_VENDOR", "Z_VENDOR" },
				new boolean[] { false, false, false, false }, "vendorConn", false, file, "", "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "Z_MEK", "A_MEK", "Z_VENDOR", "A_VENDOR" },
				new boolean[] { false, false, false, false }, "vendorConn", false, file, "", "\"");

		mapRule.createPredicateMapping("PT_NODES_MV", new String[] { "HOLDERCOMPOSITE_INST_ID" },
				new boolean[] { false }, "Node", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_LOCATIONS_MV", new String[] { "SITEIST" }, new boolean[] { false },
				"Location", true, file, ontologyDest, "\"");
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "CONNECTION_TRAIL_INST_ID" },
				new boolean[] { true }, "Connection", true, file, ontologyDest, "\"");

		// mapRule.createPredicateMapping("PT_NODES_MV", new String[] {
		// "HOLDERCOMPOSITE_INST_ID", "SITE" },
		// new boolean[] { false, true }, "isLocated", true, file, ontologyDest,
		// "\"");

		createObjectProperties("NOC_USER", "PT_NODES_MV", "PT_LOCATIONS_MV", "SITE", "SITEIST", "isLocated",
				"HOLDERCOMPOSITE_INST_ID", "SITEIST", file, ontologyDest, "\"");

		createObjectProperties2("activeConnection", file, ontologyDest);
		
		mapRule.createPredicateMapping("PT_CONNECTIONS_MV", new String[] { "A_MEK", "Z_MEK" },
				new boolean[] { false, false }, "isConnected", true, file, ontologyDest, "\"");

		try {
			FileUtils.copyFile(file, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
			out.write("'a<" + ontologyDest + predicate
					+ ">'(X,Y) :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT " + a + column1 + a + ", " + a
					+ column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a + db + a + "." + a
					+ table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a + table2 + a + "." + a
					+ joinColumn2 + a + " WHERE " + a + column1 + a + " = ? AND " + a + column2 + a
					+ " = ? ', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n" +

					"'a<" + ontologyDest + predicate + ">'(X,Y) :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT " + a
					+ column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a
					+ db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " WHERE " + a + column1 + a
					+ " = ?', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n" +

					"'a<" + ontologyDest + predicate + ">'(X,Y) :- var(X), nonvar(Y),findall_odbc_sql([Y],'SELECT " + a
					+ column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a
					+ db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " WHERE " + a + column2 + a
					+ " = ?', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n" +

					"'a<" + ontologyDest + predicate + ">'(X,Y) :- var(X), var(Y), findall_odbc_sql([],'SELECT " + a
					+ column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a
					+ db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " ', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n" +

					"'d<" + ontologyDest + predicate + ">'(X,Y) :- nonvar(X),nonvar(Y),findall_odbc_sql([X,Y],'SELECT "
					+ a + column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN "
					+ a + db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " WHERE " + a + column1 + a + " = ? AND " + a + column2
					+ a + " = ? ', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n" +

					"'d<" + ontologyDest + predicate + ">'(X,Y) :- nonvar(X), var(Y), findall_odbc_sql([X],'SELECT " + a
					+ column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a
					+ db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " WHERE " + a + column1 + a
					+ " = ? ', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n" +

					"'d<" + ontologyDest + predicate + ">'(X,Y) :- var(X), nonvar(Y), findall_odbc_sql([Y],'SELECT " + a
					+ column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a
					+ db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " WHERE " + a + column2 + a
					+ " = ? ', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n" +

					"'d<" + ontologyDest + predicate + ">'(X,Y) :- var(X), var(Y), findall_odbc_sql([],'SELECT " + a
					+ column1 + a + ", " + a + column2 + a + " FROM " + a + db + a + "." + a + table1 + a + " JOIN " + a
					+ db + a + "." + a + table2 + a + " ON " + a + table1 + a + "." + a + joinColumn1 + a + "=" + a
					+ table2 + a + "." + a + joinColumn2 + a + " ', [X1,Y1]), X is floor(X1), Y is floor(Y1). \n");
			out.close();
		} catch (IOException e) {
			System.err.println("Mistake with createDataProperties.");
			e.printStackTrace();
		}
	}

	public void createObjectProperties2(String predicate, File file, String ontology) {
		boolean isDL = !ontology.matches("");
		String ontologyDest = "";
		String cl = "";
		if (isDL) {
			ontologyDest = ontology;
			cl = ">";
		}
		try {
			FileWriter out = new FileWriter(file, true);
			out.write("'a" + predicate + "'(X) :- var(X), findall_odbc_sql([X],'"
					+ "SELECT \"PT_CONNECTIONS_MV\".\"CONNECTION_TRAIL_INST_ID\" FROM "
					+ "\"NOC_USER\".\"PT_CONNECTIONS_MV\" JOIN \"NOC_USER\".\"PT_NODES_MV\" "
					+ "\"a_nodes\" ON \"PT_CONNECTIONS_MV\".\"A_MEK\"=\"a_nodes\".\"HOLDERCOMPOSITEID\" "
					+ "JOIN \"NOC_USER\".\"PT_NODES_MV\" \"z_nodes\" ON \"PT_CONNECTIONS_MV\".\"Z_MEK\"=\"z_nodes\".\"HOLDERCOMPOSITEID\" "
					+ "WHERE \"PT_CONNECTIONS_MV\".\"CONNECTION_TRAIL_INST_ID\" = ? and \"a_nodes\".\"STATUS\"=\\'In Service\\' AND \"z_nodes\".\"STATUS\"=\\'In Service\\'"
					+ " ', [X1]), X is floor(X1). \n" +

					"'a" + predicate + "'(X) :- var(X), findall_odbc_sql([],'"
					+ "SELECT \"PT_CONNECTIONS_MV\".\"CONNECTION_TRAIL_INST_ID\" FROM \"NOC_USER\".\"PT_CONNECTIONS_MV\" JOIN \"NOC_USER\".\"PT_NODES_MV\" \"a_nodes\" ON \"PT_CONNECTIONS_MV\".\"A_MEK\"=\"a_nodes\".\"HOLDERCOMPOSITEID\" JOIN \"NOC_USER\".\"PT_NODES_MV\" \"z_nodes\" ON \"PT_CONNECTIONS_MV\".\"Z_MEK\"=\"z_nodes\".\"HOLDERCOMPOSITEID\" WHERE \"a_nodes\".\"STATUS\"=\\'In Service\\' AND \"z_nodes\".\"STATUS\"=\\'In Service\\'"
					+ " ', [X1]), X is floor(X1). \n"+
					"'d" + predicate + "'(X) :- var(X), findall_odbc_sql([X],'"
					+ "SELECT \"PT_CONNECTIONS_MV\".\"CONNECTION_TRAIL_INST_ID\" FROM "
					+ "\"NOC_USER\".\"PT_CONNECTIONS_MV\" JOIN \"NOC_USER\".\"PT_NODES_MV\" "
					+ "\"a_nodes\" ON \"PT_CONNECTIONS_MV\".\"A_MEK\"=\"a_nodes\".\"HOLDERCOMPOSITEID\" "
					+ "JOIN \"NOC_USER\".\"PT_NODES_MV\" \"z_nodes\" ON \"PT_CONNECTIONS_MV\".\"Z_MEK\"=\"z_nodes\".\"HOLDERCOMPOSITEID\" "
					+ "WHERE \"PT_CONNECTIONS_MV\".\"CONNECTION_TRAIL_INST_ID\" = ? and \"a_nodes\".\"STATUS\"=\\'In Service\\' AND \"z_nodes\".\"STATUS\"=\\'In Service\\'"
					+ " ', [X1]), X is floor(X1). \n" +

					"'d" + predicate + "'(X) :- var(X), findall_odbc_sql([],'"
					+ "SELECT \"PT_CONNECTIONS_MV\".\"CONNECTION_TRAIL_INST_ID\" FROM \"NOC_USER\".\"PT_CONNECTIONS_MV\" JOIN \"NOC_USER\".\"PT_NODES_MV\" \"a_nodes\" ON \"PT_CONNECTIONS_MV\".\"A_MEK\"=\"a_nodes\".\"HOLDERCOMPOSITEID\" JOIN \"NOC_USER\".\"PT_NODES_MV\" \"z_nodes\" ON \"PT_CONNECTIONS_MV\".\"Z_MEK\"=\"z_nodes\".\"HOLDERCOMPOSITEID\" WHERE \"a_nodes\".\"STATUS\"=\\'In Service\\' AND \"z_nodes\".\"STATUS\"=\\'In Service\\'"
					+ " ', [X1]), X is floor(X1). \n");
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
