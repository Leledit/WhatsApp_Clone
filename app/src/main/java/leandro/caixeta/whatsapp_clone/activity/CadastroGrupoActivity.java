package leandro.caixeta.whatsapp_clone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.adapter.ContatoAdapter;
import leandro.caixeta.whatsapp_clone.adapter.GrupoSelecionandoAdapter;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.databinding.ActivityCadastroGrupoBinding;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Grupo;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class CadastroGrupoActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCadastroGrupoBinding binding;
    private List<Usuario> listaMenbros   = new ArrayList<>();
    private TextView textTotalParticipantes;
    private EditText editNomeGrupo;
    private CircleImageView imgGrupo;
    private RecyclerView recycleViewParticipantes;
    private GrupoSelecionandoAdapter grupoSelecionandoAdapter;
    private static final int SELECAO_GALERIA = 2;
    private StorageReference storageReference = ConfiguracaoFirebase.getStorage();
    private Grupo grupo ;
    private FloatingActionButton fabCadastrarGrupo ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastroGrupoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_cadastro_grupo);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //Configurando a seta de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuraçoes iniciais
        textTotalParticipantes = findViewById(R.id.textTotalParticipantes);
        editNomeGrupo = findViewById(R.id.editNomeGrupo);
        imgGrupo = findViewById(R.id.imgGrupo);
        recycleViewParticipantes = findViewById(R.id.recycleViewParticipantes);
        grupo = new Grupo();
        fabCadastrarGrupo = findViewById(R.id.fabCadastrarGrupo);


        //Recuperar lista de membros passada
        if(getIntent().getExtras() != null){
            List<Usuario> membros =(List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listaMenbros.addAll(membros);
        }

        //setando a quantidade de participantes que faram parte do grupo
        textTotalParticipantes.setText("Participantes: "+listaMenbros.size());

        //Alterando o titulo e o subtitulo da toolbar
        binding.toolbar.setTitle("Novo grupo");
        binding.toolbar.setSubtitle("Defina o nome");

        //Configurar recycleView para os membroSelecionados
        grupoSelecionandoAdapter = new GrupoSelecionandoAdapter(listaMenbros,getApplicationContext());
        RecyclerView.LayoutManager layoutManagerMembros = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recycleViewParticipantes.setLayoutManager(layoutManagerMembros);
        recycleViewParticipantes.setHasFixedSize( true);
        recycleViewParticipantes.setAdapter(grupoSelecionandoAdapter);


        imgGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,SELECAO_GALERIA);
                }
            }
        });

        fabCadastrarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeGrupo = editNomeGrupo.getText().toString();

                //Adicionando o usuario atual(logado) na lista de participantes do grupo
                listaMenbros.add(UsuarioFirebase.getDadosUsuarioLogado());
                grupo.setMembros(listaMenbros);

                grupo.setNome(nomeGrupo);
                grupo.salvar();

                Intent i = new Intent(CadastroGrupoActivity.this, ChatActivity.class);
                i.putExtra("chatGrupo",grupo);
                startActivity(i);


            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_cadastro_grupo);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            Bitmap imagem = null;

            try {
                Uri localImagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);

                //Verificando se a imagem nao esta nula
                if(imagem != null) {
                    imgGrupo.setImageBitmap(imagem);

                    //Recuperando dados da imagem para o firebase
                    ByteArrayOutputStream inputStream = new ByteArrayOutputStream ();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70,inputStream);
                    byte[] dadosImagem = inputStream.toByteArray();

                    //salvando a imagem no firebase
                    final StorageReference imgRef = storageReference
                            .child("imagens")
                            .child("grupos")
                            .child(grupo.getId()+"jpeg");

                    UploadTask uploadTask = imgRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroGrupoActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CadastroGrupoActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = task.getResult().toString();
                                    grupo.setFoto(url);
                                }
                            });

                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}