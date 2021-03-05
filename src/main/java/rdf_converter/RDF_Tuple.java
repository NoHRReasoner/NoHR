package rdf_converter;

public interface RDF_Tuple {

    String getSubject();

    String getPredicate();

    String getObject();

    void setSubjectVariable();

    void setObjectVariable();

    boolean hasQuestionMark(String variable);



}
