package exception;

public class TryCatchObj {
    public static void main(String[] args) {
        int n = getValueFromFinally(0);
        System.out.println("n is " + n);

    }

    public static int getValueFromFinally(int n) {
        int ret;
        try {
            ret = 2 / n;
            return ret;
        } catch (Exception e) {
            ret = 10;
            return ret;
        } finally {
            ret = 100;

            return ret;
        }

    }
}
