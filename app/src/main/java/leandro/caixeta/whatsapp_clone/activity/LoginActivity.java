package leandro.caixeta.whatsapp_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmailL, edtSenhaL;
    private Button btn_login ;
    private FirebaseAuth auth = ConfiguracaoFirebase.getAutha();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmailL = findViewById(R.id.edtEmailL);
        edtSenhaL = findViewById(R.id.edtSenhaL);
        btn_login = findViewById(R.id.btn_login);

        //criando evento do botao "login"
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chamando metodo de validação
                validarDados(v);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = auth.getCurrentUser();
        if(usuarioAtual != null){
            abriTelaprincipal();
        }
    }



    public void validarDados(View v){

        //pegando os dados escrito pelo usuario
        String email = edtEmailL.getText().toString();
        String senha = edtSenhaL.getText().toString();



        if(!email.isEmpty()){//verificando o campo email
            if(!senha.isEmpty()){//verificando o campo senha
                //cadastrando o usuario
                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);
                logandoUsuario(usuario);
            }else{
                Toast.makeText(getApplicationContext(),"O campo Senha deve esta prenchido!!",Toast.LENGTH_LONG).show();

            }
        }else{
            Toast.makeText(getApplicationContext(),"O campo E-mail deve esta prenchido!!",Toast.LENGTH_LONG).show();

        }

    }



    public void logandoUsuario(Usuario usuario){

        auth.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //caso o login seja realizado com sucesso
                    abriTelaprincipal();
                    finish();
                }else{
                    //caso tenha ocorrido algum erro
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha nao correspodem a um usuario cadastrado";
                    }catch (FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuario nao cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar o usuario: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(),excecao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void abriTelaCadastro(View view){
        startActivity(new Intent(getApplicationContext(),CadastroActiviry.class));
    }
    public void abriTelaprincipal(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}