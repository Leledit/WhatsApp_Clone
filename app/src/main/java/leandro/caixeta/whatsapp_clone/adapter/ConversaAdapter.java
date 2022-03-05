package leandro.caixeta.whatsapp_clone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import leandro.caixeta.whatsapp_clone.R;
import leandro.caixeta.whatsapp_clone.model.Conversa;
import leandro.caixeta.whatsapp_clone.model.Grupo;
import leandro.caixeta.whatsapp_clone.model.Usuario;

public class ConversaAdapter extends RecyclerView.Adapter<ConversaAdapter.MyViewholder> {


    private List<Conversa> conversas;
    private Context context;

    public ConversaAdapter(List<Conversa> list, Context c) {

        this.conversas = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos,parent,false);

        return new MyViewholder(itemLista);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {

        Conversa conversa = conversas.get(position);
        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        //Verificando se a conversa é normal(de pessoa para pessoa) ou se é uma conversa em grupo
        if(conversa.getIsGrup().equals("true")){
            Grupo grupo = conversa.getGrupo();
            holder.nome.setText(grupo.getNome());
            if(grupo.getFoto() != null){
                Uri uri = Uri.parse(grupo.getFoto());
                Glide.with(context).load(uri).into(holder.foto);
            }else{
                holder.foto.setImageResource(R.drawable.padrao);
            }

        }else{
            Usuario usuario = conversa.getUsuarioExibicao();
            if(usuario != null){
                holder.nome.setText(usuario.getNome());
                if(usuario.getFoto() != null){
                    Uri uri = Uri.parse(usuario.getFoto());
                    Glide.with(context).load(uri).into(holder.foto);
                }else{
                    holder.foto.setImageResource(R.drawable.padrao);
                }
            }

        }






    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {
        CircleImageView foto;
        TextView nome,ultimaMensagem;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome = itemView.findViewById(R.id.textViewNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.textViewEmailContato);
        }
    }



}
