package leandro.caixeta.whatsapp_clone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.adapter.MensagensAdapter;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.databinding.ActivityChatBinding;
import leandro.caixeta.whatsapp_clone.helper.Base64Custon;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Conversa;
import leandro.caixeta.whatsapp_clone.model.Grupo;
import leandro.caixeta.whatsapp_clone.model.Mensagem;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class ChatActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;
    private TextView textViewNomeChate;
    private ImageView circleImagemFotoChat;
    private Usuario usuarioDestinatario;
    private EditText editTextTextPersonName;
    private ImageView imageCamera;
    private FloatingActionButton floatButonEnviar;
    private RecyclerView recyclerViewMensagens ;
    private MensagensAdapter mensagensAdapter;
    private List<Mensagem> mensagemsList = new ArrayList<>();
    private DatabaseReference referenceBase = ConfiguracaoFirebase.getDatase();
    private DatabaseReference mensagnesRef;
    private ChildEventListener childEventListenerMensagens ;
    private static final int SELECAO_CAMERA = 1;
    private StorageReference storageReference  =ConfiguracaoFirebase.getStorage();
    private Grupo grupo;


    //identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_chat);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //adicionando a seta de voltar
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);



       //configuraçoes iniciais
        binding.toolbar.setTitle("");
        textViewNomeChate = findViewById(R.id.textViewNomeChate);
        circleImagemFotoChat = findViewById(R.id.circleImagemFotoChat);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        floatButonEnviar = findViewById(R.id.floatButonEnviar);
        recyclerViewMensagens = findViewById(R.id.recyclerViewMensagens);
        imageCamera = findViewById(R.id.imageCamera);
        grupo = new Grupo();

        //Recuperando os dados do usuario destinatario
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
           if(bundle.containsKey("chatGrupo")){

               grupo = (Grupo) bundle.getSerializable("chatGrupo");
               idUsuarioDestinatario = grupo.getId();
               textViewNomeChate.setText(grupo.getNome());


               String foto = grupo.getFoto();
               if(foto != null){
                   Uri uri =Uri.parse(foto);
                   Glide.with(ChatActivity.this).load(uri).into(circleImagemFotoChat);
               }else{
                   circleImagemFotoChat.setImageResource(R.drawable.padrao);
               }

           }else{
               usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
               textViewNomeChate.setText(usuarioDestinatario.getNome());


               String foto = usuarioDestinatario.getFoto();
               if(foto != null){
                   Uri uri =Uri.parse(foto);
                   Glide.with(ChatActivity.this).load(uri).into(circleImagemFotoChat);
               }else{
                   circleImagemFotoChat.setImageResource(R.drawable.padrao);
               }
               //recuperando os dados do usuario destinatario
               idUsuarioDestinatario = Base64Custon.codificarBase64(usuarioDestinatario.getEmail());
           }
        }

        //recuperando os dados do usuario Remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();

        //chamando metodo resposnavel por enviar a mensagem
        floatButonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensagem();
            }
        });

        //Configurando o adapter
        mensagensAdapter = new MensagensAdapter(mensagemsList,getApplicationContext());

        //Configurando o RecycleView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewMensagens.setLayoutManager(layoutManager);
        recyclerViewMensagens.setHasFixedSize(true);
        recyclerViewMensagens.setAdapter(mensagensAdapter);



        mensagnesRef = referenceBase.child("mensagens").child(idUsuarioRemetente ).child(idUsuarioDestinatario);

        //evento de clique da camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,SELECAO_CAMERA);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                }
                if(imagem != null){
                    //Recuperando dados da imagem para o firebase
                    ByteArrayOutputStream inputStream = new ByteArrayOutputStream ();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70,inputStream);
                    byte[] dadosImagem = inputStream.toByteArray();

                    //Criando o nome da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //Configurar a referencia do firebase
                    StorageReference imagemRef = storageReference.child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente).child(nomeImagem);
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = task.getResult().toString();
                                    Mensagem mensagem = new Mensagem();
                                    mensagem.setIdUsuario(idUsuarioRemetente);
                                    mensagem.setMensagem("imagem.jpeg");
                                    mensagem.setImagem(url);

                                    //Salvando mensagem remetente
                                    salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);
                                    //Salvando mensagem para o destinatario
                                    salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);
                                    Toast.makeText(ChatActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_chat);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //Metodo responsavel por enviar a imagem
    public void enviarMensagem(){
        String mensagem = editTextTextPersonName.getText().toString();

        if(!mensagem.isEmpty()){

            if(usuarioDestinatario != null){
                Mensagem msg = new Mensagem();
                msg.setIdUsuario(idUsuarioRemetente);
                msg.setMensagem(mensagem);
                //chamando metodo responsavel por salvar a mensagem para o remetente
                salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,msg);
                //chamando metodo responsavel por salvar a mensagem para o Destinatario
                salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente ,msg);

                //Salvando uma conversa
                salvarConversa(msg,false);
            }else{

                for(Usuario membro: grupo.getMembros()){

                    String idRemetenteGrupo = Base64Custon.codificarBase64(membro.getEmail());
                    String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
                    Mensagem msg = new Mensagem();
                    msg.setIdUsuario(idRemetenteGrupo);
                    msg.setMensagem(mensagem);

                    salvarMensagem(idRemetenteGrupo,idUsuarioDestinatario,msg);

                    //Salvando uma conversa
                    salvarConversa(msg,true);

                }
            }



        }else{
            Toast.makeText(this, "Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();
        }
    }
    private void salvarConversa(Mensagem msg,boolean isGrup){

        //Salvando conversa em grupo
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());

        if(isGrup){//conversa em grupo

            //Conversa de grupo
            conversaRemetente.setIsGrup("true");
            conversaRemetente.setGrupo(grupo);


        }else{//conversa entre duas pessoas
            //Conversa convencional
            conversaRemetente.setUsuarioExibicao(usuarioDestinatario );
            conversaRemetente.setIsGrup("false");

        }
        conversaRemetente.salvar();


    }

    private void salvarMensagem(String idRemetente,String idDestinatario , Mensagem mensagem){

        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatase();
        DatabaseReference mensagemRef = databaseReference.child("mensagens");
        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(mensagem);
        //limpar o texto
        editTextTextPersonName.setText("");
    }

    private void recuperarMensagens(){
        childEventListenerMensagens = mensagnesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagemsList.add(mensagem);
                mensagensAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //recuperando as mensagens no banco de dados
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagnesRef.removeEventListener(childEventListenerMensagens);
    }
}