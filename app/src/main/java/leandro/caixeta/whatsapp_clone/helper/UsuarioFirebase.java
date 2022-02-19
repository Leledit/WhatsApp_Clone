package leandro.caixeta.whatsapp_clone.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class UsuarioFirebase {

    public static  String getIdentificadorUsuario(){

        FirebaseAuth auth = ConfiguracaoFirebase.getAutha();
        String email = auth.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custon.codificarBase64(email);

        return identificadorUsuario;
    }
    //Metodo que retorna o usuario que esta ativo atualmente no app
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth auth = ConfiguracaoFirebase.getAutha();
        return auth.getCurrentUser();
    }

    public static boolean atualizarFotoUsuario(Uri url){
       try {
           //dever ser pego o usuario que esta ativo atualmente no app
           FirebaseUser user = getUsuarioAtual();
           //criando um perfil para o usuario(para poder passar a url da foto dele)
           UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(url).build();
           user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if(!task.isSuccessful()){
                       Log.d("perfil","Erro ao atualizar a foro de perfil");
                   }
               }
           });
           return true;
       }catch (Exception e){
           e.printStackTrace();
           return  false;
       }
    }//fechamento do metodo atualizarFotoUsuario

    public static boolean atualizarNomeUsuario(String nome){
        try {
            //dever ser pego o usuario que esta ativo atualmente no app
            FirebaseUser user = getUsuarioAtual();
            //criando um perfil para o usuario(para poder passar a url da foto dele)
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("perfil","Erro ao atualizar o nome de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }//fechamento do metodo atualizarFotoUsuario

    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser user = getUsuarioAtual();
        Usuario usuario = new Usuario() ;
        usuario.setEmail(user.getEmail());
        usuario.setNome(user.getDisplayName());

        //Verificando se o usuario nao possui foto
        if(user.getPhotoUrl() == null){
            usuario.setFoto("");
        }else{
            usuario.setFoto(user.getPhotoUrl().toString());
        }

        return usuario;
    }
}
