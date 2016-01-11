package com.clothapp.resources;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Roberto on 29/12/15.
 */
public class RegisterUtil {


    //funzione per settare background tint nel caso si usino le API 21
    public static void setButtonTint(Button button, ColorStateList tint) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
        } else {
            ViewCompat.setBackgroundTintList(button, tint);
        }
    }

    //funzione per controllare le 2 password siano uguali e non nulle
    public static boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean pstatus = true;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                pstatus = false;
            }
        }
        return pstatus;
    }
    //funzione per controllare che sia indirizzo mail valido
    public static boolean isValidEmailAddress(String email) {
        if (email == "") return false;
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //  it returns true if it is a valid birthday, else false
    public static boolean isValidBirthday (int day, int month, int year) {

        boolean flag = false;
        if(year < 1900 || year > 2015) return flag;
        if (day <= 0 || month <= 0) return flag;

        switch (month) {

            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                if (day <= 31) flag = true;
                break;

            case 4: case 6: case 9: case 11: if(day <= 30)
                flag = true;
                break;

            case 2:
                if(day <=28) flag = true;
                else if(isBisestle(year) && day == 29) flag = true;
                break;
        }

        return flag;
    }

    //   checking if the n parameter representing the year is bisestle
    //   it returns true if it is
    public static boolean isBisestle(int n) {
        if((n % 4 == 0 && n % 100 != 0) || n % 400 == 0) return true;
        return false;
    }

    //  checking pswd lenght is greater than 6
    public static boolean checkPswdLength(String a){
        return a.length() >= 6 && a.length() <= 12;
    }

    /*
 *  PASSWORD MUST CONTAIN:
 *  At least one capital letter, one non capital letter and one digit character.
 *  Special characters except dots are not allowed
 *
 *  check if the password is solid
 *  it returns:
 *   0 if everything is fine
 *  -1 if there are no capital letters
 *  -2 if there are no non capital letters
 *  -3 if there are no digits characters
 *  -4 if there space characters (tab new line ecc)
 *  -5 if there are other special characters (like comma, question mark ecc)
 */
    public static int passWordChecker (String input) {
        Pattern[] passwordRegexes = new Pattern[3];
        passwordRegexes[0] = Pattern.compile(".*[A-Z].*"); //   capital letters
        passwordRegexes[1] = Pattern.compile(".*[a-z].*"); //   non capital letters
        passwordRegexes[2] = Pattern.compile(".*\\d.*");   //   numbers

        for (int i = 0; i < passwordRegexes.length; i++) {
            if (!passwordRegexes[i].matcher(input).matches()) return -(i+1);
        }

        Pattern spacePattern = Pattern.compile(".*\\s.*"); //   tab, space, new line ecc
        if(spacePattern.matcher(input).matches()) return -(passwordRegexes.length+1);
        Pattern specialChars = Pattern.compile(".*[^a-zA-Z0-9].*"); //  special characters
        input.replaceAll(".","");
        if(specialChars.matcher(input).matches()) {
            return -(passwordRegexes.length+2);
        }
        return 0;
    }

    public static String cryptoPswd(String a){
        String finale = "";
        int length = a.length();
        for (int i = 0; i<length; i++){
            finale += (a.charAt(i)*13+11)%7;
        }
        return finale;
    }

    //TODO funzione che decripta la pswd
    public static String deCryptoPswd(String a){
        String finale = "";
        int length = a.length();
        return finale;
    }
}
