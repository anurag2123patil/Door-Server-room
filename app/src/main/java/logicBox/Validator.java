package logicBox;


public class Validator {

    //1.Validate email format
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    //This method will sanitize phone number fetched from phonebook,
    //this ensure desired format for phone number
    public final static String sanitizePhoneNumber(String phoneNumber) {

        String rawPhoneNumber = phoneNumber.replaceAll("\\s+", "");

        if (rawPhoneNumber.contains("+")) {
            rawPhoneNumber = rawPhoneNumber.replace("+", "");

        } else if (rawPhoneNumber.startsWith("0")) {
            rawPhoneNumber = rawPhoneNumber.replaceFirst("0", "91");

        } else if (!rawPhoneNumber.startsWith("+") && !rawPhoneNumber.startsWith("0") && rawPhoneNumber.length() == 10) {
            StringBuilder stringBuilder = new StringBuilder(rawPhoneNumber);
            stringBuilder.insert(0, "91");
            rawPhoneNumber = String.valueOf(stringBuilder);
        }
        return rawPhoneNumber;
    }
}
