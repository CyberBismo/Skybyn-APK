package social.app.wesocial;

import android.util.Patterns;

import static android.util.Patterns.EMAIL_ADDRESS;

public class Functions {

    public Boolean isJsonObject(String json){
        return json.startsWith("{");

        }

    public Boolean isJsonArray(String json){
        return json.startsWith("[");

    }

    public Boolean validateEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
