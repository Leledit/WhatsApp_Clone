package leandro.caixeta.whatsapp_clone.model;

import com.google.firebase.database.DatabaseReference;

import leandro.caixeta.whatsapp_clone.config.ConfiguracaoFirebase;

public class Conversa {
    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;
    private String isGrup;
    private  Grupo grupo;

    public Conversa() {
        this.setIsGrup("false");
    }
    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getDatase();
        DatabaseReference conversaRef = reference.child("conversas");
        conversaRef.child(this.getIdRemetente())
                .child(getIdDestinatario())
                .setValue(this);
    }

    public String getIsGrup() {
        return isGrup;
    }

    public void setIsGrup(String isGrup) {
        this.isGrup = isGrup;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
}
