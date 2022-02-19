package leandro.caixeta.whatsapp_clone.activity;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.helper.Base64Custon;
import leandro.caixeta.whatsapp_clone.helper.Permissao;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class configuracoes extends AppCompatActivity {

     private String[] permissoesNecessarias = new String[]{
             Manifest.permission.READ_EXTERNAL_STORAGE,
             Manifest.permission.CAMERA
     };

     private ImageView imgBGaleria,imgBCamera;
     private static final int SELECAO_CAMERA = 1;
     private static final int SELECAO_GALERIA = 2;
     private CircleImageView circleImageViewFotoPerfil;
     private StorageReference storageReference = ConfiguracaoFirebase.getStorage();
     private String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
     private EditText editNome ;
     private ImageView imgAtualizarNome;
     private Usuario usuarioLogado = new Usuario();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //ligando os componentes as suas respectivas variaveis
        imgBGaleria = findViewById(R.id.imgGaleria);
        imgBCamera = findViewById(R.id.imgCamera);
        circleImageViewFotoPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        editNome = findViewById(R.id.editNome);
        imgAtualizarNome = findViewById(R.id.imgAtualizarNome);


        //Alterando o titulo da actioBar
        getSupportActionBar().setTitle("Configurações");

        //Validando permissoes
        Permissao.validarPermissoes(permissoesNecessarias,this,1);

        //o codigo abaixo serve para configurar o botao de voltar(so vai fucionar se a
        //activityr for filha de outra activity)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Receuperar dados do usuario
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        Uri url =  user.getPhotoUrl();
        if(url != null){
            Glide.with(configuracoes.this).load(url).into(circleImageViewFotoPerfil);
        }else{
            circleImageViewFotoPerfil.setImageResource(R.drawable.padrao);
        }

        //setando os dados do usuario no bd
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        editNome.setText(user.getDisplayName());

        //adicionando os eventos de click para os imgViews
        imgBCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,SELECAO_CAMERA);
                }
            }
        });

        imgBGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(intent,SELECAO_GALERIA);


                }
            }
        });

        imgAtualizarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome  = editNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);
               if(retorno){
                    usuarioLogado.setNome(nome);
                    usuarioLogado.atualizar();
                    Toast.makeText(configuracoes.this, "Nome alterado com sucesso", Toast.LENGTH_SHORT).show();
                }




              //  Toast.makeText(configuracoes.this, "Result: "+retorno, Toast.LENGTH_SHORT).show();
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

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);
                        break;
                }

                if(imagem != null){
                    circleImageViewFotoPerfil.setImageBitmap(imagem);

                    //Recuperando dados da imagem para o firebase
                    ByteArrayOutputStream inputStream = new ByteArrayOutputStream ();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70,inputStream);
                    byte[] dadosImagem = inputStream.toByteArray();

                    //salvando a imagem no firebase
                  final StorageReference imgRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuario)
                            .child("perfil.jpg");

                    UploadTask uploadTask = imgRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(configuracoes.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(configuracoes.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();

                                    atualizaFotoUsuario(url);
                                }
                            });

                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
        }
    }

    public void atualizaFotoUsuario(Uri url){

      boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if(retorno) {
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
            Toast.makeText(this, "Sua foto foi alterada", Toast.LENGTH_SHORT).show();
        }
    }
    //metodo resposnavel por tratar as permissoes
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                //chamando metodo que ira tratar as permissoes negativas
                alertaPermissao();
            }
        }

    }

    private void alertaPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this );
        builder.setTitle("Permissoes Negadas");
        builder.setCancelable(false);
        builder.setMessage("Para ultilizar o app é necessario aceitar as permissoes");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}