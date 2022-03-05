package leandro.caixeta.whatsapp_clone.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.adapter.ContatoAdapter;
import leandro.caixeta.whatsapp_clone.adapter.GrupoSelecionandoAdapter;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.databinding.ActivityGrupoBinding;
import leandro.caixeta.whatsapp_clone.helper.RecyclerItemClickListener;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class GrupoActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityGrupoBinding binding;

    private RecyclerView recycleMembrosSelecionados,recycleMembros;
    private ContatoAdapter contatoAdapter;
    private GrupoSelecionandoAdapter grupoSelecionandoAdapter;
    private List<Usuario> listaMenbros   = new ArrayList<>();
    private List<Usuario> listaMenbrosSelecionados = new ArrayList<>();
    private ValueEventListener eventListenerMembros;
    private DatabaseReference usuarioRef ;
    private FirebaseUser userAtual ;

    private FloatingActionButton fabeAvancarCadastro;


    public void atualizarMenbrosToolbar(){

        int totalSelecionado = listaMenbrosSelecionados.size();
        int total =listaMenbros.size() + totalSelecionado;

        binding.toolbar.setSubtitle(totalSelecionado+ " de " +total+" selecionados");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGrupoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_grupo);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //Alterando o titulo da toolbar
        binding.toolbar.setTitle("Novo grupo");

        //Configurando a seta de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuraçoes iniciais
        recycleMembros = findViewById(R.id.recycleMembros);
        recycleMembrosSelecionados = findViewById(R.id.recycleMembrosSelecionados);
        usuarioRef = ConfiguracaoFirebase.getDatase().child("usuarios");
        userAtual = UsuarioFirebase.getUsuarioAtual();
        fabeAvancarCadastro = findViewById(R.id.fabCadastrarGrupo);

        //configurando o adapter

        contatoAdapter = new ContatoAdapter(listaMenbros,getApplicationContext());

        //Configurar recycleView para os contatos
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycleMembros.setLayoutManager(layoutManager);
        recycleMembros.setHasFixedSize( true);
        recycleMembros.setAdapter(contatoAdapter);

        //Configurar recycleView para os membroSelecionados
        grupoSelecionandoAdapter = new GrupoSelecionandoAdapter(listaMenbrosSelecionados,getApplicationContext());
        RecyclerView.LayoutManager layoutManagerMembros = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recycleMembrosSelecionados.setLayoutManager(layoutManagerMembros);
        recycleMembrosSelecionados.setHasFixedSize( true);
        recycleMembrosSelecionados.setAdapter(grupoSelecionandoAdapter);

        recycleMembrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recycleMembrosSelecionados, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMenbrosSelecionados.get(position);

                //Remover da listagem de membros selecionados
                listaMenbrosSelecionados.remove(usuarioSelecionado);
                grupoSelecionandoAdapter.notifyDataSetChanged();

                //Adicionar a listagem de membros
                listaMenbros.add(usuarioSelecionado);
                contatoAdapter.notifyDataSetChanged();


                //chamando metodo que atualiza a quantidade de membros que vao estar no grupo
                atualizarMenbrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));


        recycleMembros.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recycleMembros, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Usuario usuarioSelecionado = listaMenbros.get(position);

                //Removendo usuario selecionado da lista
                listaMenbros.remove(usuarioSelecionado);


                //adicionando usuario na nova lista de selecionados
                listaMenbrosSelecionados.add(usuarioSelecionado);

                contatoAdapter.notifyDataSetChanged();
                grupoSelecionandoAdapter.notifyDataSetChanged();

                //chamando metodo que atualiza a quantidade de membros que vao estar no grupo
                atualizarMenbrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));


        //evento de click do floatActionButton
        fabeAvancarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GrupoActivity.this,CadastroGrupoActivity.class);
                intent.putExtra("membros",(Serializable) listaMenbrosSelecionados);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_grupo);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContato();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(eventListenerMembros);
    }

    public void recuperarContato(){
        listaMenbros.clear();

        eventListenerMembros = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);

                    String emailUsuairoAtual = userAtual.getEmail();
                    if(!emailUsuairoAtual.equals(usuario.getEmail())){
                        listaMenbros.add(usuario);
                    }

                }



                contatoAdapter.notifyDataSetChanged();
                //chamando metodo que atualiza a quantidade de membros que vao estar no grupo
                atualizarMenbrosToolbar();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}