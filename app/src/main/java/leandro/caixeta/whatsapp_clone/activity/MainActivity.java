package leandro.caixeta.whatsapp_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;


//import com.example.yourcontrol.databinding.ActivityMainBinding

 //import leandro.caixeta.whatsapp_clone.databinding.ActivityMainBinding;
public class MainActivity extends AppCompatActivity {

//*ActivityMainBinding/
   // private ActivityMainBinding binding;

     private Toolbar toolbar;
     private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getAutha();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configurando a toolbar
        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");
       // setActionBar(toolbar);
       // setSupportActionBar(toolbar);

    }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.menu,menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

         switch (item.getItemId()){
             case R.id.menuSair :
                 deslogarUsuario();
                 finish();
                 break;

         }

         return super.onOptionsItemSelected(item);
     }

     public void deslogarUsuario(){
        try {
            firebaseAuth.signOut();
        }catch (Exception e){

        }

     }
 }