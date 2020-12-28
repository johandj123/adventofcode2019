public class Day4 {

    private static final int MIN = 156218;
    private static final int MAX = 652527;

    public static void main(String[] args) {
        int count1 = 0;
        int count2 = 0;
        for (int i = MIN; i <= MAX; i++) {
            String s = Integer.toString(i);
            if (nonDecreasing(s) && duplicateExists(s)) {
                count1++;
            }
            if (nonDecreasing(s) && singleDuplicateExists(s)) {
                count2++;
            }
        }
        System.out.println("First: " + count1);
        System.out.println("Second: " + count2);
    }

    private static boolean nonDecreasing(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i + 1) < s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean duplicateExists(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean singleDuplicateExists(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1) &&
                    (i == 0 || s.charAt(i - 1) != s.charAt(i)) &&
                    (i == s.length() - 2 || s.charAt(i + 1) != s.charAt(i + 2))) {
                return true;
            }
        }
        return false;
    }
}
