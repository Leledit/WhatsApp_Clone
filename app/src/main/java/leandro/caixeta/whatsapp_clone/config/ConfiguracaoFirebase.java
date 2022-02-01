package leandro.caixeta.whatsapp_clone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static DatabaseReference datase;
    private static FirebaseAuth auth;

    //metodo que retorna a instancia do FirebaseDatabase


    //metodo que retorna a instancia do FirebaseAuth
    public static FirebaseAuth getAutha(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static DatabaseReference getDatase(){
        if(datase == null){
            datase = FirebaseDatabase.getInstance().getReference();
        }
        return  datase;
    }



}
