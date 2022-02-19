package leandro.caixeta.whatsapp_clone.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Usuario() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getDatase();
        DatabaseReference usuariRef = reference.child("usuarios").child(getId());

        usuariRef.setValue(this);
    }
    public  void atualizar(){
        String identifiadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getDatase();
        DatabaseReference usuarioRef = database.child("usuarios").child(identifiadorUsuario);

        //é usado como parametro uma funçao que retorna um map contendo os dados od usuario
        //para poder atualizar no banco de dados
        usuarioRef.updateChildren(converteMap());
    }
    @Exclude
    public Map<String,Object> converteMap(){
        HashMap<String,Object> usuarioMap = new HashMap<>();

        usuarioMap.put("email",getEmail());
        usuarioMap.put("nome",getNome());
        usuarioMap.put("foto",getFoto());

        return  usuarioMap;
    }
}
