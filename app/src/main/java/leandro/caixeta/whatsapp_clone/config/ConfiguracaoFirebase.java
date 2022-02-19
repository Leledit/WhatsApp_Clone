package leandro.caixeta.whatsapp_clone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference datase;
    private static FirebaseAuth auth;
    private static StorageReference storage;

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

    public static StorageReference getStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }



}
