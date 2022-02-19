package leandro.caixeta.whatsapp_clone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.helper.UsuarioFirebase;
import leandro.caixeta.whatsapp_clone.model.Mensagem;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViweHolder> {

    private List<Mensagem> mensagems;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static  final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> lista , Context c) {
        this.mensagems = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;
        if(viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente,parent,false);

        }else if(viewType == TIPO_DESTINATARIO){
           item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario,parent,false);
        }
        return new MyViweHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViweHolder holder, int position) {
        Mensagem msg = mensagems.get(position);

        String mensagem = msg.getMensagem();
        String imagem = msg.getImagem();

     if (imagem != null){

         Uri uri = Uri.parse(imagem);
         Glide.with(context).load(uri).into(holder.imagem);
         holder.mensagem.setVisibility(View.GONE);

        }else{
         holder.mensagem.setText(mensagem);
         holder.imagem.setVisibility(View.GONE);
     }



    }

    @Override
    public int getItemCount() {
        return mensagems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem msg = mensagems.get(position);
        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        if(idUsuario.equals(msg.getIdUsuario())){
            return TIPO_REMETENTE;
        }
        return TIPO_DESTINATARIO;
    }

    public class MyViweHolder extends RecyclerView.ViewHolder{

        TextView mensagem;
        ImageView imagem;

         public MyViweHolder(View itemview){

             super(itemview);
             mensagem = itemview.findViewById(R.id.TextMensagemTexto);
             imagem = itemview.findViewById(R.id.imageMensgemFoto);
        }
    }


}
