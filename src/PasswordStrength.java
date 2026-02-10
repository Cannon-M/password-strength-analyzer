import java.util.Scanner;

public class PasswordStrength {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Password Strength Checker");
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();

        int strengthScore = 0;

        strengthScore += lengthScore(password);
        strengthScore += characterTypeScore(password);

        if (hasRepeatedPattern(password)) {
            strengthScore -= 1;
            System.out.println("Repeated pattern detected (weakens password)");
        }

        strengthScore += randomnessScore(password);

        System.out.println("\nPassword: " + password);
        System.out.println("Strength Score: " + strengthScore);
        System.out.println("Strength Level: " + strengthLevel(strengthScore));

        double crackSeconds = estimateCrackTime(password);
        System.out.println("Estimated Time to Crack (brute-force): " 
            + formatTime(crackSeconds));
        

        double bits = entropy(password);                  // declare and assign
        System.out.printf("Entropy: %.2f bits (%s)%n", bits, entropyLevel(bits));
        scanner.close();
    }

    // ---------- Length ----------
    static int lengthScore(String password) {
        int length = password.length();

        if (length >= 24) return 5;
        if (length >= 20) return 4;
        if (length >= 16) return 3;
        if (length >= 12) return 2;
        if (length >= 8)  return 1;

        return 0;
    }

    // ---------- Character Types ----------
    static int characterTypeScore(String password) {
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        int score = 0;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;

        return score;
    }

    // ---------- Pool Size ----------
    static int characterPoolSize(String password) {
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        int pool = 0;
        if (hasLower) pool += 26;
        if (hasUpper) pool += 26;
        if (hasDigit) pool += 10;
        if (hasSpecial) pool += 32;

        return pool;
    }

    // ---------- Crack Time ------------
    static double estimateCrackTime(String password) {
        int poolsize = characterPoolSize(password);
        int length = password.length();

        if (poolsize == 0 || length == 0) {
            return 0;
        }

        double combinations = Math.pow(poolsize, length);
        double guessespersec = 1_000_000_000.0; //Guesses Per Second

        return (combinations / 2) / guessespersec;
    }

    // --------- Format Time -------------
    static String formatTime(double seconds) {
    if (seconds < 1) return "Instantly";

    double minutes = seconds / 60;
    double hours = minutes / 60;
    double days = hours / 24;
    double years = days / 365;

    if (years >= 1) return String.format("%.2f years", years);
    if (days >= 1) return String.format("%.2f days", days);
    if (hours >= 1) return String.format("%.2f hours", hours);
    if (minutes >= 1) return String.format("%.2f minutes", minutes);

    return String.format("%.2f seconds", seconds);
    }


    // ---------- Repeated Patterns ----------
    static boolean hasRepeatedPattern(String password) {
        int length = password.length();

        for (int size = 1; size <= length / 2; size++) {
            if (length % size != 0) continue;

            String part = password.substring(0, size);
            if (password.equals(part.repeat(length / size))) {
                return true;
            }
        }
        return false;
    }

    // ---------- Randomness ----------
    static int randomnessScore(String password) {
        int score = 0;
        int length = password.length();

        // Character frequency check
        int[] freq = new int[128];
        for (int i = 0; i < length; i++) {
            freq[password.charAt(i)]++;
        }

        for (int count : freq) {
            if (count > length / 3) {
                score -= 2;
                break;
            }
        }

        // Sequential characters check (abc, 123)
        for (int i = 0; i < length - 2; i++) {
            char a = password.charAt(i);
            char b = password.charAt(i + 1);
            char c = password.charAt(i + 2);

            if (b == a + 1 && c == b + 1) {
                score -= 2;
                break;
            }
        }

        // Unique character ratio
        boolean[] seen = new boolean[128];
        int uniqueCount = 0;

        for (int i = 0; i < length; i++) {
            char c = password.charAt(i);
            if (!seen[c]) {
                seen[c] = true;
                uniqueCount++;
            }
        }

        double ratio = (double) uniqueCount / length;

        if (ratio > 0.7) score += 2;
        else if (ratio < 0.4) score -= 1;

        return score;
    }

    // ---------- Final Rating ----------
    static String strengthLevel(int score) {
        if (score < 6) return "Weak";
        if (score <= 10) return "Medium";
        return "Strong";
    }

    static double entropy(String password) {
        int poolsize = characterPoolSize(password);
        int length = password.length();
        
        if (poolsize == 0 || length == 0) return 0.0;

        return length * (Math.log(poolsize)) / (Math.log(2));

    }

    static String entropyLevel(double bits) {
    if (bits < 40) return "Very Weak";
    if (bits < 60) return "Weak";
    if (bits < 80) return "Moderate";
    if (bits < 100) return "Strong";
    return "Very Strong";
    }

}