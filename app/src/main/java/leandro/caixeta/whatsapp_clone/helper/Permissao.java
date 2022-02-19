package leandro.caixeta.whatsapp_clone.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {


    public static boolean validarPermissoes(String[] permissoes , Activity activity, int resquestcold){

        //verificando se a versao do app é maior que 23
        if(Build.VERSION.SDK_INT >= 23){

            //criando uma lista de arrays
            List<String> listaPermissoes  = new ArrayList<>();
            //*Percorre as permissoes passadas verificando uma a uma
            // se ja tem a permissao liberada */
            //no boolean abaixo é recuperado as permissoes(que vieram da acrivity que solicitou ela) e verificar se o
            //android ja possui elas(o usuario ja deu permissao)
            for(String permissao: permissoes){
               Boolean tempermissao = ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;
                if(!tempermissao)listaPermissoes.add(permissao);
            }
            /*Caso a lista esteja vazia, nao é necessario solicitar permissão*/
            if(listaPermissoes.isEmpty()) return true;
            String[] novaPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novaPermissoes);
            //solicitar permissao
            ActivityCompat.requestPermissions(activity,novaPermissoes,resquestcold);
        }
        return  true;
    }
}
