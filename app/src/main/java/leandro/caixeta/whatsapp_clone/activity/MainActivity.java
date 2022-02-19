package leandro.caixeta.whatsapp_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.fragment.ContatosFragment;
import leandro.caixeta.whatsapp_clone.fragment.ConversasFragment;


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
       /* toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");

        */
       // setActionBar(toolbar);
       // setSupportActionBar(toolbar);

        //Configurando as abas do menu
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add(R.string.conversas, ConversasFragment.class)
                        .add(R.string.contato, ContatosFragment.class)
                        .create()
        );
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout layout = findViewById(R.id.viewPagerTab);
        layout.setViewPager(viewPager);

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

             case R.id.menuConfiguracoes:
                 abriConfiguracoes();
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

     public void abriConfiguracoes(){
         startActivity(new Intent(this,configuracoes.class));
     }
 }