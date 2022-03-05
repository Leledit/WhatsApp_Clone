package leandro.caixeta.whatsapp_clone.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;
import leandro.caixeta.whatsapp_clone.helper.Base64Custon;

public class Grupo  implements Serializable {

    private String id;
    private String nome;
    private String foto;
    private List<Usuario>membros;

    public Grupo() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatase();
        DatabaseReference grupoRef = databaseReference.child("grupos");

        String idGrupo = grupoRef.push().getKey();
        setId(idGrupo);
    }

    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatase();
        DatabaseReference grupoRef = databaseReference.child("grupos");

        grupoRef.child(getId()).setValue(this);

        //Salvando conversa para membros do grupo
        for(Usuario membros :getMembros()){

            String idRemetente = Base64Custon.codificarBase64(membros.getEmail());
            String idDestinatario = getId();

            Conversa conversa = new Conversa();
            conversa.setIdRemetente(idRemetente);
            conversa.setIdDestinatario(idDestinatario);
            conversa.setUltimaMensagem("");
            conversa.setIsGrup("true");
            conversa.setGrupo(this);

            conversa.salvar();

        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }
}
