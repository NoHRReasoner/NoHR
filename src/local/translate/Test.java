package local.translate;

public class Test {
    public static void main(String[] args) throws Exception {
        Translate translate = new Translate("/Users/vadimivanov/Downloads/Tests2/complEquiv/complEquiv.owl");
        translate.proceed();
        translate.Finish();
        translate.clear();
        System.exit(0);
    }
}