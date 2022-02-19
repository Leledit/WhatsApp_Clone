package leandro.caixeta.whatsapp_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.helper.Base64Custon;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class CadastroActiviry extends AppCompatActivity {

    private TextInputEditText edtNome ,  edtEmailC, edtSenhaC;
    private Button btn_cadastrar;
    private FirebaseAuth auth = ConfiguracaoFirebase.getAutha();
    private DatabaseReference database = ConfiguracaoFirebase.getDatase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_activiry);

        edtNome = findViewById(R.id.edtNome);
        edtEmailC = findViewById(R.id.edtEmailC);
        edtSenhaC = findViewById(R.id.edtSenhaC);
        btn_cadastrar = findViewById(R.id.btn_cadastrar);

        //Evento de click do botao "cadatrar"

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCadastrorUsuario(v);
            }
        });

    }

    public void validarCadastrorUsuario(View view){
        //pegando valores digitados pelo usuario
        String nome = edtNome.getText().toString();
        String email = edtEmailC.getText().toString();
        String senha = edtSenhaC.getText().toString();

        //realizando validaçoes dos dados
        if(!nome.isEmpty()){//verificando o campo nome
            if(!email.isEmpty()){//verificando o campo email
                if(!senha.isEmpty()){//verificando o campo senha
                    //cadastrando o usuario
                    Usuario usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    usuario.setNome(nome);
                    cadastrandosuarioFirebase(usuario);
                }else{
                    Toast.makeText(getApplicationContext(),"O campo Senha deve esta prenchido!!",Toast.LENGTH_LONG).show();

                }
            }else{
                Toast.makeText(getApplicationContext(),"O campo E-mail deve esta prenchido!!",Toast.LENGTH_LONG).show();

            }
        }else{
            Toast.makeText(getApplicationContext(),"O campo nome deve esta prenchido!!",Toast.LENGTH_LONG).show();
        }


    }


    public void cadastrandosuarioFirebase(Usuario usuario){
        auth.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Deu certo fazer o cadastro do usuario
                    Toast.makeText(getApplicationContext(),"Sucesso ao cadastrar o usuario!",Toast.LENGTH_LONG).show();
                    UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                    finish();

                    //Salvando os dados do usuario no banco de dados(Realtime database)

                    try {

                        String identificadoUsuario = Base64Custon.codificarBase64(usuario.getEmail());
                        usuario.setId(identificadoUsuario);
                        usuario.salvar();


                    }catch (Exception e){

                    }

                }else{
                    //deu algum erro no processo de cadastro

                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte !";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um E-mail valido";
                    }catch (FirebaseAuthUserCollisionException e) {
                        excecao = "conta ja cadastrar";
                    } catch (Exception e){
                        excecao = "Erro ao cadastrar o usuario:" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(),""+excecao,Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}