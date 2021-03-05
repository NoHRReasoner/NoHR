package rdf_converter;

public class RDF_TupleImp implements RDF_Tuple {

    private String subject;
    private String predicate;
    private String object;

    public RDF_TupleImp(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public String getObject() {
        return object;
    }


    @Override
    public void setSubjectVariable() {
        subject="?"+subject;
    }

    @Override
    public void setObjectVariable() {
        object="?"+object;
    }

    @Override
    public boolean hasQuestionMark(String variable) {
        return variable.indexOf('?') != -1;
    }
}
